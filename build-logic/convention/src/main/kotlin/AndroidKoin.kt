import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    disableIncrementalCompilationForRelease()
}

/**
 * The Koin compiler plugin publishes each module's definitions as generated `Koin_hints_*` classes
 * and reads its dependencies' hints to verify injection sites at compile time. On an *incremental*
 * recompile it does not re-read those hints, so every cross-module definition is reported missing:
 *
 *     e: ... [Koin][KOIN-D003] Missing definition: ...ThemePreferenceRepository
 *
 * The hints are genuinely present on the compile classpath — only the incremental pass fails to see
 * them, which is why `--rerun-tasks` makes the same build pass. That turns release builds into a
 * coin flip depending on what happens to be up to date.
 *
 * Release compilation is therefore forced to be non-incremental. Release builds are infrequent and
 * a correct one is worth more than the time saved. Debug keeps incremental compilation, so the
 * edit-run loop is unaffected. Remove this once koin-compiler-gradle-plugin fixes the incremental
 * path — 1.1.0 is the latest published version as of writing and still has it.
 */
private fun Project.disableIncrementalCompilationForRelease() {
    tasks.withType<KotlinCompile>().configureEach {
        if (name.contains("Release")) {
            incremental = false
        }
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
