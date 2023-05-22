package com.example.gymlog.ui.theme

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

@Composable
fun GymLogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

private val LightColors = lightColorScheme(
    primary = PrimaryLightColor,
    onPrimary = Color.White,
    secondary = PrimaryLightColor,
    onSecondary = Color.White,
    primaryContainer = PrimaryContainerLightColor,
    onPrimaryContainer = OnPrimaryContainerLightColor,
    secondaryContainer = PrimaryContainerLightColor,
    onSecondaryContainer = OnPrimaryContainerLightColor,
    surface = SurfaceLightColor,
    surfaceVariant = SurfaceVariantLightColor,
    outline = OutlineLightColor,
    background = BackgroundLightColor,
    onBackground = OnBackgroundLightColor,
    onSurface = OnSurfaceLightColor,
    onSurfaceVariant = OnSurfaceVariantLightColor
)

private val DarkColors = darkColorScheme(
    primary = PrimaryDarkColor,
    onPrimary = OnPrimaryDarkColor,
    secondary = PrimaryDarkColor,
    onSecondary = OnPrimaryDarkColor,
    primaryContainer = PrimaryContainerDarkColor,
    onPrimaryContainer = OnPrimaryContainerDarkColor,
    secondaryContainer = PrimaryContainerDarkColor,
    onSecondaryContainer = OnPrimaryContainerDarkColor,
    surface = SurfaceDarkColor,
    surfaceVariant = SurfaceVariantDarkColor,
    outline = OutlineDarkColor,
    background = BackgroundDarkColor,
    onBackground = OnBackgroundDarkColor,
    onSurface = OnSurfaceDarkColor,
    onSurfaceVariant = OnSurfaceVariantDarkColor
)