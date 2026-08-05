import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/** All logging goes through Timber — never android.util.Log directly. */
internal fun Project.addTimberDependency() {
    dependencies {
        add("implementation", libs.findLibrary("timber").get())
    }
}
