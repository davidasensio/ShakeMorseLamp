import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Shared Compose configuration for Application modules (AGP 9.x).
 * AGP 9.x: buildFeatures lives on the concrete extension types, not CommonExtension.
 */
internal fun Project.configureAndroidComposeApp(
    extension: ApplicationExtension,
) {
    extension.buildFeatures {
        compose = true
    }
    addComposeDependencies()
}

/**
 * Shared Compose configuration for Library modules (AGP 9.x).
 */
internal fun Project.configureAndroidComposeLib(
    extension: LibraryExtension,
) {
    extension.buildFeatures {
        compose = true
    }
    addComposeDependencies()
}

private fun Project.addComposeDependencies() {
    dependencies {
        val bom = libs.findLibrary("androidx-compose-bom").get()
        add("implementation", platform(bom))
        add("implementation", libs.findLibrary("androidx-compose-ui").get())
        add("implementation", libs.findLibrary("androidx-compose-ui-graphics").get())
        add("implementation", libs.findLibrary("androidx-compose-ui-tooling-preview").get())
        add("implementation", libs.findLibrary("androidx-compose-material3").get())
        add("debugImplementation", libs.findLibrary("androidx-compose-ui-tooling").get())
        add("debugImplementation", libs.findLibrary("androidx-compose-ui-test-manifest").get())
        add("androidTestImplementation", platform(bom))
        add("androidTestImplementation", libs.findLibrary("androidx-compose-ui-test-junit4").get())
    }
}
