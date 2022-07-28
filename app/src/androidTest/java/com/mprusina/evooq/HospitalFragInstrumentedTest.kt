package com.mprusina.evooq

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.tabs.TabLayout
import com.mprusina.evooq.ui.MainActivity
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HospitalFragInstrumentedTest {

    // For all tests except "whenGetDataButtonClicked_andServerErrorHappens_thenCheckThatServerErrorMessage_isDisplayed",
    // server must be running - with more time I would implement MockWebServer to mock server responses

    @get:Rule
    var mainActivityRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun whenAppStarted_thenCheckThatPatientList_isDisplayed () {
        onView(withId(R.id.patient_list)).check(matches(isDisplayed()))
        onView(withId(R.id.main_button_get_data)).check(matches(isDisplayed()))
        onView(withId(R.id.main_button_administer_drugs)).check(matches(isDisplayed()))
    }

    @Test
    fun whenPatientTabSelected_thenCheckThatPatientList_isDisplayed () {
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(0))
        onView(withId(R.id.patient_list)).check(matches(isDisplayed()))
    }

    @Test
    fun whenDrugTabSelected_thenCheckThatDrugList_isDisplayed() {
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(1))
        onView(withId(R.id.drug_list)).check(matches(isDisplayed()))
    }

    @Test
    fun whenGetDataButtonClicked_thenCheckThatLoadingIndicator_isDisplayed() {
        onView(withId(R.id.main_button_get_data)).perform(click())
        onView(withId(R.id.loading_indicator)).check(matches(isDisplayed()))
    }

    @Test
    fun whenGetDataButtonClicked_thenCheckThatDataInPatientList_isDisplayed() {
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(0))
        onView(withId(R.id.main_button_get_data)).perform(click())
        onView(allOf(withText("Patient:"), hasSibling(withText("1")))).check(matches(isDisplayed()))
    }

    @Test
    fun whenGetDataButtonClicked_andPatientListItemIsClicked_thenCheckThatPatientDetailsView_isDisplayed() {
        onView(withId(R.id.main_button_get_data)).perform(click())
        onView(allOf(withText("Patient:"), hasSibling(withText("1")))).perform(click())
        onView(withId(R.id.tv_details_patient_id_label)).check(matches(isDisplayed()))
        onView(withId(R.id.details_button_administer_drugs)).check(matches(isDisplayed()))
    }

    @Test
    fun whenGetDataButtonClicked_andServerErrorHappens_thenCheckThatServerErrorMessage_isDisplayed() {
        onView(withId(R.id.main_button_get_data)).perform(click())
        Thread.sleep(10000)
        onView(withId(R.id.tv_server_error_message)).check(matches(isDisplayed()))
    }

    private fun selectTabAtPosition(tabIndex: Int): ViewAction {
        return object : ViewAction {
            override fun getDescription() = "With tab at index $tabIndex"
            override fun getConstraints() = allOf(isDisplayed(), isAssignableFrom(TabLayout::class.java))
            override fun perform(uiController: UiController, view: View) {
                val tabLayout = view as TabLayout
                val tabAtIndex: TabLayout.Tab = tabLayout.getTabAt(tabIndex)
                    ?: throw PerformException.Builder()
                        .withCause(Throwable("No tab at index $tabIndex"))
                        .build()
                tabAtIndex.select()
            }
        }
    }
}