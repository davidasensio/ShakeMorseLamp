package com.handysparksoft.shakelamp.core.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * Color palettes sourced from the Stitch design system (project "Shake Morse Flashlight").
 * [Dark] is "Lumen Utility"; [Light] is "Luminous Ecological" — Stitch designed the light
 * mode as a separate palette rather than a tonal inversion of the dark one, but both use the
 * same Material3 color-role names, so [Theme] can map either straight into a [ColorScheme].
 */
object SMLColor {
    object Dark {
        val Primary = Color(0xFFFFFFFF)
        val OnPrimary = Color(0xFF2A3400)
        val PrimaryContainer = Color(0xFFCBF200)
        val OnPrimaryContainer = Color(0xFF596C00)
        val InversePrimary = Color(0xFF546500)

        val Secondary = Color(0xFFB9D15F)
        val OnSecondary = Color(0xFF2A3400)
        val SecondaryContainer = Color(0xFF5F7201)
        val OnSecondaryContainer = Color(0xFFDFF781)

        val Tertiary = Color(0xFFFFFFFF)
        val OnTertiary = Color(0xFF1A343D)
        val TertiaryContainer = Color(0xFFCCE7F3)
        val OnTertiaryContainer = Color(0xFF4F6872)

        val Error = Color(0xFFFFB4AB)
        val OnError = Color(0xFF690005)
        val ErrorContainer = Color(0xFF93000A)
        val OnErrorContainer = Color(0xFFFFDAD6)

        val Background = Color(0xFF121508)
        val OnBackground = Color(0xFFE2E4CF)

        val Surface = Color(0xFF121508)
        val OnSurface = Color(0xFFE2E4CF)
        val SurfaceVariant = Color(0xFF333627)
        val OnSurfaceVariant = Color(0xFFC5C9AC)
        val SurfaceDim = Color(0xFF121508)
        val SurfaceBright = Color(0xFF383B2C)
        val SurfaceContainerLowest = Color(0xFF0C0F04)
        val SurfaceContainerLow = Color(0xFF1A1D10)
        val SurfaceContainer = Color(0xFF1E2113)
        val SurfaceContainerHigh = Color(0xFF282B1D)
        val SurfaceContainerHighest = Color(0xFF333627)
        val SurfaceTint = Color(0xFFB2D400)

        val Outline = Color(0xFF8F9378)
        val OutlineVariant = Color(0xFF454932)
        val InverseSurface = Color(0xFFE2E4CF)
        val InverseOnSurface = Color(0xFF2F3223)

        val PrimaryFixed = Color(0xFFCBF200)
        val PrimaryFixedDim = Color(0xFFB2D400)
        val OnPrimaryFixed = Color(0xFF171E00)
        val OnPrimaryFixedVariant = Color(0xFF3E4C00)
        val SecondaryFixed = Color(0xFFD5ED78)
        val SecondaryFixedDim = Color(0xFFB9D15F)
        val OnSecondaryFixed = Color(0xFF181E00)
        val OnSecondaryFixedVariant = Color(0xFF3F4C00)
        val TertiaryFixed = Color(0xFFCCE7F3)
        val TertiaryFixedDim = Color(0xFFB0CBD6)
        val OnTertiaryFixed = Color(0xFF031F27)
        val OnTertiaryFixedVariant = Color(0xFF314A54)
    }

    object Light {
        val Primary = Color(0xFF536500)
        val OnPrimary = Color(0xFFFFFFFF)
        val PrimaryContainer = Color(0xFFB9D15F)
        val OnPrimaryContainer = Color(0xFF495900)
        val InversePrimary = Color(0xFFB9D15F)

        val Secondary = Color(0xFF5D5E61)
        val OnSecondary = Color(0xFFFFFFFF)
        val SecondaryContainer = Color(0xFFE2E2E5)
        val OnSecondaryContainer = Color(0xFF636467)

        val Tertiary = Color(0xFF5B5F5A)
        val OnTertiary = Color(0xFFFFFFFF)
        val TertiaryContainer = Color(0xFFC4C8C1)
        val OnTertiaryContainer = Color(0xFF50544F)

        val Error = Color(0xFFBA1A1A)
        val OnError = Color(0xFFFFFFFF)
        val ErrorContainer = Color(0xFFFFDAD6)
        val OnErrorContainer = Color(0xFF93000A)

        val Background = Color(0xFFF9F9FF)
        val OnBackground = Color(0xFF151C27)

        val Surface = Color(0xFFF9F9FF)
        val OnSurface = Color(0xFF151C27)
        val SurfaceVariant = Color(0xFFDCE2F3)
        val OnSurfaceVariant = Color(0xFF454838)
        val SurfaceDim = Color(0xFFD3DAEA)
        val SurfaceBright = Color(0xFFF9F9FF)
        val SurfaceContainerLowest = Color(0xFFFFFFFF)
        val SurfaceContainerLow = Color(0xFFF0F3FF)
        val SurfaceContainer = Color(0xFFE7EEFE)
        val SurfaceContainerHigh = Color(0xFFE2E8F8)
        val SurfaceContainerHighest = Color(0xFFDCE2F3)
        val SurfaceTint = Color(0xFF536500)

        val Outline = Color(0xFF767967)
        val OutlineVariant = Color(0xFFC6C8B3)
        val InverseSurface = Color(0xFF2A313D)
        val InverseOnSurface = Color(0xFFEBF1FF)

        val PrimaryFixed = Color(0xFFD5EE78)
        val PrimaryFixedDim = Color(0xFFB9D15F)
        val OnPrimaryFixed = Color(0xFF171E00)
        val OnPrimaryFixedVariant = Color(0xFF3E4C00)
        val SecondaryFixed = Color(0xFFE2E2E5)
        val SecondaryFixedDim = Color(0xFFC6C6C9)
        val OnSecondaryFixed = Color(0xFF1A1C1E)
        val OnSecondaryFixedVariant = Color(0xFF454749)
        val TertiaryFixed = Color(0xFFE0E3DD)
        val TertiaryFixedDim = Color(0xFFC4C8C1)
        val OnTertiaryFixed = Color(0xFF191D19)
        val OnTertiaryFixedVariant = Color(0xFF444843)
    }
}
