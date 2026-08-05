import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

/**
 * Convention plugin that applies ktlint (formatting) and Detekt (static analysis)
 * to any module that opts in.
 *
 * Opt-in usage in a module's build.gradle.kts:
 *   plugins { alias(libs.plugins.shakelamp.android.quality) }
 *
 * Tasks added:
 *   - ktlintCheck          (run formatting checks, wired into 'check')
 *   - ktlintFormat         (auto-format sources)
 *   - detekt               (run static analysis, wired into 'check')
 *   - detektMain / detektTest
 *
 * Config files:
 *   - .editorconfig        (ktlint rules — at repo root)
 *   - config/detekt/detekt.yml (Detekt ruleset — at repo root)
 */
class AndroidQualityConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jlleitschuh.gradle.ktlint")
                apply("io.gitlab.arturbosch.detekt")
            }

            // ── ktlint ────────────────────────────────────────────────────────
            configure<KtlintExtension> {
                android.set(true)
                ignoreFailures.set(false)
                reporters {
                    reporter(ReporterType.PLAIN)
                    reporter(ReporterType.CHECKSTYLE)
                    reporter(ReporterType.HTML)
                }
            }

            // ── Detekt ────────────────────────────────────────────────────────
            configure<DetektExtension> {
                config.setFrom(rootProject.files("config/detekt/detekt.yml"))
                buildUponDefaultConfig = true
                autoCorrect = false
                parallel = true
                // Baseline file lives next to this module's build file
                baseline = file("$projectDir/config/detekt/baseline.xml")
            }

            dependencies {
                // Detekt formatting ruleset (Compose-aware via detekt-formatting)
                add("detektPlugins", libs.findLibrary("detekt-formatting").get())
            }
        }
    }
}
