package com.mprusina.evooq

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.mprusina.evooq.data.DrugRepository
import com.mprusina.evooq.data.DrugRepositoryImpl
import com.mprusina.evooq.data.PatientRepository
import com.mprusina.evooq.data.PatientRepositoryImpl
import com.mprusina.evooq.data.api.DrugService
import com.mprusina.evooq.data.api.PatientService
import com.mprusina.evooq.ui.SimulatorViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
class SimulatorViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var drugService: DrugService

    @Mock
    private lateinit var patientService: PatientService

    private lateinit var drugRepository: DrugRepository
    private lateinit var patientRepository: PatientRepository
    private lateinit var viewModel: SimulatorViewModel

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(mainThreadSurrogate)
        drugRepository = DrugRepositoryImpl(drugService)
        patientRepository = PatientRepositoryImpl(patientService)
        viewModel = SimulatorViewModel(drugRepository, patientRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun `Given response ok When fetchPatientAndDrugData() is called Then patient data is not null or empty`() {
        runBlocking {
            val patientData = "H, F"
            `when`(patientService.getPatients()).thenReturn(patientData)
            viewModel.fetchPatientAndDrugData()
        }
        val patients = viewModel.getPatients().getOrAwaitValue()
        println(patients)
        assertNotNull(patients)
        assert(patients.isNotEmpty())
    }

    @Test
    fun `Given response ok When fetchPatientAndDrugData() is called Then drug data is not null or empty`() {
        runBlocking {
            val patientData = "H, F" // Have to mock patient response, otherwise test will fail, as getDrugs() is called after getPatients()
            val drugData = "As, P"
            `when`(patientService.getPatients()).thenReturn(patientData)
            `when`(drugService.getDrugs()).thenReturn(drugData)
            viewModel.fetchPatientAndDrugData()
            delay(1000)
        }
        val drugs = viewModel.getDrugs().getOrAwaitValue()
        assert(drugs.isNotEmpty())
        assertNotNull(drugs)
    }

    @Test
    fun `Given response data is empty When fetchPatientAndDrugData() is called Then patient data is empty`() {
        runBlocking {
            val patientData = ""
            val drugData = ""
            `when`(patientService.getPatients()).thenReturn(patientData)
            `when`(drugService.getDrugs()).thenReturn(drugData)
            viewModel.fetchPatientAndDrugData()
        }
        val patientResponse = viewModel.getPatients().getOrAwaitValue()
        assert(patientResponse.isEmpty())
    }

    @Test
    fun `Given response data is empty When fetchPatientAndDrugData() is called Then drug data is empty`() {
        runBlocking {
            val patientData = ""
            val drugData = ""
            `when`(patientService.getPatients()).thenReturn(patientData)
            `when`(drugService.getDrugs()).thenReturn(drugData)
            viewModel.fetchPatientAndDrugData()
        }
        val drugResponse = viewModel.getDrugs().getOrAwaitValue()
        assert(drugResponse.isEmpty())
    }

    @Test
    fun `Given server ok When fetchPatientAndDrugData() is called Then fetchPatientDrugDataResponseError value is false`() {
        runBlocking {
            val patientData = "H, F"
            val drugData = "As, P"
            `when`(patientService.getPatients()).thenReturn(patientData)
            `when`(drugService.getDrugs()).thenReturn(drugData)
            viewModel.fetchPatientAndDrugData()
        }
        val response = viewModel.isFetchPatientDrugDataResponseResultError().getOrAwaitValue()
        assertFalse(response)
    }

    @Test
    fun `Given server error When fetchPatientAndDrugData() is called Then fetchPatientDrugDataResponseError value is true`() {
        runBlocking {
            // since we didn't mock Drug Service and Patient Service responses, and server is not running, request will fail
            viewModel.fetchPatientAndDrugData()
        }
        val response = viewModel.isFetchPatientDrugDataResponseResultError().getOrAwaitValue()
        assert(response)
    }

    @Test
    fun `Given response ok When fetchDrugData() is called Then drug data is not null or empty`() {
        runBlocking {
            val drugData = "As, P"
            `when`(drugService.getDrugs()).thenReturn(drugData)
            viewModel.fetchDrugData()
        }
        val drugs = viewModel.getDrugs().getOrAwaitValue()
        assertNotNull(drugs)
        assert(drugs.isNotEmpty())
    }

    @Test
    fun `Given response data is empty When fetchDrugData() is called Then drug data is empty`() {
        runBlocking {
            val patientData = ""
            val drugData = ""
            `when`(patientService.getPatients()).thenReturn(patientData)
            `when`(drugService.getDrugs()).thenReturn(drugData)
            viewModel.fetchDrugData()
        }
        val drugResponse = viewModel.getDrugs().getOrAwaitValue()
        assert(drugResponse.isEmpty())
    }

    @Test
    fun `Given server ok When fetchDrugData() is called Then fetchDrugDataResponseError value is false`() {
        runBlocking {
            val drugData = "As, P"
            `when`(drugService.getDrugs()).thenReturn(drugData)
            viewModel.fetchDrugData()
        }
        val response = viewModel.isFetchDrugDataResponseResultError().getOrAwaitValue()
        assertFalse(response)
    }

    @Test
    fun `Given server error When fetchDrugData() is called Then fetchDrugDataResponseError value is true`() {
        runBlocking {
            // since we didn't mock Drug Service and Patient Service responses, and server is not running, request will fail
            viewModel.fetchDrugData()
        }
        val response = viewModel.isFetchDrugDataResponseResultError().getOrAwaitValue()
        assert(response)
    }

    @Test
    fun `Given patient data exists When getPatientWithId() is called Then fun returns patient with provided id`() {
        runBlocking {
            val patientData = "H, F"
            val drugData = "As, P"
            `when`(patientService.getPatients()).thenReturn(patientData)
            `when`(drugService.getDrugs()).thenReturn(drugData)
            viewModel.fetchPatientAndDrugData()
            delay(2000) // delaying for patientData to be updated before continuing test
        }
        val patients = viewModel.getPatientWithId(1).getOrAwaitValue()
        assertEquals("Healthy", patients.stateName)
    }

    private fun <T> LiveData<T>.getOrAwaitValue(time: Long = 5, timeUnit: TimeUnit = TimeUnit.SECONDS): T {
        var data: T? = null
        val countdown = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(o: T?) {
                data = o
                countdown.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }

        this.observeForever(observer)

        if (!countdown.await(time, timeUnit)) {
            throw TimeoutException("LiveData value not set.")
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }
}