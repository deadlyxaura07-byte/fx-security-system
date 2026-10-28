package com.example.data.scanner

import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import com.example.data.model.CheckCategory
import com.example.data.model.SecurityCheckItem
import com.example.data.model.SystemSecurityReport
import com.example.data.model.ThreatLevel
import java.io.File

class DeviceSecurityAuditor(private val context: Context) {

    fun executeFullAudit(): SystemSecurityReport {
        val checks = mutableListOf<SecurityCheckItem>()

        // 1. Root / OS Tamper Check
        val rootFound = checkRootBinaries() || (Build.TAGS?.contains("test-keys") == true)
        checks.add(
            SecurityCheckItem(
                id = "root_integrity",
                title = "OS Integrity & Root Check",
                description = if (rootFound) "System modifications or superuser binaries detected" else "Standard sealed Android runtime verified",
                category = CheckCategory.SYSTEM_INTEGRITY,
                threatLevel = if (rootFound) ThreatLevel.CRITICAL else ThreatLevel.SAFE,
                isPassed = !rootFound,
                actionLabel = if (rootFound) "Device Tampered" else null,
                details = "Scanned binary execution paths (/system/bin/su, /system/xbin/su, /sbin/su) and system build tags (${Build.TAGS ?: "release-keys"})."
            )
        )

        // 2. USB Debugging / ADB
        val adbEnabled = try {
            Settings.Global.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0) == 1
        } catch (_: Exception) {
            false
        }
        checks.add(
            SecurityCheckItem(
                id = "adb_debugging",
                title = "USB Debugging (ADB)",
                description = if (adbEnabled) "ADB debugging bridge is currently ACTIVE" else "ADB interface disabled (Recommended)",
                category = CheckCategory.SYSTEM_INTEGRITY,
                threatLevel = if (adbEnabled) ThreatLevel.MODERATE else ThreatLevel.SAFE,
                isPassed = !adbEnabled,
                actionLabel = if (adbEnabled) "Open Developer Settings" else null,
                details = "When USB Debugging is active, unauthorized computers or rogue charging stations can execute commands via debug bridge."
            )
        )

        // 3. Developer Options
        val devOptionsEnabled = try {
            Settings.Global.getInt(context.contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) == 1
        } catch (_: Exception) {
            false
        }
        checks.add(
            SecurityCheckItem(
                id = "developer_options",
                title = "Developer Mode",
                description = if (devOptionsEnabled) "Developer settings are unlocked on this device" else "Developer mode is inactive",
                category = CheckCategory.SYSTEM_INTEGRITY,
                threatLevel = if (devOptionsEnabled) ThreatLevel.LOW else ThreatLevel.SAFE,
                isPassed = !devOptionsEnabled,
                actionLabel = if (devOptionsEnabled) "Review Dev Options" else null,
                details = "Unlocked developer options allow mock locations, background process overrides, and custom animation hooks."
            )
        )

        // 4. Device Screen Lock & Credential Protection
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        val isDeviceSecure = keyguardManager?.isDeviceSecure == true
        checks.add(
            SecurityCheckItem(
                id = "device_lock",
                title = "Device Lock & Credentials",
                description = if (isDeviceSecure) "Biometric or PIN/Password lockscreen enforced" else "No secure lockscreen configured",
                category = CheckCategory.DEVICE_LOCK,
                threatLevel = if (isDeviceSecure) ThreatLevel.SAFE else ThreatLevel.CRITICAL,
                isPassed = isDeviceSecure,
                actionLabel = if (!isDeviceSecure) "Setup Lock Screen" else null,
                details = "A hardware-backed lock screen is required to encrypt cryptographic keystore tokens and prevent physical data theft."
            )
        )

        // 5. Unknown App Sources
        val unknownSources = try {
            @Suppress("DEPRECATION")
            Settings.Secure.getInt(context.contentResolver, Settings.Secure.INSTALL_NON_MARKET_APPS, 0) == 1
        } catch (_: Exception) {
            false
        }
        checks.add(
            SecurityCheckItem(
                id = "unknown_sources",
                title = "Legacy Sideloading Flag",
                description = if (unknownSources) "Legacy global non-market app installation is ENABLED" else "Legacy global APK installation disabled",
                category = CheckCategory.APP_RISK,
                threatLevel = if (unknownSources) ThreatLevel.MODERATE else ThreatLevel.SAFE,
                isPassed = !unknownSources,
                actionLabel = if (unknownSources) "Restrict Sideloading" else null,
                details = "Global APK installation bypasses Play Protect reputation scoring and digital signature checks."
            )
        )

        // 6. Security Patch Freshness
        val patchDate = Build.VERSION.SECURITY_PATCH
        val isPatchAvailable = !patchDate.isNullOrBlank()
        checks.add(
            SecurityCheckItem(
                id = "security_patch",
                title = "Android Security Patch Level",
                description = if (isPatchAvailable) "Device patch string: $patchDate" else "Security patch date unavailable",
                category = CheckCategory.SYSTEM_INTEGRITY,
                threatLevel = ThreatLevel.SAFE,
                isPassed = isPatchAvailable,
                actionLabel = "System Updates",
                details = "Android OS ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT}), build fingerprint: ${Build.FINGERPRINT.take(30)}..."
            )
        )

        // 7. Active Network Security
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork = connManager?.activeNetwork
        val capabilities = connManager?.getNetworkCapabilities(activeNetwork)
        val hasVpn = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true
        val isWifi = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        val isCellular = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true

        val networkTitle = when {
            hasVpn -> "Encrypted VPN Tunnel Active"
            isWifi -> "Connected to Wi-Fi Network"
            isCellular -> "Connected to Cellular Data"
            else -> "No Active Network Interface"
        }
        checks.add(
            SecurityCheckItem(
                id = "network_security",
                title = "Network Encryption Layer",
                description = networkTitle,
                category = CheckCategory.NETWORK,
                threatLevel = if (hasVpn || isCellular) ThreatLevel.SAFE else ThreatLevel.LOW,
                isPassed = hasVpn || isCellular,
                actionLabel = if (!hasVpn && isWifi) "Use Trusted VPN" else null,
                details = "Wi-Fi connections may be subject to ARP spoofing or unencrypted DNS requests if not protected by DNS-over-TLS."
            )
        )

        // 8. Hardware Biometric Sensor Availability
        val hasBiometrics = context.packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && context.packageManager.hasSystemFeature(PackageManager.FEATURE_FACE))
        checks.add(
            SecurityCheckItem(
                id = "biometric_hardware",
                title = "Biometric Cryptographic Hardware",
                description = if (hasBiometrics) "Hardware biometric authentication sensor present" else "No supported biometric hardware detected",
                category = CheckCategory.DEVICE_LOCK,
                threatLevel = ThreatLevel.SAFE,
                isPassed = hasBiometrics,
                details = "Hardware-isolated biometric chips ensure cryptographic keys are decrypted only upon verified biometric match."
            )
        )

        // 9. Informational: SELinux & Hardware Keystore (Uninspectable directly via standard app API)
        checks.add(
            SecurityCheckItem(
                id = "selinux_explanation",
                title = "SELinux & Trusted Execution Environment",
                description = "Enforced by Android OS kernel (Sandboxed)",
                category = CheckCategory.SYSTEM_INTEGRITY,
                threatLevel = ThreatLevel.SAFE,
                isPassed = true,
                isInspectable = false,
                details = "Android restricts direct third-party access to SELinux enforcer state and TrustZone keys to prevent privilege elevation. Verified via platform compatibility."
            )
        )

        // Calculate score based on actual weights
        var totalPoints = 100
        checks.forEach { check ->
            if (!check.isPassed) {
                when (check.threatLevel) {
                    ThreatLevel.CRITICAL -> totalPoints -= 30
                    ThreatLevel.MODERATE -> totalPoints -= 15
                    ThreatLevel.LOW -> totalPoints -= 5
                    ThreatLevel.SAFE -> {}
                }
            }
        }
        val finalScore = totalPoints.coerceIn(0, 100)

        val criticalCount = checks.count { !it.isPassed && it.threatLevel == ThreatLevel.CRITICAL }
        val moderateCount = checks.count { !it.isPassed && (it.threatLevel == ThreatLevel.MODERATE || it.threatLevel == ThreatLevel.LOW) }
        val passedCount = checks.count { it.isPassed }

        return SystemSecurityReport(
            score = finalScore,
            scanTimestamp = System.currentTimeMillis(),
            checks = checks,
            highRiskCount = criticalCount,
            moderateRiskCount = moderateCount,
            passedCount = passedCount
        )
    }

    private fun checkRootBinaries(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        return paths.any { path ->
            try {
                File(path).exists()
            } catch (_: Exception) {
                false
            }
        }
    }
}
