import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/** The MVI contract is Flow-based everywhere (StateFlow/SharedFlow), so every module needs this. */
internal fun Project.addCoroutinesDependency() {
    dependencies {
        add("implementation", libs.findLibrary("kotlinx-coroutines-core").get())
    }
}
