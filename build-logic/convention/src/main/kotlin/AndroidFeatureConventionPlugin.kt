import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin for feature modules.
 * Bundles [AndroidLibraryConventionPlugin] + [AndroidLibraryComposeConventionPlugin]
 * and adds the shared ViewModel/Lifecycle dependency every feature needs.
 *
 * Usage in a feature module's build.gradle.kts:
 *   plugins { alias(libs.plugins.shakelamp.android.feature) }
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("shakelamp.android.library")
                apply("shakelamp.android.library.compose")
            }
            dependencies {
                add("implementation", libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
                add("implementation", libs.findLibrary("androidx-core-ktx").get())
            }
            addKoinComposeDependencies()
            addNavigation3RuntimeDependency()
        }
    }
}
