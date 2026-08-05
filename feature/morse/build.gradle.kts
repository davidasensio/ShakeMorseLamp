plugins {
    alias(libs.plugins.shakelamp.android.feature)
    alias(libs.plugins.shakelamp.android.quality)
    alias(libs.plugins.shakelamp.android.jacoco)
}

android {
    namespace = "com.handysparksoft.shakelamp.feature.morse"
}
