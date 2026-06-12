package com.example.data

import kotlinx.coroutines.flow.Flow

class PnrRepository(private val pnrDao: PnrDao) {
    val allSearches: Flow<List<PnrEntity>> = pnrDao.getAllSearches()

    suspend fun getPnr(pnr: String): PnrEntity? {
        return pnrDao.getPnr(pnr)
    }

    suspend fun insertPnr(pnr: PnrEntity) {
        pnrDao.insertPnr(pnr)
    }

    suspend fun deletePnr(pnr: PnrEntity) {
        pnrDao.deletePnr(pnr)
    }

    suspend fun clearHistory() {
        pnrDao.clearHistory()
    }
}
