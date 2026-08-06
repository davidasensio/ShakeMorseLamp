import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.JavaVersion
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

/**
 * Convention plugin for pure Kotlin/JVM modules (domain logic, models, utilities)
 * that have no Android dependency.
 *
 * Usage in a module's build.gradle.kts:
 *   plugins { alias(libs.plugins.shakelamp.jvm.library) }
 */
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
            }
            extensions.configure<JavaPluginExtension> {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
            // The plain Kotlin/JVM plugin doesn't sync jvmTarget from JavaPluginExtension the
            // way AGP's Kotlin integration does, so compileKotlin defaults to the daemon's JDK
            // and mismatches compileJava (17) — pin both via a shared toolchain instead.
            extensions.configure<KotlinJvmProjectExtension> {
                jvmToolchain(17)
            }
            configureKotlin()
            addSharedTestingDependencies()
            addKoinCoreDependencies()
            addCoroutinesDependency()
            tasks.withType<Test>().configureEach { useJUnitPlatform() }
        }
    }
}
