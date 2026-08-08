plugins {
    alias(libs.plugins.shakelamp.android.application)
    alias(libs.plugins.shakelamp.android.application.compose)
    alias(libs.plugins.shakelamp.android.roborazzi)
}

android {
    namespace = "com.handysparksoft.shakelamp"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.handysparksoft.shakelamp"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:morse"))
    implementation(project(":feature:flashlight"))
    implementation(project(":feature:settings"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}