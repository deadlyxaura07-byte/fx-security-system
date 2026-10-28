package com.example.data.local

import kotlinx.coroutines.flow.Flow

class SecurityHistoryRepository(private val dao: SecurityScanDao) {
    val allScans: Flow<List<SecurityScanEntity>> = dao.getAllScans()
    val latestScan: Flow<SecurityScanEntity?> = dao.getLatestScan()

    suspend fun recordScan(scan: SecurityScanEntity): Long {
        return dao.insertScan(scan)
    }

    suspend fun deleteScan(id: Long) {
        dao.deleteScanById(id)
    }

    suspend fun clearHistory() {
        dao.clearAllHistory()
    }
}
