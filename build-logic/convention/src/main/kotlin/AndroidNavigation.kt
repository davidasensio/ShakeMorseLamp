import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Navigation 3 route/key types — needed wherever a feature declares its destinations.
 *
 * Also applies the Kotlin serialization compiler plugin and adds kotlinx-serialization-core:
 * NavKey types must be @Serializable, or rememberNavBackStack fails at runtime (its saveable
 * back-stack support looks up a serializer even though NavKey itself has no such requirement
 * at the type level — this only shows up when the app actually runs, not at compile time).
 */
internal fun Project.addNavigation3RuntimeDependency() {
    pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
    dependencies {
        add("implementation", libs.findLibrary("androidx-navigation3-runtime").get())
        add("implementation", libs.findLibrary("kotlinx-serialization-core").get())
    }
}

/** NavDisplay + entry-scoped ViewModels — only the module assembling the nav graph needs this. */
internal fun Project.addNavigation3UiDependencies() {
    dependencies {
        add("implementation", libs.findLibrary("androidx-navigation3-ui").get())
        add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-navigation3").get())
    }
}
