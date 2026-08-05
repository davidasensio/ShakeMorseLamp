import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.handysparksoft.shakelamp.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.ktlint.gradlePlugin)
    compileOnly(libs.koin.compiler.gradlePlugin)
    compileOnly(libs.roborazzi.gradlePlugin)
    compileOnly(libs.kotlin.serialization.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "shakelamp.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "shakelamp.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "shakelamp.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "shakelamp.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "shakelamp.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("jvmLibrary") {
            id = "shakelamp.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("androidQuality") {
            id = "shakelamp.android.quality"
            implementationClass = "AndroidQualityConventionPlugin"
        }
        register("androidJacoco") {
            id = "shakelamp.android.jacoco"
            implementationClass = "AndroidJacocoConventionPlugin"
        }
        register("androidRoborazzi") {
            id = "shakelamp.android.roborazzi"
            implementationClass = "AndroidRoborazziConventionPlugin"
        }
    }
}
