import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Applies the Koin Compiler Plugin and the base Koin dependencies (core + annotations).
 * This is a native Kotlin compiler plugin, not KSP — Koin's KSP-based koin-ksp-compiler is
 * deprecated in favor of this approach as of Koin 4.2.x / koin-compiler 1.x.
 */
internal fun Project.addKoinCoreDependencies() {
    pluginManager.apply("io.insert-koin.compiler.plugin")
    dependencies {
        add("implementation", libs.findLibrary("koin-core").get())
        add("implementation", libs.findLibrary("koin-annotations").get())
    }
}

/** koin-android: androidContext()/startKoin() on the Application class. App module only. */
internal fun Project.addKoinAndroidDependencies() {
    dependencies {
        add("implementation", libs.findLibrary("koin-android").get())
    }
}

/** koinViewModel() in Compose screens. */
internal fun Project.addKoinComposeDependencies() {
    dependencies {
        add("implementation", libs.findLibrary("koin-androidx-compose").get())
    }
}
