package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.FXEmerald
import com.example.ui.theme.FXSecurityCyan
import com.example.ui.theme.FXSecurityYellow
import com.example.viewmodel.SecurityViewModel

@Composable
fun PrivacyGuardScreen(
    viewModel: SecurityViewModel,
    onNavigateToAppScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appRisks by viewModel.appRisks.collectAsStateWithLifecycle()

    val cameraApps = appRisks.count { it.sensitivePermissions.contains("Camera Hardware") }
    val micApps = appRisks.count { it.sensitivePermissions.contains("Microphone Audio") }
    val locationApps = appRisks.count { it.sensitivePermissions.contains("Precise GPS Location") }
    val contactsApps = appRisks.count { it.sensitivePermissions.contains("Read Contacts") }
    val smsApps = appRisks.count { it.sensitivePermissions.contains("Read SMS Messages") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Privacy Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, FXSecurityCyan.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(FXSecurityCyan.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = FXSecurityCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "PRIVACY GUARD",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Hardware sensor & permission surveillance",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            try {
                                context.startActivity(Intent(Settings.ACTION_PRIVACY_SETTINGS).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                })
                            } catch (_: Exception) {
                                context.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                })
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_system_privacy_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open Android Privacy Dashboard", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Active Hardware Sensors & Permission Footprint
        item {
            Text(
                text = "INSPECTED HARDWARE PERMISSIONS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SensorPermissionTile(
                    title = "Camera Hardware Access",
                    count = cameraApps,
                    icon = Icons.Filled.CameraAlt,
                    description = "Apps with declared privileges to record optical sensor streams",
                    onClick = onNavigateToAppScanner
                )
                SensorPermissionTile(
                    title = "Microphone & Audio Capture",
                    count = micApps,
                    icon = Icons.Filled.Mic,
                    description = "Apps capable of sampling ambient audio in foreground or background",
                    onClick = onNavigateToAppScanner
                )
                SensorPermissionTile(
                    title = "Precise & Background GPS",
                    count = locationApps,
                    icon = Icons.Filled.LocationOn,
                    description = "Apps holding fine location coordinates (< 10m precision)",
                    onClick = onNavigateToAppScanner
                )
                SensorPermissionTile(
                    title = "Personal Contacts & Address Book",
                    count = contactsApps,
                    icon = Icons.Filled.Visibility,
                    description = "Apps granted permission to parse full contact directory",
                    onClick = onNavigateToAppScanner
                )
                SensorPermissionTile(
                    title = "SMS Telephony Messages",
                    count = smsApps,
                    icon = Icons.Filled.Security,
                    description = "Apps requesting permission to read inbound or historical text messages",
                    onClick = onNavigateToAppScanner
                )
            }
        }

        // Educational Breakdown: Uninspectable Settings
        item {
            Text(
                text = "SYSTEM SETTINGS AUDIT DISCLOSURE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.HelpOutline,
                            contentDescription = null,
                            tint = FXSecurityYellow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Settings Protected by Android Sandboxing",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "Android OS isolates certain security configurations so standard non-root applications cannot inspect or alter them without privileged system signatures:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    UninspectableItem(
                        title = "Private DNS / DNS-over-TLS",
                        explanation = "The active Private DNS provider hostname cannot be queried by 3rd-party apps to protect user browsing privacy. Verify manually in System > Network > Private DNS."
                    )

                    UninspectableItem(
                        title = "Hardware Kill Switch Status",
                        explanation = "Physical hardware sensor disconnect toggles (found on select privacy-focused devices) exist at the motherboard level and report no public standard API."
                    )

                    UninspectableItem(
                        title = "Kernel SELinux Enforcing Mode",
                        explanation = "Android enforces SELinux via the Linux kernel. Third-party apps are blocked from calling getenforce to prevent privilege probing."
                    )

                    UninspectableItem(
                        title = "TrustZone & Hardware Master Keys",
                        explanation = "Keystore encryption keys are stored inside isolated TrustZone / StrongBox silicon; apps can request cryptographic operations but can never inspect raw key material."
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SensorPermissionTile(
    title: String,
    count: Int,
    icon: ImageVector,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .background(
                                if (count > 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$count apps",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (count > 0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun UninspectableItem(title: String, explanation: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = explanation,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 15.sp
        )
    }
}
