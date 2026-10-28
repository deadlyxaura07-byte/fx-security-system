package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ThreatLevel
import com.example.ui.components.FXCircularGauge
import com.example.ui.components.FXStatusBadge
import com.example.ui.theme.FXEmerald
import com.example.ui.theme.FXSecurityCyan
import com.example.ui.theme.FXSecurityRed
import com.example.ui.theme.FXSecurityYellow
import com.example.viewmodel.SecurityViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: SecurityViewModel,
    onNavigateToThreatShield: () -> Unit,
    onNavigateToAntiSpam: () -> Unit,
    onNavigateToPrivacyGuard: () -> Unit,
    onNavigateToAppScanner: () -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentReport by viewModel.currentReport.collectAsStateWithLifecycle()
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val scanProgress by viewModel.scanProgress.collectAsStateWithLifecycle()
    val scanStage by viewModel.scanCurrentStage.collectAsStateWithLifecycle()
    val history by viewModel.scanHistory.collectAsStateWithLifecycle()
    val appRisks by viewModel.appRisks.collectAsStateWithLifecycle()
    val spamIncidents by viewModel.spamIncidents.collectAsStateWithLifecycle()

    val score = currentReport?.score ?: 95
    val status = currentReport?.overallStatus ?: "SECURE"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Score Dial & Working Scan Button
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FXCircularGauge(
                        score = score,
                        overallStatus = status,
                        isScanning = isScanning,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    AnimatedVisibility(visible = isScanning) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LinearProgressIndicator(
                                progress = { scanProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = FXEmerald,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = scanStage,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.runFullScan() },
                        enabled = !isScanning,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("scan_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isScanning) "DIAGNOSTIC RUNNING..." else "RUN FULL SECURITY SCAN",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    if (currentReport != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val formattedDate = SimpleDateFormat("MMM d, yyyy · HH:mm:ss", Locale.getDefault())
                            .format(Date(currentReport!!.scanTimestamp))
                        Text(
                            text = "Last verified: $formattedDate",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Quick Telemetry Row (Threats, Privacy, Anti-Spam)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickMetricTile(
                    title = "Threat Shield",
                    value = if ((currentReport?.highRiskCount ?: 0) > 0) "${currentReport?.highRiskCount} Alerts" else "Armed",
                    threatLevel = if ((currentReport?.highRiskCount ?: 0) > 0) ThreatLevel.CRITICAL else ThreatLevel.SAFE,
                    icon = Icons.Filled.Shield,
                    onClick = onNavigateToThreatShield,
                    modifier = Modifier.weight(1f)
                )

                QuickMetricTile(
                    title = "Privacy Audit",
                    value = "${appRisks.count { it.sensitivePermissions.isNotEmpty() }} Active",
                    threatLevel = ThreatLevel.SAFE,
                    icon = Icons.Filled.Lock,
                    onClick = onNavigateToPrivacyGuard,
                    modifier = Modifier.weight(1f)
                )

                QuickMetricTile(
                    title = "Anti-Spam",
                    value = "${spamIncidents.size} Filtered",
                    threatLevel = ThreatLevel.SAFE,
                    icon = Icons.Filled.NotificationsActive,
                    onClick = onNavigateToAntiSpam,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Recommended Actions Section
        item {
            Text(
                text = "RECOMMENDED ACTIONS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            val checksWithIssues = currentReport?.checks?.filter { !it.isPassed } ?: emptyList()
            if (checksWithIssues.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FXEmerald.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(FXEmerald.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = FXEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "All System Safeguards Operational",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Zero high or moderate vulnerabilities identified in latest check.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    checksWithIssues.forEach { issue ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    when (issue.id) {
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
                                        else -> onNavigateToThreatShield()
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (issue.threatLevel == ThreatLevel.CRITICAL) FXSecurityRed.copy(alpha = 0.4f)
                                else FXSecurityYellow.copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = null,
                                    tint = if (issue.threatLevel == ThreatLevel.CRITICAL) FXSecurityRed else FXSecurityYellow,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = issue.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = issue.description,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (issue.actionLabel != null) {
                                    Text(
                                        text = issue.actionLabel,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.ChevronRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Recent Activity & History Quick Link
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT SECURITY ACTIVITY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "View History",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigateToHistory() }
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            if (history.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "No prior scan logs recorded yet. Tap 'Run Full Security Scan' above.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    history.take(3).forEach { record ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToHistory() },
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
                                        .size(34.dp)
                                        .background(
                                            if (record.score >= 85) FXEmerald.copy(alpha = 0.15f)
                                            else FXSecurityYellow.copy(alpha = 0.15f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${record.score}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (record.score >= 85) FXEmerald else FXSecurityYellow
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Diagnostic · ${record.overallStatus}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    val dateStr = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(record.timestamp))
                                    Text(
                                        text = "$dateStr · ${record.passedChecks}/${record.totalChecks} checks nominal",
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
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun QuickMetricTile(
    title: String,
    value: String,
    threatLevel: ThreatLevel,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                FXStatusBadge(threatLevel = threatLevel, customText = value)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
