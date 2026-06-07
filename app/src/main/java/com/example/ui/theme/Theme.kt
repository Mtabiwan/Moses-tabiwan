package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreenDark,
    onPrimary = DarkBackground,
    secondary = SecondaryGoldDark,
    onSecondary = DarkBackground,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = TextLight,
    onSurface = TextLight,
    error = GhanaRed
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = OnPrimaryWhite,
    secondary = SecondaryGold,
    onSecondary = OnSecondaryWhite,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = TextDark,
    onSurface = TextDark,
    error = GhanaRed,
    secondaryContainer = GhanaYellow,
    onSecondaryContainer = SecondaryGold
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set default dynamicColor to false to maintain branding
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
