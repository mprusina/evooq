package com.mprusina.evooq.data

import com.mprusina.evooq.data.entities.Patient

interface PatientRepository: Repository {
    suspend fun getPatients(): List<Patient>
}