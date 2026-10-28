package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "security_scan_history")
data class SecurityScanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val score: Int,
    val overallStatus: String,
    val totalChecks: Int,
    val passedChecks: Int,
    val criticalIssues: Int,
    val moderateIssues: Int,
    val recommendedAction: String,
    val summaryFindings: String
)
