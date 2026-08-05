import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

/**
 * Shared Android + Kotlin configuration for Application modules (AGP 9.x).
 * AGP 9.x: CommonExtension no longer has defaultConfig/compileOptions,
 * so each concrete extension type is configured directly.
 */
internal fun Project.configureKotlinAndroidApp(
    extension: ApplicationExtension,
) {
    extension.apply {
        compileSdk = 37

        defaultConfig {
            minSdk = 30
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }

    configureKotlin()
}

/**
 * Shared Android + Kotlin configuration for Library modules (AGP 9.x).
 */
internal fun Project.configureKotlinAndroidLib(
    extension: LibraryExtension,
) {
    extension.apply {
        compileSdk = 37

        defaultConfig {
            minSdk = 30
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }

    configureKotlin()
}

/**
 * Shared Kotlin compiler options for all modules.
 */
internal fun Project.configureKotlin() {
    tasks.withType<KotlinCompilationTask<*>>().configureEach {
        compilerOptions {
            freeCompilerArgs.addAll(
                listOf(
                    "-opt-in=kotlin.RequiresOptIn",
                    "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                ),
            )
        }
    }
}
