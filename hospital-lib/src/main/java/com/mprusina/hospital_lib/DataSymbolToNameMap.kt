package com.mprusina.hospital_lib

object DataSymbolToNameMap {
    val patientSymbolToNameMap = hashMapOf(
        "F" to "Fever",
        "H" to "Healthy",
        "D" to "Diabetes",
        "T" to "Tuberculosis",
        "X" to "Dead"
    )

    val drugSymbolNameMap = hashMapOf(
        "As" to "Aspirin",
        "An" to "Antibiotic",
        "I" to "Insulin",
        "P" to "Paracetamol"
    )
}