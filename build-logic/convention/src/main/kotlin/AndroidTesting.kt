import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Every module gets the same unit-test baseline: JUnit 5 (via the JUnit BOM), Turbine for
 * Flow assertions, and MockK for collaborator mocks.
 *
 * Also adds the JUnit Vintage Engine so the JUnit Platform can discover JUnit 4-style tests —
 * Compose UI testing (`createComposeRule`, `@RunWith`) and Roborazzi screenshot tests are
 * inherently JUnit 4 (there's no JUnit 5 equivalent yet), so this bridges them onto the same
 * `useJUnitPlatform()` test task instead of needing a separate test runner.
 */
internal fun Project.addSharedTestingDependencies() {
    dependencies {
        add("testImplementation", platform(libs.findLibrary("junit5-bom").get()))
        add("testImplementation", libs.findLibrary("junit5-jupiter").get())
        add("testRuntimeOnly", libs.findLibrary("junit5-platform-launcher").get())
        add("testRuntimeOnly", libs.findLibrary("junit5-vintage-engine").get())
        add("testImplementation", libs.findLibrary("turbine").get())
        add("testImplementation", libs.findLibrary("mockk").get())
        add("testImplementation", libs.findLibrary("kotlinx-coroutines-test").get())
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
