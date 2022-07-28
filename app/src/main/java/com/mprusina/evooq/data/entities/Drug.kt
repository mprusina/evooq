package com.mprusina.evooq.data.entities

data class Drug(
    val id: Int,
    val drugSymbol: String,
    val drugName: String,
    var isSelected: Boolean = false
)
