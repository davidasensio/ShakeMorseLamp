# ShakeMorseLamp — AGENTS.md

Android app that controls the device flashlight, sends Morse code messages, and turns the
flashlight on via shake detection. Designs are sourced from Google Stitch and Claude, then
implemented as Compose UI.

This file is the single source of truth for architecture, tooling, and workflow conventions.
`CLAUDE.md` only points here.

## Working Instructions

These take priority over everything else in this file:

- **Read `README.md` first**, before making any change, if it exists at the repo root — it
  carries project-level context (setup, purpose, current status) that this file doesn't repeat.
- **Consult `docs/`** for anything not covered here or in the README — design notes, ADRs,
  API/reference material — before guessing or re-deriving something that may already be
  documented there.
- **Ask clarifying questions** whenever a request is ambiguous, underspecified, or could be
  implemented multiple reasonable ways (e.g. which module something belongs in, a naming choice
  with lasting effects, a UX behavior not specified in the design). Don't silently guess and
  proceed on anything with real, hard-to-reverse impact.
- **Never expose privacy-sensitive or secret data**: API keys, access tokens, Personal Access
  Tokens (PATs), passwords, signing keys/keystores, credentials of any kind. Never print, log,
  commit, hardcode, or paste these into source, commit messages, PRs, or chat output — including
  partially (e.g. "the token starts with..."). Keep them in local, git-ignored config
  (`local.properties`, environment variables, a secrets manager) and reference them indirectly.
  If existing code or history already exposes a secret, flag it instead of repeating/spreading it
  further.

## Tech Stack

| Concern | Choice |
|---|---|
| Language | Kotlin 2.x |
| UI | Jetpack Compose + Material3 |
| Navigation | Navigation 3 (`androidx.navigation3`), type-safe routes |
| Build | AGP 9.x, Gradle version catalog (`gradle/libs.versions.toml`) |
| Architecture | Clean Architecture (UI → Domain → Data), packages-by-feature |
| UI Pattern | MVI: single `UiState` per screen, `StateFlow` for state, `SharedFlow` for one-off events |
| Async | Kotlin Coroutines / Flow |
| Logging | Timber |
| Dependency Injection | Koin + Koin Annotations (`koin-ksp-compiler`) |
| Design System | Dedicated `:core:designsystem` module |
| Unit Testing | JUnit 5 (Jupiter) + Turbine (Flow testing) |
| Mocking | MockK, used with JUnit 5 for collaborator mocks |
| UI Testing | Jetpack Compose testing APIs (`createComposeRule`) + Page Object pattern |
| Screenshot Testing | Roborazzi |
| Code Quality | ktlint + Detekt |
| Coverage | JaCoCo (opt-in per module) |
| Branching / Commits | git-flow |
| Min SDK | 30 (Android 11) |
| Target SDK | 36 |

## Module Graph

```
:app
 ├── :feature:flashlight  ← flashlight control UI + ViewModel + domain + data
 ├── :feature:morse       ← morse encoder UI + ViewModel + domain + data
 ├── :core:ui             ← shared cross-feature Compose UI (composite/stateful pieces)
 ├── :core:designsystem   ← design system: theme, typography, color, reusable components
 └── :core:common         ← shared utilities, extensions
```

Feature modules depend on core modules. **No module may depend on `:app`.**
Core modules must not depend on feature modules. Feature modules must not depend on each other —
share code by pushing it down into `:core:*`. `:core:ui` may depend on `:core:designsystem`
(composite components are built on top of design system primitives).

### Packages-by-feature, inside each module

Within a feature module, organize by sub-feature/responsibility, not by architectural layer at
the top level. Each feature module contains its own `ui`, `domain`, and `data` packages so the
Clean Architecture boundary is visible, but there is no separate `:domain`/`:data` module per
feature — that split happens as packages inside the one feature module:

```
feature/morse/src/main/java/com/handysparksoft/shakelamp/feature/morse/
 ├── ui/            → Composables, ViewModel, UiState/UiEvent/UiAction, Koin UI module
 ├── domain/        → use cases, domain models, repository interfaces (pure Kotlin, no Android)
 └── data/          → repository implementations, data sources, mappers
```

`domain` must not import anything from `ui` or `data`. `data` implements interfaces declared in
`domain`. `ui` depends on `domain` only, never directly on `data`.

### testFixtures

Shared test doubles, fakes, and builders that other modules need (e.g. `:feature:morse` unit
tests wanting a fake flashlight repository from `:feature:flashlight`, or `:core:common` fakes)
live in that module's `src/testFixtures/java` source set, not in a separate test-only module.
Consumers add `testFixtures(project(":feature:flashlight"))` under `testImplementation`. Enable
`testFixtures { enable = true }` per module as needed rather than globally.

## MVI Contract

One `UiState` per screen. It is the **only** thing the Composable reads to render itself.

```kotlin
@Immutable
data class MorseUiState(
    val message: String = "",
    val isTransmitting: Boolean = false,
    val error: MorseError? = null,
)

sealed interface MorseUiAction {
    data class MessageChanged(val text: String) : MorseUiAction
    data object StartTransmission : MorseUiAction
}

sealed interface MorseUiEvent {
    data object TransmissionFinished : MorseUiEvent
    data class ShowError(val message: String) : MorseUiEvent
}
```

ViewModel shape:

```kotlin
class MorseViewModel(private val sendMorseMessage: SendMorseMessageUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(MorseUiState())
    val uiState: StateFlow<MorseUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<MorseUiEvent>()
    val uiEvent: SharedFlow<MorseUiEvent> = _uiEvent.asSharedFlow()

    fun onAction(action: MorseUiAction) { /* ... */ }
}
```

- State: `StateFlow<UiState>`, collected with `collectAsStateWithLifecycle()`.
- One-off events (navigation, snackbars, toasts): `SharedFlow<UiEvent>` exposed from a
  `MutableSharedFlow` with no replay, collected via `LaunchedEffect` +
  `repeatOnLifecycle(Lifecycle.State.STARTED)`. Do not use `Channel` for this — standardize on
  `SharedFlow`.
- User intents: a single sealed `UiAction`/`UiIntent` type handled through one `onAction()`
  entry point on the ViewModel — avoid one public function per interaction.
- Prefer `sealed interface` over `sealed class` for `UiState`/`UiAction`/`UiEvent` variants when
  there is no shared state to hoist into a base class.

## Dependency Injection (Koin)

- Use Koin with **Koin Annotations** (`io.insert-koin:koin-annotations` +
  `io.insert-koin:koin-ksp-compiler` via KSP) — annotate classes with `@Single`, `@Factory`,
  `@KoinViewModel`, and group them with `@Module @ComponentScan` per module/feature, instead of
  hand-written `module { }` DSL blocks.
- Each feature module exposes one generated Koin module (via `@Module` on a marker class in that
  feature's root package); `:app` aggregates all feature/core modules at `startKoin { }` in the
  `Application` class.
- Domain use cases and repositories are injected by constructor; never resolve dependencies with
  `get()` / service-locator calls inside domain or data classes — only at the composition edges
  (ViewModel factories, `Application`).
- ViewModels are provided with `@KoinViewModel` and obtained in Compose via `koinViewModel()`.

## Navigation

- Navigation 3 (`androidx.navigation3`), with type-safe route objects (`@Serializable data
  object`/`data class` per destination) defined next to each feature's top-level Composable.
- Each feature module exposes its destinations/entry points; the nav graph is assembled in
  `:app` (or a thin navigation aggregator if the graph grows large enough to warrant it) —
  feature modules must not depend on each other to link screens together.

## Design System

- `:core:designsystem` is the dedicated module for the app's design system: theme (color,
  typography, shapes), and reusable Compose components (buttons, text fields, cards, top bars,
  icons, etc.), sourced from the Google Stitch / Claude designs.
- Feature modules **must use `:core:designsystem` components when a suitable one exists** instead
  of raw Material3 primitives or one-off styled Composables. If a feature needs a new reusable
  primitive, add it to `:core:designsystem` rather than duplicating it locally.
- Feature-specific composites that combine several design-system components with app/business
  logic (e.g. a loading/error wrapper used across features) belong in `:core:ui`, not in
  `:core:designsystem` — keep the design system itself free of feature/business concerns.
- `:core:designsystem` has no dependency on `:core:common` or any feature module; it only depends
  on Compose/Material3.

## Convention Plugins (build-logic)

All modules use convention plugins from `build-logic/convention/`. Never configure
compileSdk, minSdk, Java toolchain, or Compose directly in a module build file.

| Plugin ID | Use for |
|---|---|
| `shakelamp.android.application` | `:app` only |
| `shakelamp.android.application.compose` | `:app` with Compose |
| `shakelamp.android.library` | Any Android library module |
| `shakelamp.android.library.compose` | Library module + Compose |
| `shakelamp.android.feature` | Feature modules (bundles library + compose) |
| `shakelamp.jvm.library` | Pure Kotlin modules (no Android) |
| `shakelamp.android.quality` | Opt-in: ktlint + Detekt |
| `shakelamp.android.jacoco` | Opt-in: JaCoCo coverage |

Koin (KSP) wiring belongs in the `shakelamp.android.feature` and `shakelamp.android.library`
convention plugins (apply the KSP plugin + add `koin-annotations`/`koin-ksp-compiler` there)
rather than repeating it in every module's `build.gradle.kts`.

### Adding a new feature module

1. Create `feature/<name>/build.gradle.kts`:
   ```kotlin
   plugins {
       alias(libs.plugins.shakelamp.android.feature)
       alias(libs.plugins.shakelamp.android.quality)
       alias(libs.plugins.shakelamp.android.jacoco)
   }
   android { namespace = "com.handysparksoft.shakelamp.feature.<name>" }
   ```
2. Create `feature/<name>/src/main/AndroidManifest.xml` (empty `<manifest />`).
3. Add `include(":feature:<name>")` to `settings.gradle.kts`.
4. Create `ui/`, `domain/`, `data/` packages under
   `feature/<name>/src/main/java/com/handysparksoft/shakelamp/feature/<name>/`.
5. Add a `@Module @ComponentScan` Koin module class for the feature.
6. If other modules need test doubles from this feature, enable `testFixtures` in its
   `build.gradle.kts` and add fakes under `src/testFixtures/java`.

## Testing

- **Unit tests**: JUnit 5 (Jupiter). No JUnit 4 `@Test`/`@RunWith` in new code.
- **Flow testing**: Turbine (`flow.test { awaitItem() ... }`) for `StateFlow`/`SharedFlow`
  assertions in ViewModel tests — no manual `toList()` collection or `Dispatchers.setMain`
  boilerplate duplicated per test class; put shared test dispatcher rules in `:core:common`
  testFixtures.
- **Mocking**: MockK for collaborator mocks in JUnit 5 unit tests (`mockk<T>()`, `every { }`,
  `coEvery { }` for suspend functions, `verify { }`). Prefer a fake from `testFixtures` over a
  mock when the collaborator's behavior is simple enough to fake — reach for MockK when a real
  fake would be disproportionate to what the test needs (e.g. verifying interactions, one-off
  edge-case stubbing).
- **Compose UI tests**: Jetpack Compose testing APIs (`createComposeRule`,
  `ComposeContentTestRule`) for interaction/state assertions on individual screens.
- **Page Object pattern**: for screens with non-trivial UI tests, wrap `ComposeTestRule`
  interactions in a Page Object class (e.g. `MorseScreenRobot`/`MorsePage` with methods like
  `typeMessage(text)`, `tapSend()`, `assertTransmitting()`) instead of repeating
  `composeTestRule.onNodeWithTag(...)` chains across test methods. Keep one Page Object per
  screen, next to that screen's tests; share reusable pieces via `testFixtures` if more than one
  module's tests need them.
- **Screenshot tests**: Roborazzi for a small set of key screens/states per feature — not
  exhaustive per-component coverage. Run on Robolectric, no emulator required.
- **Coverage**: `./gradlew jacocoDebugTestReport` per module that opts into
  `shakelamp.android.jacoco`.

## Git Workflow (git-flow)

- `main` — always releasable/production.
- `develop` — integration branch; base for new work.
- `feature/<short-description>` — branched from `develop`, merged back via PR.
- `release/<version>` — branched from `develop` when preparing a release.
- `hotfix/<short-description>` — branched from `main` for urgent production fixes, merged into
  both `main` and `develop`.
- Commit messages follow the format in [Commits](#commits) below.

## Commits

Write and apply commits with Conventional Commits, a 50–60 character subject, and a detailed
body that lists changes in descending order of relevance. When requested, also push the branch
and create a pull request using the repository PR template. Before creating a PR, ask whether it
relates to any issue and include that linkage in the PR template content. Before creating a PR,
also ask whether to run `scripts/update_readme_loc_breakdown.sh` and run it if the user says yes.

### Commit Message Format

Use this format:

```
<emoji> <type>(<scope>): <subject>

<Body paragraphs and/or bullets ordered by relevance>
```

Rules:

- Prefix the subject with the emoji that matches the Conventional Commit type.
- Use Conventional Commits types: `feat`, `fix`, `refactor`, `chore`, `docs`, `test`, `perf`,
  `build`, `ci`, `style`.
- Keep the subject 50–60 characters total (including type/scope). Use imperative mood and no
  trailing period.
- Body must be detailed and list changes in descending relevance.
- If using bullets in the body, use markdown `-` bullets (no nested bullets). Example lines:
  `- User-visible or functional changes first.`, `- Behavioral or architectural changes next.`,
  `- Internal refactors, tooling, and docs last.`
- Do not include format validation results or test outcomes in the commit message body.

Emoji mapping:

- `feat` → ✨
- `fix` → 🐛
- `refactor` → ♻️
- `chore` → 🧹
- `docs` → 📝
- `test` → ✅
- `perf` → ⚡️
- `build` → 🏗️
- `ci` → 🤖
- `style` → 🎨

## Build Commands

```bash
# Build
./gradlew assembleDebug
./gradlew assembleRelease

# Tests
./gradlew test                      # Unit tests
./gradlew connectedAndroidTest      # Instrumented tests (needs a device/emulator)
./gradlew recordRoborazziDebug      # Record screenshot baselines
./gradlew verifyRoborazziDebug      # Verify screenshots against baselines

# Coverage report (per module that opts in)
./gradlew jacocoDebugTestReport

# Static Analysis
./gradlew detekt
./gradlew ktlintCheck
./gradlew ktlintFormat              # Auto-format
./gradlew lint                      # Android lint (includes Compose lints)

# Create Baselines
./gradlew detektBaseline
./gradlew ktlintGenerateBaseline
./gradlew lint -Dlint.baselines.continue=true

# Full check (lint + test + quality)
./gradlew check
```

Only regenerate a baseline (`detektBaseline`, `ktlintGenerateBaseline`, lint baseline) to
knowingly grandfather in existing issues while cleaning up a specific area — not as a way to
silence new violations. Re-running `recordRoborazziDebug` should be a deliberate, reviewed update
to expected UI, not a way to make a failing screenshot test pass.

## Rules

- **Compose screens** are `@Composable` functions in `feature:*` modules, under that feature's
  `ui` package.
- **ViewModels** use `StateFlow<UiState>` for state and `SharedFlow<UiEvent>` for one-off events;
  a single `onAction(UiAction)` entry point for intents.
- **No XML layouts** — Compose only.
- **No LiveData** — use `StateFlow`/`SharedFlow`.
- **No `buildSrc`** — all build logic lives in `build-logic/`.
- **No wildcard imports** — enforced by ktlint.
- Line length limit is **120 characters**.
- Use `@Stable` / `@Immutable` annotations on Compose UI state classes.
- Prefer `sealed interface` over `sealed class` for UI state and events.
- Domain layer is pure Kotlin: no `android.*` imports, no Compose, no Koin annotations beyond
  what's needed to expose use cases/repository interfaces for injection.
- Inject dependencies via Koin constructor injection; no manual `get()` calls outside DI setup.
- All logging goes through `Timber` — no `android.util.Log` calls in app code.
- Reach for a `:core:designsystem` component before writing a new one; build feature UI out of
  design system primitives rather than raw Material3.

## Anti-Patterns to Avoid

- Don't call Android APIs directly from ViewModels — use repository/use-case abstractions.
- Don't put business logic in `@Composable` functions.
- Don't apply `shakelamp.android.application` to library modules (and vice versa).
- Don't add `compileSdk`, `minSdk`, or `jvmTarget` directly to module build files.
- Don't use `System.out.println` or `android.util.Log` — use `Timber`.
- Don't use `Channel` for one-off ViewModel events — use `SharedFlow` for consistency across
  screens.
- Don't add per-feature Koin `module { }` DSL blocks by hand when Koin Annotations can generate
  them — keep DI declarations next to the classes they provide.
- Don't create a `:domain`/`:data` module per feature — keep the Clean Architecture split as
  packages inside the single feature module.
- Don't write JUnit 4-style tests in new/touched files — migrate opportunistically to JUnit 5.
- Don't mock everything by default with MockK — prefer a `testFixtures` fake for collaborators
  with real, testable behavior; reserve mocks for verifying interactions or stubbing edge cases.
- Don't build a new button/card/input/etc. inside a feature module — add it to
  `:core:designsystem` so every feature can reuse it.
