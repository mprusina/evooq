package com.mprusina.evooq.data

import com.mprusina.evooq.data.api.DrugService
import com.mprusina.evooq.data.entities.Drug
import com.mprusina.hospital_lib.DataSymbolToNameMap
import javax.inject.Inject

class DrugRepositoryImpl @Inject constructor(private val drugService: DrugService) : DrugRepository {

    override suspend fun getDrugs(): List<Drug> {
        return convertStringToList(drugService.getDrugs())
    }

    override fun convertStringToList(rawResponse: String): List<Drug> {
        val response = rawResponse.replace("\"", "")
        val drugList: MutableList<Drug> = mutableListOf()
        if (response.isNotEmpty()) {
            val drugSymbolList = response.split(",")
            var drugId = 1
            for (drugSymbol in drugSymbolList) {
                val drugName = mapDataSymbolToDataName(drugSymbol)
                val drug = Drug(drugId, drugSymbol, drugName)
                drugId++
                drugList.add(drug)
            }
        }
        return drugList
    }

    override fun mapDataSymbolToDataName(symbol: String): String {
        var name = ""
        for (drugSymbol in DataSymbolToNameMap.drugSymbolNameMap.keys) {
            if (symbol == drugSymbol) {
                name = DataSymbolToNameMap.drugSymbolNameMap[drugSymbol]!!
                break
            }
        }
        return name
    }
}