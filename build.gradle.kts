// Top-level build file.
// Convention plugins (shakelamp.*) are provided by the build-logic composite build.
// External plugins are declared here with apply false so they are available on the
// classpath for subprojects without being applied at the root level.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.koin.compiler) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}