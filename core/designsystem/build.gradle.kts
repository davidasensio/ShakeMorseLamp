plugins {
    alias(libs.plugins.shakelamp.android.library)
    alias(libs.plugins.shakelamp.android.library.compose)
    alias(libs.plugins.shakelamp.android.quality)
    alias(libs.plugins.shakelamp.android.roborazzi)
}

android {
    namespace = "com.handysparksoft.shakelamp.core.designsystem"
}

dependencies {
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.compose.ui.test.manifest)
}
