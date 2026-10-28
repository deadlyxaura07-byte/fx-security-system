package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.FXTopBar
import com.example.ui.screens.AntiSpamScreen
import com.example.ui.screens.AppRiskScannerScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.PrivacyGuardScreen
import com.example.ui.screens.SecurityHistoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ThreatShieldScreen
import com.example.ui.theme.FXEmerald
import com.example.ui.theme.FXSecurityTheme
import com.example.ui.theme.FXThemeMode
import com.example.viewmodel.SecurityViewModel

enum class AppScreen(
    val label: String,
    val icon: ImageVector,
    val isBottomBarItem: Boolean = true
) {
    DASHBOARD("Dashboard", Icons.Filled.Dashboard, true),
    THREAT_SHIELD("Threats", Icons.Filled.Shield, true),
    ANTI_SPAM("Anti-Spam", Icons.Filled.PhoneAndroid, true),
    PRIVACY_GUARD("Privacy", Icons.Filled.Lock, false),
    APP_RISK("App Risk", Icons.Filled.Security, true),
    HISTORY("History", Icons.Filled.History, false),
    SETTINGS("Settings", Icons.Filled.Settings, true)
}

class MainActivity : ComponentActivity() {

    private val viewModel: SecurityViewModel by viewModels {
        val app = application as FXSecurityApp
        SecurityViewModel.Factory(
            app.deviceSecurityAuditor,
            app.appRiskAuditor,
            app.antiSpamAdvisor,
            app.historyRepository,
            app.preferencesRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

            FXSecurityTheme(themeMode = themeMode) {
                var currentScreen by rememberSaveable { mutableStateOf(AppScreen.DASHBOARD) }

                BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
                    currentScreen = AppScreen.DASHBOARD
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing,
                    topBar = {
                        FXTopBar(
                            currentTheme = themeMode,
                            onToggleTheme = {
                                val nextMode = when (themeMode) {
                                    FXThemeMode.FX_BLACK -> FXThemeMode.FX_PRISM
                                    FXThemeMode.FX_PRISM -> FXThemeMode.FX_HYBRID
                                    FXThemeMode.FX_HYBRID -> FXThemeMode.FX_BLACK
                                }
                                viewModel.setThemeMode(nextMode)
                            }
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            tonalElevation = 8.dp
                        ) {
                            AppScreen.values().filter { it.isBottomBarItem }.forEach { screen ->
                                val selected = currentScreen == screen
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { currentScreen = screen },
                                    icon = {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = screen.label
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = screen.label,
                                            fontSize = 11.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.testTag("nav_${screen.name.lowercase()}")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        when (currentScreen) {
                            AppScreen.DASHBOARD -> DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToThreatShield = { currentScreen = AppScreen.THREAT_SHIELD },
                                onNavigateToAntiSpam = { currentScreen = AppScreen.ANTI_SPAM },
                                onNavigateToPrivacyGuard = { currentScreen = AppScreen.PRIVACY_GUARD },
                                onNavigateToAppScanner = { currentScreen = AppScreen.APP_RISK },
                                onNavigateToHistory = { currentScreen = AppScreen.HISTORY }
                            )
                            AppScreen.THREAT_SHIELD -> ThreatShieldScreen(
                                viewModel = viewModel
                            )
                            AppScreen.ANTI_SPAM -> AntiSpamScreen(
                                viewModel = viewModel
                            )
                            AppScreen.PRIVACY_GUARD -> PrivacyGuardScreen(
                                viewModel = viewModel,
                                onNavigateToAppScanner = { currentScreen = AppScreen.APP_RISK }
                            )
                            AppScreen.APP_RISK -> AppRiskScannerScreen(
                                viewModel = viewModel
                            )
                            AppScreen.HISTORY -> SecurityHistoryScreen(
                                viewModel = viewModel
                            )
                            AppScreen.SETTINGS -> SettingsScreen(
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
