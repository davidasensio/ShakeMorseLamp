package com.handysparksoft.shakelamp.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

// Inter / JetBrains Mono aren't bundled yet — these fall back to the closest system family
// until real font resources are added. Swap here only; the rest of the type scale won't change.
private val SansFamily = FontFamily.SansSerif
private val MonoFamily = FontFamily.Monospace

/** Type scale sourced from the Stitch design system (display/headline-mobile/body/label). */
val SMLTypography =
    Typography(
        displayLarge =
            TextStyle(
                fontFamily = SansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 56.sp,
                lineHeight = 64.sp,
                letterSpacing = (-0.02).em,
            ),
        headlineLarge =
            TextStyle(
                fontFamily = SansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 28.sp,
                lineHeight = 36.sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = SansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = MonoFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.05.em,
            ),
    )
