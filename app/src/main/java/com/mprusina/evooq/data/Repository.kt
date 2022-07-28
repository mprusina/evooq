package com.mprusina.evooq.data

interface Repository {
    fun convertStringToList(rawResponse: String): List<Any>
    fun mapDataSymbolToDataName(symbol: String): String
}