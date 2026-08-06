# ShakeMorseLamp

An Android app that turns your phone into a Morse code flashlight: control the torch directly,
type a message and transmit it as Morse code blinks, and (coming soon) turn the light on with a
shake. Designs are sourced from Google Stitch and Claude, then implemented as Jetpack Compose UI.

## Status

- ✅ Flashlight control (on/off, auto-off timer with realistic non-linear durations up to 8 hours)
- ✅ Morse transmission: type a message, send it via the torch, loop mode, quick signals
  (SOS/HELP/OK), and a persisted send history
- ✅ Design system (`:core:designsystem`) with light/dark theming, sourced from the Stitch designs
- 🚧 Settings screen (playback speed, theme, notification/permission toggles)
- 🚧 Shake-to-activate background service
- 🚧 Dimmable flashlight (torch strength)
- 🚧 Emergency/SOS shortcut, Quick Settings Tile, home screen widget
- 🚧 Morse receiver via camera

See [docs/ROADMAP.md](docs/ROADMAP.md) for the full feature list and build order.

## Architecture

Clean Architecture (UI → Domain → Data) with MVI on the UI layer, packages-by-feature inside each
module, Koin for dependency injection, and Navigation 3 for type-safe routing. Full conventions —
module graph, DI/testing patterns, git workflow, commit format — live in
[AGENTS.md](AGENTS.md), the source of truth for contributing to this repo (human or AI).

## Getting Started

- **Requirements**: Android Studio (current stable), JDK 17, Android SDK with API 37 installed.
- **Min SDK**: 30 (Android 11) · **Target SDK**: 36
- Clone the repo and open it in Android Studio, or build from the command line:

```bash
./gradlew assembleDebug
```

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

## Module Graph

```
:app
 ├── :feature:flashlight  ← flashlight control UI + ViewModel + domain + data
 ├── :feature:morse       ← morse encoder UI + ViewModel + domain + data
 ├── :core:ui             ← shared cross-feature Compose UI (composite/stateful pieces)
 ├── :core:designsystem   ← design system: theme, typography, color, reusable components
 ├── :core:morse          ← pure-Kotlin Morse encoding/timing/playback engine
 └── :core:common         ← shared utilities, extensions
```

See [AGENTS.md](AGENTS.md) for the full architecture rules (module dependency direction,
packages-by-feature layout, DI, testing conventions, git workflow).

## License

Not yet decided.
