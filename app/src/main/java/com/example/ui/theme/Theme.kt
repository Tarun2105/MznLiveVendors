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
    primary = MznBlueLight,
    onPrimary = MznNavyDark,
    primaryContainer = MznBlueDark,
    onPrimaryContainer = Color.White,
    secondary = MznRedAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4A1015),
    onSecondaryContainer = MznRedLight,
    tertiary = MznGold,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF4A3800),
    onTertiaryContainer = MznGold,
    background = MznNavyDark,
    onBackground = TextPrimaryDark,
    surface = MznNavySurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = MznNavyCard,
    onSurfaceVariant = TextSecondaryDark,
    outline = MznNavyBorder
)

private val LightColorScheme = lightColorScheme(
    primary = MznBluePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = MznBlueDark,
    secondary = MznRedAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFEBEE),
    onSecondaryContainer = Color(0xFFB71C1C),
    tertiary = MznOrange,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFF3E0),
    onTertiaryContainer = Color(0xFFE65100),
    background = MznBgLight,
    onBackground = TextPrimaryLight,
    surface = MznSurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = MznBorderLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = MznBorderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve branded theme by default
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
