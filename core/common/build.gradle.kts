plugins {
    alias(libs.plugins.shakelamp.android.library)
    alias(libs.plugins.shakelamp.android.quality)
}

android {
    namespace = "com.handysparksoft.shakelamp.core.common"
    testFixtures {
        enable = true
    }
}

dependencies {
    implementation(libs.androidx.datastore.preferences)

    testFixturesImplementation(platform(libs.junit5.bom))
    testFixturesImplementation(libs.junit5.jupiter)
    testFixturesImplementation(libs.kotlinx.coroutines.test)
}
