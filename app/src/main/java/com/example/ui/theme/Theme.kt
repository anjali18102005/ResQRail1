package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = Color(0xFF00112B),
    secondary = CyanAccentDark,
    background = AppBackgroundDark,
    surface = Color(0xFF04142F), // Darker Navy
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    outline = ColorBorderDark,
    error = ColorDanger,
    primaryContainer = NavyPrimary,
    onPrimaryContainer = TextPrimaryDark,
    secondaryContainer = AppBackgroundDarkSecondary,
    onSecondaryContainer = TextSecondaryDark,
    surfaceVariant = AppBackgroundDarkSecondary,
    onSurfaceVariant = TextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary, // Skyscanner Royal Blue as Primary in Light Mode!
    onPrimary = Color.White,
    secondary = Color(0xFF0284C7),
    background = AppBackgroundLight, // Whitish
    surface = AppBackgroundLightSecondary, // Pure White Card Background
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    outline = ColorBorder,
    error = ColorDanger,
    primaryContainer = Color(0xFFE2E8F0),
    onPrimaryContainer = TextPrimaryLight,
    secondaryContainer = AppBackgroundLightSecondary,
    onSecondaryContainer = TextSecondaryLight,
    surfaceVariant = AppBackgroundLightSecondary,
    onSurfaceVariant = TextSecondaryLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set default dynamicColor to false to preserve specific ResQRail brand colors
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
