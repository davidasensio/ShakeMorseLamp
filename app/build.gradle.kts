import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.shakelamp.android.application)
    alias(libs.plugins.shakelamp.android.application.compose)
    alias(libs.plugins.shakelamp.android.roborazzi)
}

fun ProviderFactory.propertyOrEnv(name: String): String? =
    gradleProperty(name)
        .orElse(environmentVariable(name))
        .orNull
        ?.takeIf { it.isNotBlank() }

// Signing material is git-ignored and absent on CI and on a fresh clone, so a missing file must
// leave debug builds working rather than fail configuration. Gradle properties and environment
// variables take precedence so CI can supply credentials without the file existing at all.
val keystorePropertiesFile: File = rootProject.file("./keystore/keystore_old/keystore.properties") // Since this an old app, needs the keystore_old to manage certificates
val keystoreProperties = Properties()
try {
    FileInputStream(keystorePropertiesFile).use(keystoreProperties::load)
} catch (_: Exception) {
    println("WARNING! Keystore files not found! KeystoreProperties couldn't be loaded")
}
val signingKeyAlias: String? =
    providers.propertyOrEnv("SIGNING_KEY_ALIAS") ?: keystoreProperties.getProperty("keyAlias")
val signingKeyPassword: String? =
    providers.propertyOrEnv("SIGNING_KEY_PASSWORD") ?: keystoreProperties.getProperty("keyPassword")
val signingStoreFilePath: String? =
    providers.propertyOrEnv("SIGNING_STORE_FILE") ?: keystoreProperties.getProperty("storeFile")
val signingStorePassword: String? =
    providers.propertyOrEnv("SIGNING_STORE_PASSWORD") ?: keystoreProperties.getProperty("storePassword")

android {
    namespace = "com.handysparksoft.shakelamp"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.handysparksoft.shakelamp"
        versionCode = 20
        versionName = "2.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            try {
                keyAlias = signingKeyAlias ?: error("Missing signing key alias")
                keyPassword = signingKeyPassword ?: error("Missing signing key password")
                storeFile = file(signingStoreFilePath ?: error("Missing signing store file"))
                storePassword = signingStorePassword ?: error("Missing signing store password")
            } catch (_: Exception) {
                println("WARNING! KeystoreProperties are not loaded!")
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
