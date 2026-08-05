import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin that wires Roborazzi (Robolectric-based Compose screenshot testing)
 * to any module that opts in.
 *
 * Opt-in usage in a module's build.gradle.kts:
 *   plugins { alias(libs.plugins.shakelamp.android.roborazzi) }
 *
 * Tasks added:
 *   - recordRoborazziDebug   (record screenshot baselines)
 *   - verifyRoborazziDebug   (verify screenshots against baselines)
 *
 * Library modules have no real targetSdk, so Robolectric's SDK auto-detection falls back to
 * compileSdk — which is ahead of what Robolectric ships shadows for. Annotate every Roborazzi
 * test class with `@Config(sdk = [36])` (matching AGENTS.md's Target SDK) to pin the runtime
 * platform explicitly and skip that auto-detection.
 */
class AndroidRoborazziConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.github.takahirom.roborazzi")

            extensions.configure<LibraryExtension> {
                testOptions.unitTests {
                    isIncludeAndroidResources = true
                    isReturnDefaultValues = true
                }
            }

            dependencies {
                add("testImplementation", libs.findLibrary("roborazzi").get())
                add("testImplementation", libs.findLibrary("roborazzi-compose").get())
                add("testImplementation", libs.findLibrary("roborazzi-junit-rule").get())
                add("testImplementation", libs.findLibrary("robolectric").get())
            }
        }
    }
}
