package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.SecurityScanEntity
import com.example.ui.theme.FXEmerald
import com.example.ui.theme.FXSecurityRed
import com.example.ui.theme.FXSecurityYellow
import com.example.viewmodel.SecurityViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SecurityHistoryScreen(
    viewModel: SecurityViewModel,
    modifier: Modifier = Modifier
) {
    val history by viewModel.scanHistory.collectAsStateWithLifecycle()
    var showClearConfirm by remember { mutableStateOf(false) }
    var selectedScanForDetail by remember { mutableStateOf<SecurityScanEntity?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AUDIT LOG ARCHIVE",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${history.size} verified diagnostic snapshots preserved locally in Room",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (history.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { showClearConfirm = true },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_clear_history")
                    ) {
                        Icon(imageVector = Icons.Filled.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear", fontSize = 12.sp)
                    }
                }
            }
        }

        if (history.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Prior Audit Snapshots",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Execute a full scan from the dashboard to initialize immutable local audit logs.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.runFullScan() },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Run Initial Diagnostic")
                        }
                    }
                }
            }
        } else {
            items(history, key = { it.id }) { scan ->
                HistoryScanCard(
                    scan = scan,
                    onClick = { selectedScanForDetail = scan },
                    onDelete = { viewModel.deleteHistoryItem(scan.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear All Audit History?") },
            text = { Text("This will permanently remove all stored local scan records and recommendations from the device database.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FXSecurityRed)
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    selectedScanForDetail?.let { scan ->
        AlertDialog(
            onDismissRequest = { selectedScanForDetail = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (scan.score >= 85) FXEmerald.copy(alpha = 0.15f) else FXSecurityYellow.copy(alpha = 0.15f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${scan.score}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (scan.score >= 85) FXEmerald else FXSecurityYellow
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "Audit Record #${scan.id}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        val dateStr = SimpleDateFormat("MMM d, yyyy · HH:mm:ss", Locale.getDefault()).format(Date(scan.timestamp))
                        Text(text = dateStr, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Status: ${scan.overallStatus}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Telemetry: ${scan.passedChecks}/${scan.totalChecks} checks nominal (${scan.criticalIssues} critical, ${scan.moderateIssues} moderate)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Findings Summary:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = scan.summaryFindings,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Recommended Action:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = scan.recommendedAction,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(onClick = { selectedScanForDetail = null }) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
private fun HistoryScanCard(
    scan: SecurityScanEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("history_item_${scan.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (scan.score >= 85) FXEmerald.copy(alpha = 0.15f)
                                else FXSecurityYellow.copy(alpha = 0.15f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${scan.score}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (scan.score >= 85) FXEmerald else FXSecurityYellow
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        val dateStr = SimpleDateFormat("MMM d, yyyy · HH:mm", Locale.getDefault()).format(Date(scan.timestamp))
                        Text(
                            text = dateStr,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Status: ${scan.overallStatus} · ${scan.passedChecks}/${scan.totalChecks} Passed",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete record",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = scan.recommendedAction,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}
