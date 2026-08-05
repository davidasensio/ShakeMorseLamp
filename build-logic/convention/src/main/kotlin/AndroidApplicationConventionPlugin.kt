import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Note: org.jetbrains.kotlin.android is auto-applied by AGP 9.x
            // when Kotlin sources are detected — do NOT apply it manually.
            pluginManager.apply("com.android.application")
            extensions.configure<ApplicationExtension> {
                configureKotlinAndroidApp(this)
                configureJUnit5(this)
                defaultConfig.targetSdk = 36
            }
            addSharedTestingDependencies()
            addTimberDependency()
            addKoinCoreDependencies()
            addKoinAndroidDependencies()
            addKoinComposeDependencies()
            addNavigation3RuntimeDependency()
            addNavigation3UiDependencies()
        }
    }
}
