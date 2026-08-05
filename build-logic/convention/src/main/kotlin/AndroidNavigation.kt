import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/** Navigation 3 route/key types — needed wherever a feature declares its destinations. */
internal fun Project.addNavigation3RuntimeDependency() {
    dependencies {
        add("implementation", libs.findLibrary("androidx-navigation3-runtime").get())
    }
}

/** NavDisplay + entry-scoped ViewModels — only the module assembling the nav graph needs this. */
internal fun Project.addNavigation3UiDependencies() {
    dependencies {
        add("implementation", libs.findLibrary("androidx-navigation3-ui").get())
        add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-navigation3").get())
    }
}
