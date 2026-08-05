package com.handysparksoft.shakelamp.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val LumenDarkColorScheme =
    darkColorScheme(
        primary = LumenColor.Primary,
        onPrimary = LumenColor.OnPrimary,
        primaryContainer = LumenColor.PrimaryContainer,
        onPrimaryContainer = LumenColor.OnPrimaryContainer,
        inversePrimary = LumenColor.InversePrimary,
        secondary = LumenColor.Secondary,
        onSecondary = LumenColor.OnSecondary,
        secondaryContainer = LumenColor.SecondaryContainer,
        onSecondaryContainer = LumenColor.OnSecondaryContainer,
        tertiary = LumenColor.Tertiary,
        onTertiary = LumenColor.OnTertiary,
        tertiaryContainer = LumenColor.TertiaryContainer,
        onTertiaryContainer = LumenColor.OnTertiaryContainer,
        background = LumenColor.Background,
        onBackground = LumenColor.OnBackground,
        surface = LumenColor.Surface,
        onSurface = LumenColor.OnSurface,
        surfaceVariant = LumenColor.SurfaceVariant,
        onSurfaceVariant = LumenColor.OnSurfaceVariant,
        surfaceTint = LumenColor.SurfaceTint,
        inverseSurface = LumenColor.InverseSurface,
        inverseOnSurface = LumenColor.InverseOnSurface,
        error = LumenColor.Error,
        onError = LumenColor.OnError,
        errorContainer = LumenColor.ErrorContainer,
        onErrorContainer = LumenColor.OnErrorContainer,
        outline = LumenColor.Outline,
        outlineVariant = LumenColor.OutlineVariant,
        surfaceBright = LumenColor.SurfaceBright,
        surfaceDim = LumenColor.SurfaceDim,
        surfaceContainer = LumenColor.SurfaceContainer,
        surfaceContainerHigh = LumenColor.SurfaceContainerHigh,
        surfaceContainerHighest = LumenColor.SurfaceContainerHighest,
        surfaceContainerLow = LumenColor.SurfaceContainerLow,
        surfaceContainerLowest = LumenColor.SurfaceContainerLowest,
        primaryFixed = LumenColor.PrimaryFixed,
        primaryFixedDim = LumenColor.PrimaryFixedDim,
        onPrimaryFixed = LumenColor.OnPrimaryFixed,
        onPrimaryFixedVariant = LumenColor.OnPrimaryFixedVariant,
        secondaryFixed = LumenColor.SecondaryFixed,
        secondaryFixedDim = LumenColor.SecondaryFixedDim,
        onSecondaryFixed = LumenColor.OnSecondaryFixed,
        onSecondaryFixedVariant = LumenColor.OnSecondaryFixedVariant,
        tertiaryFixed = LumenColor.TertiaryFixed,
        tertiaryFixedDim = LumenColor.TertiaryFixedDim,
        onTertiaryFixed = LumenColor.OnTertiaryFixed,
        onTertiaryFixedVariant = LumenColor.OnTertiaryFixedVariant,
    )

/**
 * "Lumen Utility" — a Night Mode Utility aesthetic sourced from the Stitch design system.
 * Dark-only by design: no light palette has been designed yet, so this theme does not
 * follow the system light/dark setting.
 */
@Composable
fun ShakeMorseLampTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LumenDarkColorScheme,
        typography = LumenTypography,
        shapes = LumenShapes,
        content = content,
    )
}
