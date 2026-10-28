package com.example.data.model

enum class ThreatLevel {
    SAFE,
    LOW,
    MODERATE,
    CRITICAL
}

enum class CheckCategory(val displayName: String) {
    SYSTEM_INTEGRITY("System Integrity"),
    DEVICE_LOCK("Device Lock & Auth"),
    PRIVACY_PERMISSIONS("Privacy & Sensors"),
    NETWORK("Network Encryption"),
    SPAM_DEFENSE("Spam Shield"),
    APP_RISK("Application Hygiene")
}

data class SecurityCheckItem(
    val id: String,
    val title: String,
    val description: String,
    val category: CheckCategory,
    val threatLevel: ThreatLevel,
    val isPassed: Boolean,
    val actionLabel: String? = null,
    val details: String? = null,
    val isInspectable: Boolean = true
)

data class SystemSecurityReport(
    val score: Int,
    val scanTimestamp: Long,
    val checks: List<SecurityCheckItem>,
    val highRiskCount: Int,
    val moderateRiskCount: Int,
    val passedCount: Int
) {
    val overallStatus: String
        get() = when {
            score >= 85 -> "SECURE"
            score >= 65 -> "ATTENTION REQUIRED"
            else -> "HIGH RISK"
        }
}

data class AppRiskInfo(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val isSystemApp: Boolean,
    val installTime: Long,
    val permissions: List<String>,
    val sensitivePermissions: List<String>,
    val threatLevel: ThreatLevel,
    val assessmentReason: String
)

data class SpamIncident(
    val id: String,
    val timestamp: Long,
    val senderOrCaller: String,
    val incidentType: String, // "Robocall Flag", "Phishing SMS", "Spam Keyword", "Telemarketer"
    val contentPreview: String,
    val actionTaken: String,
    val isDemo: Boolean = true
)

enum class SavageShieldStyle(val label: String, val subtitle: String) {
    SAVAGE("Savage Shield", "Unfiltered raw retaliation notice"),
    IRONCLAD("Ironclad Formal", "Strict corporate legal threat & logging notice"),
    STEALTH("Silent Sentinel", "Stealth capture & quiet carrier reporting")
}
