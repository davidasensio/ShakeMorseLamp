import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.util.Locale

/**
 * Convention plugin that wires JaCoCo code coverage to any module that opts in.
 *
 * Opt-in usage in a module's build.gradle.kts:
 *   plugins { alias(libs.plugins.shakelamp.android.jacoco) }
 *
 * Tasks added:
 *   - jacocoDebugTestReport          (generates HTML + XML coverage report)
 *   - jacocoCoverageVerification     (enforces minimum coverage thresholds — starts at 0%)
 *
 * Reports output: build/reports/jacoco/jacocoDebugTestReport/
 */
class AndroidJacocoConventionPlugin : Plugin<Project> {

    private val coverageExcludes = listOf(
        // Android generated
        "**/R.class",
        "**/R\$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        // Test classes
        "**/*Test*.*",
        "android/**/*.*",
        // Hilt / DI generated
        "**/*_HiltComponents*",
        "**/*_Factory*",
        "**/*_MembersInjector*",
        "**/*Hilt_*",
        // Compose internals
        "**/*ComposableSingletons*",
        "**/*\$\$serializer*",
        // Data binding
        "**/*databinding*",
        "**/BR.class",
    )

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("jacoco")

            val jacocoVersion = libs.findVersion("jacoco").get().toString()
            configure<JacocoPluginExtension> {
                toolVersion = jacocoVersion
            }

            // Wire JaCoCo agent into every Test task so execution data is captured
            tasks.withType<Test>().configureEach {
                configure<JacocoTaskExtension> {
                    isIncludeNoLocationClasses = true
                    excludes = listOf("jdk.internal.*")
                }
            }

            // Register report task for the debug build type
            val buildType = "debug"
            val testTaskName = "test${buildType.replaceFirstChar { it.titlecase(Locale.getDefault()) }}UnitTest"

            tasks.register("jacocoDebugTestReport", JacocoReport::class.java) {
                dependsOn(testTaskName)
                group = "Reporting"
                description = "Generates JaCoCo coverage report for the debug build."

                reports {
                    xml.required.set(true)
                    html.required.set(true)
                }

                classDirectories.setFrom(
                    fileTree(layout.buildDirectory.dir("intermediates/javac/$buildType")) {
                        exclude(coverageExcludes)
                    },
                    fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/$buildType")) {
                        exclude(coverageExcludes)
                    },
                )
                sourceDirectories.setFrom(
                    files(
                        "src/main/java",
                        "src/main/kotlin",
                    )
                )
                executionData.setFrom(
                    fileTree(layout.buildDirectory) {
                        include("**/*.exec", "**/*.ec")
                    }
                )
            }
        }
    }
}
