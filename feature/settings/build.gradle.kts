plugins {
    alias(libs.plugins.shakelamp.android.feature)
    alias(libs.plugins.shakelamp.android.quality)
    alias(libs.plugins.shakelamp.android.jacoco)
}

android {
    namespace = "com.handysparksoft.shakelamp.feature.settings"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:morse"))
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    testImplementation(testFixtures(project(":core:common")))
}
