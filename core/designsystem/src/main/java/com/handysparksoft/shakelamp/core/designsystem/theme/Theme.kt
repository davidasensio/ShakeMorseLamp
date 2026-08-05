package com.handysparksoft.shakelamp.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme =
    darkColorScheme(
        primary = SMLColor.Dark.Primary,
        onPrimary = SMLColor.Dark.OnPrimary,
        primaryContainer = SMLColor.Dark.PrimaryContainer,
        onPrimaryContainer = SMLColor.Dark.OnPrimaryContainer,
        inversePrimary = SMLColor.Dark.InversePrimary,
        secondary = SMLColor.Dark.Secondary,
        onSecondary = SMLColor.Dark.OnSecondary,
        secondaryContainer = SMLColor.Dark.SecondaryContainer,
        onSecondaryContainer = SMLColor.Dark.OnSecondaryContainer,
        tertiary = SMLColor.Dark.Tertiary,
        onTertiary = SMLColor.Dark.OnTertiary,
        tertiaryContainer = SMLColor.Dark.TertiaryContainer,
        onTertiaryContainer = SMLColor.Dark.OnTertiaryContainer,
        background = SMLColor.Dark.Background,
        onBackground = SMLColor.Dark.OnBackground,
        surface = SMLColor.Dark.Surface,
        onSurface = SMLColor.Dark.OnSurface,
        surfaceVariant = SMLColor.Dark.SurfaceVariant,
        onSurfaceVariant = SMLColor.Dark.OnSurfaceVariant,
        surfaceTint = SMLColor.Dark.SurfaceTint,
        inverseSurface = SMLColor.Dark.InverseSurface,
        inverseOnSurface = SMLColor.Dark.InverseOnSurface,
        error = SMLColor.Dark.Error,
        onError = SMLColor.Dark.OnError,
        errorContainer = SMLColor.Dark.ErrorContainer,
        onErrorContainer = SMLColor.Dark.OnErrorContainer,
        outline = SMLColor.Dark.Outline,
        outlineVariant = SMLColor.Dark.OutlineVariant,
        surfaceBright = SMLColor.Dark.SurfaceBright,
        surfaceDim = SMLColor.Dark.SurfaceDim,
        surfaceContainer = SMLColor.Dark.SurfaceContainer,
        surfaceContainerHigh = SMLColor.Dark.SurfaceContainerHigh,
        surfaceContainerHighest = SMLColor.Dark.SurfaceContainerHighest,
        surfaceContainerLow = SMLColor.Dark.SurfaceContainerLow,
        surfaceContainerLowest = SMLColor.Dark.SurfaceContainerLowest,
        primaryFixed = SMLColor.Dark.PrimaryFixed,
        primaryFixedDim = SMLColor.Dark.PrimaryFixedDim,
        onPrimaryFixed = SMLColor.Dark.OnPrimaryFixed,
        onPrimaryFixedVariant = SMLColor.Dark.OnPrimaryFixedVariant,
        secondaryFixed = SMLColor.Dark.SecondaryFixed,
        secondaryFixedDim = SMLColor.Dark.SecondaryFixedDim,
        onSecondaryFixed = SMLColor.Dark.OnSecondaryFixed,
        onSecondaryFixedVariant = SMLColor.Dark.OnSecondaryFixedVariant,
        tertiaryFixed = SMLColor.Dark.TertiaryFixed,
        tertiaryFixedDim = SMLColor.Dark.TertiaryFixedDim,
        onTertiaryFixed = SMLColor.Dark.OnTertiaryFixed,
        onTertiaryFixedVariant = SMLColor.Dark.OnTertiaryFixedVariant,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = SMLColor.Light.Primary,
        onPrimary = SMLColor.Light.OnPrimary,
        primaryContainer = SMLColor.Light.PrimaryContainer,
        onPrimaryContainer = SMLColor.Light.OnPrimaryContainer,
        inversePrimary = SMLColor.Light.InversePrimary,
        secondary = SMLColor.Light.Secondary,
        onSecondary = SMLColor.Light.OnSecondary,
        secondaryContainer = SMLColor.Light.SecondaryContainer,
        onSecondaryContainer = SMLColor.Light.OnSecondaryContainer,
        tertiary = SMLColor.Light.Tertiary,
        onTertiary = SMLColor.Light.OnTertiary,
        tertiaryContainer = SMLColor.Light.TertiaryContainer,
        onTertiaryContainer = SMLColor.Light.OnTertiaryContainer,
        background = SMLColor.Light.Background,
        onBackground = SMLColor.Light.OnBackground,
        surface = SMLColor.Light.Surface,
        onSurface = SMLColor.Light.OnSurface,
        surfaceVariant = SMLColor.Light.SurfaceVariant,
        onSurfaceVariant = SMLColor.Light.OnSurfaceVariant,
        surfaceTint = SMLColor.Light.SurfaceTint,
        inverseSurface = SMLColor.Light.InverseSurface,
        inverseOnSurface = SMLColor.Light.InverseOnSurface,
        error = SMLColor.Light.Error,
        onError = SMLColor.Light.OnError,
        errorContainer = SMLColor.Light.ErrorContainer,
        onErrorContainer = SMLColor.Light.OnErrorContainer,
        outline = SMLColor.Light.Outline,
        outlineVariant = SMLColor.Light.OutlineVariant,
        surfaceBright = SMLColor.Light.SurfaceBright,
        surfaceDim = SMLColor.Light.SurfaceDim,
        surfaceContainer = SMLColor.Light.SurfaceContainer,
        surfaceContainerHigh = SMLColor.Light.SurfaceContainerHigh,
        surfaceContainerHighest = SMLColor.Light.SurfaceContainerHighest,
        surfaceContainerLow = SMLColor.Light.SurfaceContainerLow,
        surfaceContainerLowest = SMLColor.Light.SurfaceContainerLowest,
        primaryFixed = SMLColor.Light.PrimaryFixed,
        primaryFixedDim = SMLColor.Light.PrimaryFixedDim,
        onPrimaryFixed = SMLColor.Light.OnPrimaryFixed,
        onPrimaryFixedVariant = SMLColor.Light.OnPrimaryFixedVariant,
        secondaryFixed = SMLColor.Light.SecondaryFixed,
        secondaryFixedDim = SMLColor.Light.SecondaryFixedDim,
        onSecondaryFixed = SMLColor.Light.OnSecondaryFixed,
        onSecondaryFixedVariant = SMLColor.Light.OnSecondaryFixedVariant,
        tertiaryFixed = SMLColor.Light.TertiaryFixed,
        tertiaryFixedDim = SMLColor.Light.TertiaryFixedDim,
        onTertiaryFixed = SMLColor.Light.OnTertiaryFixed,
        onTertiaryFixedVariant = SMLColor.Light.OnTertiaryFixedVariant,
    )

/**
 * Sourced from the Stitch design system — dark mode is "Lumen Utility" (a Night Mode Utility
 * aesthetic), light mode is "Luminous Ecological". Both palettes share the same typography,
 * shape, and spacing tokens; only the color roles differ. Follows the system light/dark
 * setting by default, but can be overridden (e.g. for an in-app theme picker or tests).
 */
@Composable
fun ShakeMorseLampTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme: ColorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = SMLTypography,
        shapes = SMLShapes,
        content = content,
    )
}
