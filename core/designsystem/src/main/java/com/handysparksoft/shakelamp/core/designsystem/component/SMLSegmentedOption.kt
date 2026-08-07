package com.handysparksoft.shakelamp.core.designsystem.component

import androidx.compose.ui.graphics.painter.Painter

data class SMLSegmentedOption<T>(
    val value: T,
    val label: String,
    val icon: Painter? = null,
)
