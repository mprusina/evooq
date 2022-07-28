package com.mprusina.hospital_lib

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class HospitalSimulatorTest {

    private lateinit var simulator: HospitalSimulator

    @Before
    fun setUp() {
        simulator = HospitalSimulator()
    }

    @Test
    fun givenPatientHasFever_whenAspirinIsAdministered_thenPatientIsHealthy() {
        val patient = "F"
        val drugs = listOf("As")
        val newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("H", newPatientState)
    }

    @Test
    fun givenPatientHasFever_whenAntibioticIsAdministered_thenPatientStateUnchanged() {
        val patient = "F"
        val drugs = listOf("An")
        val newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("F", newPatientState)
    }

    @Test
    fun givenPatientHasFever_whenInsulinIsAdministered_thenPatientStateUnchanged() {
        val patient = "F"
        val drugs = listOf("I")
        val newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("F", newPatientState)
    }

    @Test
    fun givenPatientHasFever_whenParacetamolIsAdministered_thenPatientIsHealthy() {
        val patient = "F"
        val drugs = listOf("P")
        val newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("H", newPatientState)
    }

    @Test
    fun givenPatientIsHealthy_whenAspirinIsAdministered_thenPatientIsHealthy() {
        val patient = "H"
        val drugs = listOf("As")
        val newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("H", newPatientState)
    }

    @Test
    fun givenPatientIsHealthy_whenAntibioticIsAdministered_thenPatientIsHealthy() {
        val patient = "H"
        val drugs = listOf("An")
        val newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("H", newPatientState)
    }

    @Test
    fun givenPatientIsHealthy_whenInsulinIsAdministered_thenPatientIsHealthy() {
        val patient = "H"
        val drugs = listOf("I")
        val newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("H", newPatientState)
    }

    @Test
    fun givenPatientIsHealthy_whenParacetamolIsAdministered_thenPatientIsHealthy() {
        val patient = "H"
        val drugs = listOf("P")
        val newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("H", newPatientState)
    }

    @Test
    fun givenPatientHasDiabetes_whenAspirinIsAdministered_thenPatientIsDead() {
        val patient = "D"
        val drugs = listOf("As")
        val newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("X", newPatientState)
    }

    @Test
    fun givenPatientHasDiabetes_whenAntibioticIsAdministered_thenPatientIsDead() {
        val patient = "D"
        val drugs = listOf("An")
        val newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("X", newPatientState)
    }

    @Test
    fun givenPatientHasDiabetes_whenInsulinIsAdministered_thenPatientStateUnchanged() {
        val patient = "D"
        val drugs = listOf("I")
        val newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("D", newPatientState)
    }

    @Test
    fun givenPatientHasDiabetes_whenParacetamolIsAdministered_thenPatientIsDead() {
        val patient = "D"
        val drugs = listOf("P")
        val newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("X", newPatientState)
    }

    @Test
    fun givenPatientHasTuberculosis_whenAspirinIsAdministered_thenPatientStateUnchanged() {
        val patient = "T"
        val drugs = listOf("As")
        val newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("T", newPatientState)
    }

    @Test
    fun givenPatientHasTuberculosis_whenAntibioticIsAdministered_thenPatientIsHealthy() {
        val patient = "T"
        val drugs = listOf("An")
        val newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("H", newPatientState)
    }

    @Test
    fun givenPatientHasTuberculosis_whenInsulinIsAdministered_thenPatientStateUnchanged() {
        val patient = "T"
        val drugs = listOf("I")
        val newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("T", newPatientState)
    }

    @Test
    fun givenPatientHasTuberculosis_whenParacetamolIsAdministered_thenPatientStateUnchanged() {
        val patient = "T"
        val drugs = listOf("P")
        val newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("T", newPatientState)
    }

    @Test
    fun givenPatientHasAnyState_whenAspirinAndParacetamolAreAdministered_thenPatientIsDead() {
        val drugs = listOf("P", "As")
        var patient = "H"
        var newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("X", newPatientState)

        patient = "F"
        newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("X", newPatientState)

        patient = "D"
        newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("X", newPatientState)

        patient = "T"
        newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("X", newPatientState)
    }

    @Test
    fun givenPatientHasDiabetes_whenNoInsulinIsAdministered_thenPatientIsDead() {
        val drugs = listOf("As", "An", "P")
        val patient = "D"
        val newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("X", newPatientState)
    }

    @Test
    fun givenPatientIsHealthy_whenAntibioticAndInsulinAreAdministered_thenPatientHasFever() {
        val drugs = listOf("An", "I")
        val patient = "H"
        val newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("F", newPatientState)
    }

    @Test
    fun givenPatientHasFever_whenAspiringAndThenInsulinAndAntibioticAreAdministered_thenPatientIsHealthy() {
        // Verify that state is changed only once
        val drugs = listOf("As", "An", "I")
        val patient = "F"
        val newPatientState = simulator.computeDrugAdministrationSimulation(patient, drugs)
        assertEquals("H", newPatientState)
    }
}