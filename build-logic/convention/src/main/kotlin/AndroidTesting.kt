import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Every module gets the same unit-test baseline: JUnit 5 (via the JUnit BOM), Turbine for
 * Flow assertions, and MockK for collaborator mocks.
 */
internal fun Project.addSharedTestingDependencies() {
    dependencies {
        add("testImplementation", platform(libs.findLibrary("junit5-bom").get()))
        add("testImplementation", libs.findLibrary("junit5-jupiter").get())
        add("testRuntimeOnly", libs.findLibrary("junit5-platform-launcher").get())
        add("testImplementation", libs.findLibrary("turbine").get())
        add("testImplementation", libs.findLibrary("mockk").get())
    }
}

internal fun Project.configureJUnit5(extension: ApplicationExtension) {
    extension.testOptions {
        unitTests.all { it.useJUnitPlatform() }
    }
}

internal fun Project.configureJUnit5(extension: LibraryExtension) {
    extension.testOptions {
        unitTests.all { it.useJUnitPlatform() }
    }
}
