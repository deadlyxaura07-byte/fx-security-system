package com.example.data.scanner

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.example.data.model.AppRiskInfo
import com.example.data.model.ThreatLevel

class AppRiskAuditor(private val context: Context) {

    fun scanInstalledApps(): List<AppRiskInfo> {
        val pm = context.packageManager
        val packages = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getInstalledPackages(PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong()))
            } else {
                @Suppress("DEPRECATION")
                pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
            }
        } catch (_: Exception) {
            emptyList()
        }

        val auditedApps = mutableListOf<AppRiskInfo>()

        for (pkg in packages) {
            val appInfo = pkg.applicationInfo ?: continue
            val appName = try {
                pm.getApplicationLabel(appInfo).toString()
            } catch (_: Exception) {
                pkg.packageName
            }

            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            val requestedPerms = pkg.requestedPermissions?.toList() ?: emptyList()

            val sensitiveFound = identifySensitivePermissions(requestedPerms)
            val (threatLevel, reason) = evaluatePrivilegeFootprint(isSystem, sensitiveFound, requestedPerms)

            auditedApps.add(
                AppRiskInfo(
                    packageName = pkg.packageName,
                    appName = appName,
                    versionName = pkg.versionName ?: "1.0",
                    isSystemApp = isSystem,
                    installTime = pkg.firstInstallTime,
                    permissions = requestedPerms,
                    sensitivePermissions = sensitiveFound,
                    threatLevel = threatLevel,
                    assessmentReason = reason
                )
            )
        }

        // Return sorted by risk level descending (CRITICAL -> MODERATE -> LOW -> SAFE)
        return auditedApps.sortedWith(
            compareBy<AppRiskInfo> {
                when (it.threatLevel) {
                    ThreatLevel.CRITICAL -> 0
                    ThreatLevel.MODERATE -> 1
                    ThreatLevel.LOW -> 2
                    ThreatLevel.SAFE -> 3
                }
            }.thenBy { it.isSystemApp }.thenBy { it.appName.lowercase() }
        )
    }

    private fun identifySensitivePermissions(permissions: List<String>): List<String> {
        val sensitiveMap = mapOf(
            "android.permission.CAMERA" to "Camera Hardware",
            "android.permission.RECORD_AUDIO" to "Microphone Audio",
            "android.permission.ACCESS_FINE_LOCATION" to "Precise GPS Location",
            "android.permission.ACCESS_BACKGROUND_LOCATION" to "Background Location",
            "android.permission.READ_SMS" to "Read SMS Messages",
            "android.permission.SEND_SMS" to "Send SMS Messages",
            "android.permission.READ_CONTACTS" to "Read Contacts",
            "android.permission.READ_CALL_LOG" to "Call History",
            "android.permission.READ_PHONE_STATE" to "Phone Telephony State",
            "android.permission.SYSTEM_ALERT_WINDOW" to "Screen Overlay Display",
            "android.permission.PACKAGE_USAGE_STATS" to "App Usage Diagnostics",
            "android.permission.MANAGE_EXTERNAL_STORAGE" to "Broad Storage Access"
        )

        return permissions.mapNotNull { sensitiveMap[it] }
    }

    private fun evaluatePrivilegeFootprint(
        isSystem: Boolean,
        sensitive: List<String>,
        allPerms: List<String>
    ): Pair<ThreatLevel, String> {
        val count = sensitive.size

        return when {
            isSystem -> {
                ThreatLevel.SAFE to "System-signed Android component (Protected OS partition)"
            }
            count >= 4 -> {
                ThreatLevel.CRITICAL to "High privilege footprint: Holds $count sensitive sensor & data access permissions"
            }
            count in 2..3 -> {
                ThreatLevel.MODERATE to "Elevated privilege: Access to $count sensitive hardware/privacy capabilities"
            }
            count == 1 -> {
                ThreatLevel.LOW to "Limited privilege: Access to single sensitive permission (${sensitive.first()})"
            }
            else -> {
                ThreatLevel.SAFE to "Standard sandboxed application (No sensitive sensor access)"
            }
        }
    }
}
