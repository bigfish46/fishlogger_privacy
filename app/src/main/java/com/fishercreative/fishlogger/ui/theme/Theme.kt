package com.fishercreative.fishlogger.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DeepCrimsonRed,
    onPrimary = BrightWhite,
    primaryContainer = DarkCharcoalBlack,
    onPrimaryContainer = BrightWhite,
    secondary = SteelGray,
    onSecondary = BrightWhite,
    secondaryContainer = Color(0xFF1D1E20), // Slightly lighter than background for containers
    onSecondaryContainer = BrightWhite,
    tertiary = MediumGray,
    onTertiary = BrightWhite,
    tertiaryContainer = Color(0xFF1D1E20), // Slightly lighter than background for containers
    onTertiaryContainer = BrightWhite,
    background = DarkCharcoalBlack,
    onBackground = BrightWhite,
    surface = Color(0xFF1A1B1D), // Slightly lighter than background for cards
    onSurface = BrightWhite,
    surfaceVariant = Color(0xFF252628), // Even lighter for variant surfaces
    onSurfaceVariant = LightGray,
    outline = MediumGray,
    outlineVariant = SteelGray
)

private val LightColorScheme = lightColorScheme(
    primary = DeepCrimsonRed,
    onPrimary = BrightWhite,
    primaryContainer = Color(0xFFF0F0F0), // Slightly darker than background for containers
    onPrimaryContainer = DarkCharcoalBlack,
    secondary = SteelGray,
    onSecondary = BrightWhite,
    secondaryContainer = Color(0xFFE8E8E8), // Slightly darker than background for containers
    onSecondaryContainer = DarkCharcoalBlack,
    tertiary = MediumGray,
    onTertiary = BrightWhite,
    tertiaryContainer = Color(0xFFE8E8E8), // Slightly darker than background for containers
    onTertiaryContainer = DarkCharcoalBlack,
    background = BrightWhite,
    onBackground = DarkCharcoalBlack,
    surface = Color(0xFFFAFAFA), // Slightly darker than background for cards
    onSurface = DarkCharcoalBlack,
    surfaceVariant = Color(0xFFE8E8E8), // Even darker for variant surfaces
    onSurfaceVariant = SteelGray,
    outline = MediumGray,
    outlineVariant = SteelGray
)

@Composable
fun FishLoggerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}