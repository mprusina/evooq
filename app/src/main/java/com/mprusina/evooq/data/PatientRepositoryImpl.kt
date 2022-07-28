package com.mprusina.evooq.data

import com.mprusina.evooq.data.api.PatientService
import com.mprusina.evooq.data.entities.Patient
import com.mprusina.hospital_lib.DataSymbolToNameMap
import javax.inject.Inject

class PatientRepositoryImpl @Inject constructor(private val patientService: PatientService) :
    PatientRepository {

    override suspend fun getPatients(): List<Patient> {
        return convertStringToList(patientService.getPatients())
    }

    override fun convertStringToList(rawResponse: String): List<Patient> {
        val response = rawResponse.replace("\"", "")
        val patientList: MutableList<Patient> = mutableListOf()
        if (response.isNotEmpty()) {
            val stateSymbolList = response.split(",")
            var patientId = 1
            for (stateSymbol in stateSymbolList) {
                val stateName = mapDataSymbolToDataName(stateSymbol)
                val patient = Patient(patientId, stateSymbol, stateName)
                patientId++
                patientList.add(patient)
            }
        }
        return patientList
    }

    override fun mapDataSymbolToDataName(symbol: String): String {
        var name = ""
        for (stateSymbol in DataSymbolToNameMap.patientSymbolToNameMap.keys) {
            if (symbol == stateSymbol) {
                name = DataSymbolToNameMap.patientSymbolToNameMap[stateSymbol]!!
                break
            }
        }
        return name
    }
}
