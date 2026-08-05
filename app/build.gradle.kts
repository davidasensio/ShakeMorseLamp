plugins {
    alias(libs.plugins.shakelamp.android.application)
    alias(libs.plugins.shakelamp.android.application.compose)
}

android {
    namespace = "com.handysparksoft.shakelamp"

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
    implementation(project(":core:designsystem"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}