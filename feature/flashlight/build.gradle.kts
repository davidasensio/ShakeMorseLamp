plugins {
    alias(libs.plugins.shakelamp.android.feature)
    alias(libs.plugins.shakelamp.android.quality)
    alias(libs.plugins.shakelamp.android.jacoco)
}

android {
    namespace = "com.handysparksoft.shakelamp.feature.flashlight"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:morse"))

    testImplementation(testFixtures(project(":core:common")))
}
