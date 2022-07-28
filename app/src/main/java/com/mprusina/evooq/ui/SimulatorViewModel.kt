package com.mprusina.evooq.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mprusina.evooq.data.DrugRepository
import com.mprusina.evooq.data.PatientRepository
import com.mprusina.evooq.data.entities.Drug
import com.mprusina.evooq.data.entities.Patient
import com.mprusina.hospital_lib.DataSymbolToNameMap
import com.mprusina.hospital_lib.HospitalSimulator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
 Using one ViewModel across all fragments because:
    1. We're working with same data across all fragments
    2. Data must by synced between fragments (updated patients state)
    3. Logic in all fragments is pretty much identical
        3.1. Get patient and/or drug data
        3.2. Display patient and/or drug data
        3.3. Select patient(s) and drug(s) for administration
        3.4. Display updated patient(s) state
 */
@HiltViewModel
class SimulatorViewModel @Inject constructor(private val drugRepository: DrugRepository, private val patientRepository: PatientRepository): ViewModel() {

    // For storing fetched patient data
    private val _patientData = MutableLiveData<List<Patient>>()
    private val patientData: LiveData<List<Patient>> = _patientData

    // For storing fetched drug data
    private val _drugData = MutableLiveData<List<Drug>>()
    private val drugData: LiveData<List<Drug>> = _drugData

    // Used for both patient and drug lists to display server error message if error happens
    private val fetchPatientDrugDataResponseError = MutableLiveData<Boolean>()
    // Used for drug list in PatientDetailsFragment to display no data error message if no data is received
    private val fetchDrugDataResponseError = MutableLiveData<Boolean>()

    // Storing list of Pair<Patient, List<Drug>> to administer drugs to multiple patients
    private val patientsDrugsPairList = mutableListOf<Pair<Patient, List<Drug>>>()

    // Storing selected patient from list, to display patient's details in PatientDetailsFragment. Using LiveData so that details are updated on UI after drug administration
    private val _selectedPatient = MutableLiveData<Patient>()
    private val selectedPatient: LiveData<Patient> = _selectedPatient

    // Storing selected drugs for administration, used to create Pair<Patient, List<Drug>>
    private val selectedDrugs = mutableListOf<Drug>()

    private val hospitalSimulator: HospitalSimulator by lazy {
        HospitalSimulator()
    }

    fun fetchPatientAndDrugData() {
        clearAll()
        viewModelScope.launch {
            runCatching {
                _patientData.postValue(patientRepository.getPatients())
                _drugData.postValue(drugRepository.getDrugs())
            }.onFailure {
                fetchPatientDrugDataResponseError.postValue(true)
            }.onSuccess {
                fetchPatientDrugDataResponseError.postValue(false)
            }
        }
    }

    fun fetchDrugData() {
        clearAll()
        viewModelScope.launch {
            runCatching {
                _drugData.postValue(drugRepository.getDrugs())
            }.onFailure {
                fetchDrugDataResponseError.postValue(true)
            }.onSuccess {
                fetchDrugDataResponseError.postValue(false)
            }
        }
    }

    fun getPatients(): LiveData<List<Patient>> {
        return patientData
    }

    fun getDrugs(): LiveData<List<Drug>> {
        return drugData
    }

    // Return property containing result of server request, to update list's UI depending on result
    fun isFetchPatientDrugDataResponseResultError(): LiveData<Boolean> {
        return fetchPatientDrugDataResponseError
    }

    // Return property containing result of server request, to update PatientDetailsFragment's drug list UI depending on result
    fun isFetchDrugDataResponseResultError(): LiveData<Boolean> {
        return fetchDrugDataResponseError
    }

    // Find and return selected patient in patient data and return it for display in details fragment
    fun getPatientWithId(patientId: Int): LiveData<Patient> {
        _selectedPatient.value = patientData.value?.find {
            it.id == patientId
        }
        return selectedPatient
    }

    fun updateSelectedDrugs(drug: Drug) {
        val drugSelected: Boolean
        if (selectedDrugs.contains(drug)) {
            drugSelected = false
            selectedDrugs.remove(drug)
        } else {
            drugSelected = true
            selectedDrugs.add(drug)
        }
        _drugData.value?.filter {
            it.id == drug.id
        }?.forEach {
            it.isSelected = drugSelected
        }
    }

    fun clearAll() {
        clearSelectedDrugs()
        clearPatientDrugsPairList()
    }

    private fun clearSelectedDrugs() {
        _drugData.value?.forEach {
            it.isSelected = false
        }
        selectedDrugs.clear()
    }

    private fun clearPatientDrugsPairList() {
        patientsDrugsPairList.clear()
    }

    fun createPatientDrugsPair(patient: Patient) {
        var exists = false
        val patientDrugsPair = patient to selectedDrugs.toList()
        for (existingPair in patientsDrugsPairList) {
            if (existingPair.first == patient) {
                patientsDrugsPairList.remove(existingPair)
                patientsDrugsPairList.add(patientDrugsPair)
                exists = true
                break
            }
        }
        if (!exists) {
            patientsDrugsPairList.add(patientDrugsPair)
        }
        clearSelectedDrugs()
    }

    /*
    If drugs were previously selected for a patient (through drug dialog fragment) and dialog is opened again for the same patient,
    update "isSelected" value for previously selected drugs to properly display checkboxes and collect all drugs together for administration
     */
    fun checkIfPreviouslySelectedDrugsExistForPatientWithId(patientId: Int) {
        val patientDrugPair = patientsDrugsPairList.find {
            it.first.id == patientId
        }
        if (patientDrugPair != null) {
            for (selectedDrug in patientDrugPair.second) {
                _drugData.value?.filter {
                    it.id == selectedDrug.id
                }?.forEach {
                    it.isSelected = true
                }
            }
            selectedDrugs.addAll(patientDrugPair.second)
        }
    }

    // Administer drugs to multiple patients (drug administration from Patient/Drug list fragments
    fun administerDrugsToAllPatients() {
        val updatedPatientDataList = _patientData.value!!

        addNotSelectedDiabeticPatients()

        for (pair in patientsDrugsPairList) {
            val currentPatient = pair.first
            val drugsForAdministration = mutableListOf<String>()
            for (drug in pair.second) {
                drugsForAdministration.add(drug.drugSymbol)
            }
            val newPatientState = hospitalSimulator.computeDrugAdministrationSimulation(currentPatient.stateSymbol, drugsForAdministration)
            val newPatientStateName = DataSymbolToNameMap.patientSymbolToNameMap[newPatientState]!!
            val updatedPatient = Patient(currentPatient.id, newPatientState, newPatientStateName)
            updatedPatientDataList.filter {
                it.id == updatedPatient.id
            }.forEach {
                it.stateSymbol = updatedPatient.stateSymbol
                it.stateName = updatedPatient.stateName
            }
        }
        _patientData.value = updatedPatientDataList
        clearAll()
    }

    // Administer drugs to single patient (drug administration from Patient details fragment
    fun administerDrugsToPatient() {
        val currentPatient = selectedPatient.value
        val drugsForAdministration = mutableListOf<String>()
        for (drug in selectedDrugs) {
            drugsForAdministration.add(drug.drugSymbol)
        }
        if (currentPatient != null) {
            val newPatientState = hospitalSimulator.computeDrugAdministrationSimulation(currentPatient.stateSymbol, drugsForAdministration)
            val newPatientStateName = DataSymbolToNameMap.patientSymbolToNameMap[newPatientState]!!
            val updatedPatient = Patient(currentPatient.id, newPatientState, newPatientStateName)
            _selectedPatient.value = updatedPatient
            _patientData.value?.filter {
                it.id == updatedPatient.id
            }?.forEach {
                it.stateSymbol = updatedPatient.stateSymbol
                it.stateName = updatedPatient.stateName
            }
        }
        clearAll()
    }

    /*
    As Diabetic patients HAVE to receive Insulin in order not to die,
    if diabetic patient is not selected for drug administration, we have to change state to X (Dead)
     */
    private fun addNotSelectedDiabeticPatients() {
        val notSelectedDiabeticPatients = mutableListOf<Patient>()

        val allDiabeticPatients = _patientData.value!!.filter {
            it.stateName == DataSymbolToNameMap.patientSymbolToNameMap["D"]
        }

        for (dPatient in allDiabeticPatients) {
            var exists = false
            for (pair in patientsDrugsPairList) {
                if (dPatient == pair.first) {
                    exists = true
                }
            }
            if (!exists) {
                notSelectedDiabeticPatients.add(dPatient)
            }
        }

        for (diabeticPatient in notSelectedDiabeticPatients) {
            val diabeticPatientPair = diabeticPatient to emptyList<Drug>()
            patientsDrugsPairList.add(diabeticPatientPair)
        }
    }
}