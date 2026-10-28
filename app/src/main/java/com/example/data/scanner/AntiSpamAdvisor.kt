package com.example.data.scanner

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.provider.Telephony
import com.example.data.model.SavageShieldStyle
import com.example.data.model.SpamIncident

class AntiSpamAdvisor(private val context: Context) {

    fun getDefaultSmsPackage(): String? {
        return try {
            Telephony.Sms.getDefaultSmsPackage(context)
        } catch (_: Exception) {
            null
        }
    }

    fun isDefaultSmsApp(): Boolean {
        val defaultPkg = getDefaultSmsPackage()
        return defaultPkg == context.packageName
    }

    fun getSmsSettingsIntent(): Intent {
        return Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    fun getNotificationSettingsIntent(): Intent {
        return Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    fun getSavageMessageForStyle(style: SavageShieldStyle): String {
        return when (style) {
            SavageShieldStyle.SAVAGE ->
                "Nice try. You're attempting to spam an FX-protected user. Suspicious activity may be filtered and recorded. This device is protected by FX. Just fuck off."
            SavageShieldStyle.IRONCLAD ->
                "Security Notice: You are transmitting to an FX Security protected endpoint. Metadata fingerprinting and carrier spam telemetry are active. Unauthorized solicitations are reported under RFC-compliance protocols."
            SavageShieldStyle.STEALTH ->
                "FX Stealth Sentinel: Inbound unsolicited transmission suppressed. Carrier audit trail initiated."
        }
    }

    fun getSampleIncidents(): List<SpamIncident> {
        val now = System.currentTimeMillis()
        val hour = 3600 * 1000L
        return listOf(
            SpamIncident(
                id = "inc-101",
                timestamp = now - (2 * hour),
                senderOrCaller = "+1 (800) 555-0192",
                incidentType = "Robocall Flag",
                contentPreview = "Automated voice prompt: 'Immediate action required regarding your tax returns...'",
                actionTaken = "Flagged & Diverted to Voicemail Quarantine",
                isDemo = true
            ),
            SpamIncident(
                id = "inc-102",
                timestamp = now - (7 * hour),
                senderOrCaller = "USPS-ALERT#4920",
                incidentType = "Phishing SMS",
                contentPreview = "Package delayed due to address mismatch: click usps-track-customs-fix.biz/up to update.",
                actionTaken = "Malicious URL Pattern Intercepted",
                isDemo = true
            ),
            SpamIncident(
                id = "inc-103",
                timestamp = now - (22 * hour),
                senderOrCaller = "+1 (212) 555-0144",
                incidentType = "Telemarketing Sweep",
                contentPreview = "Energy rate reduction outbound dialer campaign.",
                actionTaken = "Silent Screening Heuristic Matched",
                isDemo = true
            ),
            SpamIncident(
                id = "inc-104",
                timestamp = now - (38 * hour),
                senderOrCaller = "+44 7911 123456",
                incidentType = "Wangiri One-Ring",
                contentPreview = "International single-ring call pattern designed to trigger toll callback.",
                actionTaken = "High-Risk International Call Filtered",
                isDemo = true
            )
        )
    }
}
