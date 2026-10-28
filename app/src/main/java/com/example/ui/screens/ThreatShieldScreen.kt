package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CheckCategory
import com.example.data.model.ThreatLevel
import com.example.ui.components.FXCheckItemCard
import com.example.ui.components.FXStatusBadge
import com.example.ui.theme.FXEmerald
import com.example.ui.theme.FXSecurityRed
import com.example.ui.theme.FXSecurityYellow
import com.example.viewmodel.SecurityViewModel

@Composable
fun ThreatShieldScreen(
    viewModel: SecurityViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentReport by viewModel.currentReport.collectAsStateWithLifecycle()
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()

    val checks = currentReport?.checks ?: emptyList()
    val highRiskCount = currentReport?.highRiskCount ?: 0
    val moderateCount = currentReport?.moderateRiskCount ?: 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Threat Shield Status Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (highRiskCount > 0) FXSecurityRed.copy(alpha = 0.5f)
                    else FXEmerald.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (highRiskCount > 0) FXSecurityRed.copy(alpha = 0.15f)
                                else FXEmerald.copy(alpha = 0.15f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = null,
                            tint = if (highRiskCount > 0) FXSecurityRed else FXEmerald,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "THREAT SENTINEL",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            FXStatusBadge(
                                threatLevel = if (highRiskCount > 0) ThreatLevel.CRITICAL
                                else if (moderateCount > 0) ThreatLevel.MODERATE
                                else ThreatLevel.SAFE
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (highRiskCount > 0) "$highRiskCount critical vulnerabilities require remediation"
                            else if (moderateCount > 0) "$moderateCount moderate configuration warnings noted"
                            else "Device kernel, lockscreen, and interfaces actively secured",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Action row: Rescan
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVE DEFENSE MODULES (${checks.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )

                Button(
                    onClick = { viewModel.runFullScan() },
                    enabled = !isScanning,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("rescan_threats_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isScanning) "Auditing..." else "Rescan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // List of checks with expandable details and actions
        items(checks, key = { it.id }) { check ->
            FXCheckItemCard(
                check = check,
                onActionClick = {
                    when (check.id) {
                        "adb_debugging", "developer_options" -> {
                            context.startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        }
                        "device_lock" -> {
                            context.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        }
                        "unknown_sources" -> {
                            context.startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        }
                        "network_security" -> {
                            context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        }
                        "security_patch" -> {
                            context.startActivity(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        }
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
