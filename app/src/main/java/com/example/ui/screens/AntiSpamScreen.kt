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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SavageShieldStyle
import com.example.data.model.SpamIncident
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
fun AntiSpamScreen(
    viewModel: SecurityViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val spamIncidents by viewModel.spamIncidents.collectAsStateWithLifecycle()
    val savageStyle by viewModel.savageStyle.collectAsStateWithLifecycle()
    val savagePopupEnabled by viewModel.savagePopupEnabled.collectAsStateWithLifecycle()
    val aggressiveSpamFilter by viewModel.aggressiveSpamFilter.collectAsStateWithLifecycle()

    var showAddIncidentDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("DEFENSE & PREVIEW", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_anti_spam_preview")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("INCIDENT LOG (${spamIncidents.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_anti_spam_log")
            )
        }

        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    // Savage Shield Preview Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("savage_shield_card"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FXEmerald.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(FXEmerald, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "SAVAGE SHIELD INTERACTIVE PREVIEW",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = FXEmerald,
                                        letterSpacing = 1.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(FXEmerald.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "SIMULATED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = FXEmerald)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Simulated Notification banner / message bubble
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.NotificationsActive,
                                            contentDescription = null,
                                            tint = FXEmerald,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "FX Anti-Spam Sentinel · Inbound Filter",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // REQUIRED EXACT MESSAGE STRING:
                                    Text(
                                        text = "\"Nice try. You're attempting to spam an FX-protected user. Suspicious activity may be filtered and recorded. This device is protected by FX. Just fuck off.\"",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 18.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Required Compliance Notice
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.background,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Filled.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(top = 1.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "DISPLAY ONLY: This message is a local notification demonstration. FX does not pretend this message has been delivered to a suspected spammer, nor does it automatically send retaliatory SMS messages, strictly adhering to Android carrier policies.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Supported Android Spam-Filtering Settings
                item {
                    Text(
                        text = "SUPPORTED ANDROID SPAM-FILTERING SETTINGS",
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
                            SettingActionItem(
                                title = "Default SMS & Messaging App",
                                subtitle = "Configure SMS filtering and default carrier app handler",
                                icon = Icons.Filled.Chat,
                                onClick = {
                                    context.startActivity(Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    })
                                }
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            SettingActionItem(
                                title = "Caller ID & Call Screening",
                                subtitle = "Verify system phone screening and silence unknown callers",
                                icon = Icons.Filled.Call,
                                onClick = {
                                    context.startActivity(Intent(Settings.ACTION_SOUND_SETTINGS).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    })
                                }
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            SettingActionItem(
                                title = "Notification Channel Filtering",
                                subtitle = "Review blocked channels, popups, and priority alerts",
                                icon = Icons.Filled.NotificationsActive,
                                onClick = {
                                    context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    })
                                }
                            )
                        }
                    }
                }

                // User-configurable Alert Preferences
                item {
                    Text(
                        text = "ALERT PREFERENCES",
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
                                        text = "Aggressive Spam Heuristics",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Flags unknown international VoIP prefixes and scam URL patterns",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = aggressiveSpamFilter,
                                    onCheckedChange = { viewModel.setAggressiveSpamFilter(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Savage Shield Alert Popups",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Shows high-contrast banner when spam transmission is intercepted",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = savagePopupEnabled,
                                    onCheckedChange = { viewModel.setSavagePopupEnabled(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            }
                        }
                    }
                }

                // Spam Protection Guidance
                item {
                    Text(
                        text = "SPAM PROTECTION GUIDANCE",
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
                            GuidanceBullet(
                                number = "1",
                                title = "Carrier STIR/SHAKEN Protocol",
                                description = "Major telecom networks cryptographically verify caller ID tokens. If caller ID displays 'Spam Suspected', do not engage or press digits."
                            )
                            GuidanceBullet(
                                number = "2",
                                title = "Zero-Trust SMS Verification",
                                description = "Never tap links claiming your bank, tax agency, or parcel delivery requires immediate credential updates. Visit the provider's verified app directly."
                            )
                            GuidanceBullet(
                                number = "3",
                                title = "Wangiri One-Ring Traps",
                                description = "Calls that ring once from unknown overseas countries (+2xx, +8xx, etc.) charge extortionate per-minute toll rates when called back. Block immediately."
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        } else {
            // INCIDENT LOG TAB
            LazyColumn(
                modifier = Modifier
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
                                text = "FILTERED INCIDENTS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Simulated & user-reported events",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { showAddIncidentDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("btn_report_incident")
                            ) {
                                Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Report", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            if (spamIncidents.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = { viewModel.clearSpamHistory() },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                if (spamIncidents.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "No spam incidents recorded. Tap 'Report' to log a suspicious caller or SMS.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(spamIncidents, key = { it.id }) { incident ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("incident_${incident.id}"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
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
                                        Icon(
                                            imageVector = Icons.Filled.Warning,
                                            contentDescription = null,
                                            tint = FXSecurityYellow,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = incident.senderOrCaller,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    if (incident.isDemo) {
                                        Box(
                                            modifier = Modifier
                                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(text = "DEMO DATA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .background(FXSecurityRed.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(text = "REPORTED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = FXSecurityRed)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = incident.incidentType,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = incident.contentPreview,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Action: ${incident.actionTaken}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = FXEmerald
                                    )
                                    val dateStr = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(incident.timestamp))
                                    Text(
                                        text = dateStr,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
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
    }

    if (showAddIncidentDialog) {
        var inputSender by remember { mutableStateOf("") }
        var inputType by remember { mutableStateOf("Spam Call") }
        var inputNote by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddIncidentDialog = false },
            title = { Text("Report Suspicious Incident") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = inputSender,
                        onValueChange = { inputSender = it },
                        label = { Text("Phone Number or Sender ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = inputType,
                        onValueChange = { inputType = it },
                        label = { Text("Threat Type (e.g. Phishing SMS, Robocall)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = inputNote,
                        onValueChange = { inputNote = it },
                        label = { Text("Details / Message Preview") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputSender.isNotBlank()) {
                            viewModel.addManualSpamIncident(
                                inputSender.trim(),
                                inputType.trim(),
                                inputNote.trim().ifEmpty { "Manually flagged suspicious contact." }
                            )
                            showAddIncidentDialog = false
                        }
                    }
                ) {
                    Text("Add to Log")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddIncidentDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingActionItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
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

@Composable
private fun GuidanceBullet(
    number: String,
    title: String,
    description: String
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = number, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 15.sp
            )
        }
    }
}
