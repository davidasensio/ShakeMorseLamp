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
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(testFixtures(project(":core:common")))
}
