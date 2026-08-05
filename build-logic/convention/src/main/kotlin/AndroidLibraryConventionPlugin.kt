import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Note: org.jetbrains.kotlin.android is auto-applied by AGP 9.x
            // when Kotlin sources are detected — do NOT apply it manually.
            pluginManager.apply("com.android.library")
            extensions.configure<LibraryExtension> {
                configureKotlinAndroidLib(this)
                defaultConfig.consumerProguardFiles("consumer-rules.pro")
            }
        }
    }
}
