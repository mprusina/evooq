package com.mprusina.hospital_lib

class HospitalSimulator {

    enum class PatientCondition {
        F, H, D, T, X
    }

    enum class Drug {
        As, An, I, P
    }

    fun computeDrugAdministrationSimulation(patient: String, drugs: List<String>): String {
        return administerDrugsToPatient(patient, drugs)
    }

    private fun administerDrugsToPatient(patientState: String, drugs: List<String>): String {
        val newPatientStates = mutableListOf<String>()

        if (PatientCondition.D.name == patientState && !drugs.contains(Drug.I.name)) {
            return PatientCondition.X.name
        } else {
            for (drug in drugs) {
                when (drug) {
                    Drug.As.name -> newPatientStates.add(administerAs(patientState))
                    Drug.An.name -> newPatientStates.add(administerAn(patientState))
                    Drug.I.name -> newPatientStates.add(administerI(patientState, drugs.contains(Drug.An.name)))
                    Drug.P.name -> newPatientStates.add(administerP(patientState, drugs.contains(Drug.As.name)))
                }
            }
        }

        if (newPatientStates.contains(PatientCondition.X.name)) {
            return newPatientStates.find {
                it == PatientCondition.X.name
            }!!
        } else {
            for (newState in newPatientStates) {
                if (newState != patientState) {
                    return newState
                }
            }
            return patientState
        }
    }

    private fun administerAs(patient: String): String {
        return when (patient) {
            PatientCondition.F.name -> PatientCondition.H.name
            else -> patient
        }
    }

    private fun administerAn(patient: String): String {
        return when (patient) {
            PatientCondition.T.toString() -> PatientCondition.H.toString()
            else -> patient
        }
    }

    private fun administerI(patient: String, containsAntibiotic: Boolean = false): String {
        if (containsAntibiotic && patient == PatientCondition.H.name) {
            return PatientCondition.F.name
        }
        return when (patient) {
            PatientCondition.D.name -> PatientCondition.D.name
            else -> patient
        }
    }

    private fun administerP(patient: String, containsAspirin: Boolean = false): String {
        if (containsAspirin) {
            return PatientCondition.X.name
        }
        return when (patient) {
            PatientCondition.F.name -> PatientCondition.H.name
            else -> patient
        }
    }
}