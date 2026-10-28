package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AppRiskInfo
import com.example.data.model.ThreatLevel
import com.example.ui.components.FXStatusBadge
import com.example.ui.theme.FXEmerald
import com.example.ui.theme.FXSecurityRed
import com.example.ui.theme.FXSecurityYellow
import com.example.viewmodel.SecurityViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppRiskScannerScreen(
    viewModel: SecurityViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allApps by viewModel.appRisks.collectAsStateWithLifecycle()
    val isScanningApps by viewModel.isScanningApps.collectAsStateWithLifecycle()
    val searchQuery by viewModel.appSearchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedRiskFilter.collectAsStateWithLifecycle()

    var selectedAppForDetail by remember { mutableStateOf<AppRiskInfo?>(null) }

    val filteredApps = remember(allApps, searchQuery, selectedFilter) {
        allApps.filter { app ->
            val matchesQuery = searchQuery.isBlank() ||
                    app.appName.contains(searchQuery, ignoreCase = true) ||
                    app.packageName.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                null -> true
                ThreatLevel.CRITICAL -> app.threatLevel == ThreatLevel.CRITICAL
                ThreatLevel.MODERATE -> app.threatLevel == ThreatLevel.MODERATE
                ThreatLevel.LOW -> app.threatLevel == ThreatLevel.LOW
                ThreatLevel.SAFE -> app.threatLevel == ThreatLevel.SAFE
            }
            matchesQuery && matchesFilter
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Header summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "APP PRIVILEGE AUDITOR",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "${allApps.size} installed packages inspected via Android APIs",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { viewModel.loadAppRisks() },
                            enabled = !isScanningApps,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_rescan_apps")
                        ) {
                            Icon(imageVector = Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isScanningApps) "..." else "Rescan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "NOTICE: Applications are scored strictly by declared hardware privilege footprint (Sensors, SMS, Location). FX does not falsely label legitimate software as malware without verified telemetry.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setAppSearchQuery(it) },
                placeholder = { Text("Search by name or package...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setAppSearchQuery("") }) {
                            Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_search_field"),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Filter chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { viewModel.setRiskFilter(null) },
                        label = { Text("All (${allApps.size})", fontSize = 11.sp) }
                    )
                }
                item {
                    val count = allApps.count { it.threatLevel == ThreatLevel.CRITICAL }
                    FilterChip(
                        selected = selectedFilter == ThreatLevel.CRITICAL,
                        onClick = { viewModel.setRiskFilter(ThreatLevel.CRITICAL) },
                        label = { Text("High Privilege ($count)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FXSecurityRed.copy(alpha = 0.2f),
                            selectedLabelColor = FXSecurityRed
                        )
                    )
                }
                item {
                    val count = allApps.count { it.threatLevel == ThreatLevel.MODERATE }
                    FilterChip(
                        selected = selectedFilter == ThreatLevel.MODERATE,
                        onClick = { viewModel.setRiskFilter(ThreatLevel.MODERATE) },
                        label = { Text("Elevated ($count)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FXSecurityYellow.copy(alpha = 0.2f),
                            selectedLabelColor = FXSecurityYellow
                        )
                    )
                }
                item {
                    val count = allApps.count { it.threatLevel == ThreatLevel.SAFE }
                    FilterChip(
                        selected = selectedFilter == ThreatLevel.SAFE,
                        onClick = { viewModel.setRiskFilter(ThreatLevel.SAFE) },
                        label = { Text("Standard / Safe ($count)", fontSize = 11.sp) }
                    )
                }
            }
        }

        if (filteredApps.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isScanningApps) "Auditing installed applications..." else "No applications match the search/filter criteria.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(filteredApps, key = { it.packageName }) { app ->
                AppRiskItemCard(
                    app = app,
                    onClick = { selectedAppForDetail = app }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // App Detail Dialog
    selectedAppForDetail?.let { app ->
        AlertDialog(
            onDismissRequest = { selectedAppForDetail = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Filled.Android, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = app.appName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = "v${app.versionName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Package: ${app.packageName}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Assessment: ", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        FXStatusBadge(threatLevel = app.threatLevel)
                    }

                    Text(
                        text = app.assessmentReason,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 16.sp
                    )

                    if (app.sensitivePermissions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sensitive Permissions (${app.sensitivePermissions.size}):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            app.sensitivePermissions.forEach { perm ->
                                Box(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = perm, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(app.installTime))
                    Text(
                        text = "Installed: $dateStr · ${if (app.isSystemApp) "System App" else "User App"}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.parse("package:${app.packageName}")
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        } catch (_: Exception) {}
                        selectedAppForDetail = null
                    }
                ) {
                    Icon(imageVector = Icons.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("App Info & Permissions")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedAppForDetail = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun AppRiskItemCard(
    app: AppRiskInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("app_item_${app.packageName}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (app.threatLevel == ThreatLevel.CRITICAL) FXSecurityRed.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
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
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Android,
                    contentDescription = null,
                    tint = if (app.threatLevel == ThreatLevel.CRITICAL) FXSecurityRed else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
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
                        text = app.appName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    FXStatusBadge(
                        threatLevel = app.threatLevel,
                        customText = when (app.threatLevel) {
                            ThreatLevel.CRITICAL -> "HIGH PRIV"
                            ThreatLevel.MODERATE -> "ELEVATED"
                            ThreatLevel.LOW -> "LOW"
                            ThreatLevel.SAFE -> if (app.isSystemApp) "SYSTEM" else "SAFE"
                        }
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = app.packageName,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                if (app.sensitivePermissions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = app.sensitivePermissions.take(3).joinToString(" · ") +
                                if (app.sensitivePermissions.size > 3) " (+${app.sensitivePermissions.size - 3})" else "",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
