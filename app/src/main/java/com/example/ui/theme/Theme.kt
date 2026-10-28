package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class FXThemeMode(val title: String, val subtitle: String) {
    FX_BLACK("FX Black", "Graphite black, titanium surfaces, emerald green accents"),
    FX_PRISM("FX Prism", "Clean white, silver details, subtle blue accents"),
    FX_HYBRID("FX Hybrid", "Adaptive system mode following Android display theme")
}

val FXBlackColorScheme = darkColorScheme(
    primary = FXEmerald,
    onPrimary = Color(0xFF032414),
    primaryContainer = FXEmeraldContainer,
    onPrimaryContainer = FXEmeraldOnContainer,
    secondary = FXTitanium,
    onSecondary = Color(0xFF090D14),
    secondaryContainer = FXBlackSurfaceVariant,
    onSecondaryContainer = FXBlackTextPrimary,
    tertiary = FXSecurityCyan,
    onTertiary = Color.Black,
    background = FXBlackBackground,
    onBackground = FXBlackTextPrimary,
    surface = FXBlackSurface,
    onSurface = FXBlackTextPrimary,
    surfaceVariant = FXBlackSurfaceElevated,
    onSurfaceVariant = FXBlackTextSecondary,
    outline = FXBlackOutline,
    error = FXSecurityRed,
    onError = Color.White
)

val FXPrismColorScheme = lightColorScheme(
    primary = FXPrismBlue,
    onPrimary = Color.White,
    primaryContainer = FXPrismBlueContainer,
    onPrimaryContainer = FXPrismBlueOnContainer,
    secondary = FXTitanium,
    onSecondary = Color.White,
    secondaryContainer = FXPrismSurfaceVariant,
    onSecondaryContainer = FXPrismTextPrimary,
    tertiary = Color(0xFF0284C7),
    onTertiary = Color.White,
    background = FXPrismBackground,
    onBackground = FXPrismTextPrimary,
    surface = FXPrismSurface,
    onSurface = FXPrismTextPrimary,
    surfaceVariant = FXPrismSurfaceElevated,
    onSurfaceVariant = FXPrismTextSecondary,
    outline = FXPrismOutline,
    error = FXSecurityRed,
    onError = Color.White
)

@Composable
fun FXSecurityTheme(
    themeMode: FXThemeMode = FXThemeMode.FX_HYBRID,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        FXThemeMode.FX_BLACK -> true
        FXThemeMode.FX_PRISM -> false
        FXThemeMode.FX_HYBRID -> systemInDark
    }

    val colorScheme = if (isDark) FXBlackColorScheme else FXPrismColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !isDark
            controller.isAppearanceLightNavigationBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
