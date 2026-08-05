package com.handysparksoft.shakelamp.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme =
    darkColorScheme(
        primary = SMLColor.Primary,
        onPrimary = SMLColor.OnPrimary,
        primaryContainer = SMLColor.PrimaryContainer,
        onPrimaryContainer = SMLColor.OnPrimaryContainer,
        inversePrimary = SMLColor.InversePrimary,
        secondary = SMLColor.Secondary,
        onSecondary = SMLColor.OnSecondary,
        secondaryContainer = SMLColor.SecondaryContainer,
        onSecondaryContainer = SMLColor.OnSecondaryContainer,
        tertiary = SMLColor.Tertiary,
        onTertiary = SMLColor.OnTertiary,
        tertiaryContainer = SMLColor.TertiaryContainer,
        onTertiaryContainer = SMLColor.OnTertiaryContainer,
        background = SMLColor.Background,
        onBackground = SMLColor.OnBackground,
        surface = SMLColor.Surface,
        onSurface = SMLColor.OnSurface,
        surfaceVariant = SMLColor.SurfaceVariant,
        onSurfaceVariant = SMLColor.OnSurfaceVariant,
        surfaceTint = SMLColor.SurfaceTint,
        inverseSurface = SMLColor.InverseSurface,
        inverseOnSurface = SMLColor.InverseOnSurface,
        error = SMLColor.Error,
        onError = SMLColor.OnError,
        errorContainer = SMLColor.ErrorContainer,
        onErrorContainer = SMLColor.OnErrorContainer,
        outline = SMLColor.Outline,
        outlineVariant = SMLColor.OutlineVariant,
        surfaceBright = SMLColor.SurfaceBright,
        surfaceDim = SMLColor.SurfaceDim,
        surfaceContainer = SMLColor.SurfaceContainer,
        surfaceContainerHigh = SMLColor.SurfaceContainerHigh,
        surfaceContainerHighest = SMLColor.SurfaceContainerHighest,
        surfaceContainerLow = SMLColor.SurfaceContainerLow,
        surfaceContainerLowest = SMLColor.SurfaceContainerLowest,
        primaryFixed = SMLColor.PrimaryFixed,
        primaryFixedDim = SMLColor.PrimaryFixedDim,
        onPrimaryFixed = SMLColor.OnPrimaryFixed,
        onPrimaryFixedVariant = SMLColor.OnPrimaryFixedVariant,
        secondaryFixed = SMLColor.SecondaryFixed,
        secondaryFixedDim = SMLColor.SecondaryFixedDim,
        onSecondaryFixed = SMLColor.OnSecondaryFixed,
        onSecondaryFixedVariant = SMLColor.OnSecondaryFixedVariant,
        tertiaryFixed = SMLColor.TertiaryFixed,
        tertiaryFixedDim = SMLColor.TertiaryFixedDim,
        onTertiaryFixed = SMLColor.OnTertiaryFixed,
        onTertiaryFixedVariant = SMLColor.OnTertiaryFixedVariant,
    )

/**
 * Sourced from the Stitch design system's "Lumen Utility" — a Night Mode Utility aesthetic.
 * Dark-only by design: no light palette has been designed yet, so this theme does not
 * follow the system light/dark setting.
 */
@Composable
fun ShakeMorseLampTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = SMLTypography,
        shapes = SMLShapes,
        content = content,
    )
}
