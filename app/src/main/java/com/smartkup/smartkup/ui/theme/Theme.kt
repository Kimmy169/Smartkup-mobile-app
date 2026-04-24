package com.smartkup.smartkup.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CatppuccinDarkScheme = darkColorScheme(
    primary = MochaPrimary,
    secondary = MochaSecondary,
    background = MochaBase,
    surface = MochaSurface,
    surfaceVariant = MochaSurface,
    error = MochaError,
    onPrimary = MochaBase,
    onSecondary = MochaBase,
    onBackground = MochaText,
    onSurface = MochaText,
    onSurfaceVariant = MochaSubtext,
    onError = MochaBase
)

private val CatppuccinLightScheme = lightColorScheme(
    primary = LattePrimary,
    secondary = LatteSecondary,
    background = LatteBase,
    surface = LatteSurface,
    surfaceVariant = LatteSurface,
    error = LatteError,
    onPrimary = LatteBase,
    onSecondary = LatteBase,
    onBackground = LatteText,
    onSurface = LatteText,
    onSurfaceVariant = LatteSubtext,
    onError = LatteBase
)

@Composable
fun SmartkupTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        CatppuccinDarkScheme
    } else {
        CatppuccinLightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}