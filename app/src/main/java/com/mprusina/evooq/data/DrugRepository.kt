package com.mprusina.evooq.data

import com.mprusina.evooq.data.entities.Drug

interface DrugRepository: Repository {
    suspend fun getDrugs(): List<Drug>
}