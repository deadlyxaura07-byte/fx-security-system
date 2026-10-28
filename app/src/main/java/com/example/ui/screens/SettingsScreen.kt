package com.example.ui.screens

import android.os.Build
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SavageShieldStyle
import com.example.ui.theme.FXEmerald
import com.example.ui.theme.FXPrismBlue
import com.example.ui.theme.FXSecurityRed
import com.example.ui.theme.FXThemeMode
import com.example.viewmodel.SecurityViewModel

@Composable
fun SettingsScreen(
    viewModel: SecurityViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val savageStyle by viewModel.savageStyle.collectAsStateWithLifecycle()
    val savagePopup by viewModel.savagePopupEnabled.collectAsStateWithLifecycle()
    val autoScan by viewModel.autoScanEnabled.collectAsStateWithLifecycle()
    val notifyThreats by viewModel.notifyOnThreats.collectAsStateWithLifecycle()

    var showClearDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "FX PRODUCT IDENTITY & THEME SYSTEM",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ThemeOptionCard(
                    mode = FXThemeMode.FX_BLACK,
                    currentMode = themeMode,
                    title = "FX BLACK",
                    description = "Graphite black, titanium surfaces, emerald green accents",
                    accentColor = FXEmerald,
                    icon = Icons.Filled.Brightness4,
                    onSelect = { viewModel.setThemeMode(FXThemeMode.FX_BLACK) }
                )

                ThemeOptionCard(
                    mode = FXThemeMode.FX_PRISM,
                    currentMode = themeMode,
                    title = "FX PRISM",
                    description = "Clean white, silver details, subtle sapphire blue accents",
                    accentColor = FXPrismBlue,
                    icon = Icons.Filled.Brightness7,
                    onSelect = { viewModel.setThemeMode(FXThemeMode.FX_PRISM) }
                )

                ThemeOptionCard(
                    mode = FXThemeMode.FX_HYBRID,
                    currentMode = themeMode,
                    title = "FX HYBRID",
                    description = "Unified design system with adaptive dark/light synchronization",
                    accentColor = MaterialTheme.colorScheme.primary,
                    icon = Icons.Filled.Contrast,
                    onSelect = { viewModel.setThemeMode(FXThemeMode.FX_HYBRID) }
                )
            }
        }

        // Savage Shield Tone Preferences
        item {
            Text(
                text = "SAVAGE SHIELD ALERT TONE",
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
                    SavageShieldStyle.values().forEach { style ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.setSavageStyle(style) }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        if (savageStyle == style) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.surfaceVariant,
                                        CircleShape
                                    )
                                    .border(
                                        1.dp,
                                        if (savageStyle == style) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outline,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (savageStyle == style) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = style.label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = style.subtitle,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Automated Surveillance Preferences
        item {
            Text(
                text = "AUTOMATED SENTINEL POLICIES",
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
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Auto Diagnostic Schedule",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Periodic background checks for root kits and debug bridges",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = autoScan,
                            onCheckedChange = { viewModel.setAutoScanEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Immediate Threat Broadcast",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "High-priority push notifications upon critical exploit detection",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = notifyThreats,
                            onCheckedChange = { viewModel.setNotifyOnThreats(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }

        // Device System Specs
        item {
            Text(
                text = "DEVICE TELEMETRY & ENGINE SPECS",
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
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SpecRow(label = "FX Engine Version", value = "FX Titanium v2.4 (Build #9842)")
                    SpecRow(label = "Target Platform", value = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
                    SpecRow(label = "Hardware Model", value = "${Build.MANUFACTURER.uppercase()} ${Build.MODEL}")
                    SpecRow(label = "Security Patch", value = Build.VERSION.SECURITY_PATCH ?: "Platform default")
                    SpecRow(label = "Database Engine", value = "SQLite / Room Local Encrypted Sandbox")
                }
            }
        }

        // Database reset button
        item {
            OutlinedButton(
                onClick = { showClearDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_reset_storage"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = null, tint = FXSecurityRed, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Purge Local Security History & Cache", color = FXSecurityRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Purge History Database?") },
            text = { Text("This will erase all past scan records and restore the security history database to its clean factory state.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FXSecurityRed)
                ) {
                    Text("Purge Database")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ThemeOptionCard(
    mode: FXThemeMode,
    currentMode: FXThemeMode,
    title: String,
    description: String,
    accentColor: Color,
    icon: ImageVector,
    onSelect: () -> Unit
) {
    val isSelected = mode == currentMode
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("theme_option_${mode.name}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(accentColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(accentColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}
