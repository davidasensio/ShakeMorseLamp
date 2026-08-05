import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // The Compose compiler plugin is separate from kotlin.android — safe to apply here.
            // kotlin.android is already applied by AndroidApplicationConventionPlugin.
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            extensions.configure<ApplicationExtension> {
                configureAndroidComposeApp(this)
            }
        }
    }
}
