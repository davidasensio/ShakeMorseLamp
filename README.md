# ShakeMorseLamp

An Android app that turns your phone into a Morse code flashlight: control the torch directly,
type a message and transmit it as Morse code blinks, turn the light on with a shake, and trigger
an emergency SOS from the notification shade. Designs are sourced from Google Stitch and Claude,
then implemented as Jetpack Compose UI.

## Status

- ✅ Flashlight control (on/off, dimmable torch strength, auto-off timer with realistic
  non-linear durations up to 8 hours, reliably backed by a foreground service)
- ✅ Morse transmission: type a message, send it via the torch, loop mode with a configurable
  pause between repeats, configurable transmission speed, quick signals (SOS/HELP/OK), and a
  persisted send history
- ✅ Shake-to-activate background service, with a configurable sensitivity and an Emergency mode
  that loops the saved message instead of a plain toggle
- ✅ Settings screen: gestures, hardware, transmission tuning, haptic feedback, appearance
  (light/dark/auto theme), and an About screen
- ✅ Emergency/SOS shortcut via a Quick Settings Tile — loops the saved emergency message,
  one-tap "Test It" from Settings, and (Android 13+) a one-tap "Add to Quick Settings" action
  instead of the manual Edit Tiles flow
- ✅ Home screen widget (Glance): pin a quick phrase and transmit it with one tap
- ✅ Design system (`:core:designsystem`) with light/dark theming, sourced from the Stitch designs
- ✅ Localized into 7 languages (en, ca, de, es, fr, it, pt) with an in-app language picker
- 🚧 Morse receiver via camera

See [docs/ROADMAP.md](docs/ROADMAP.md) for the full feature list and build order.

## Architecture

Clean Architecture (UI → Domain → Data) with MVI on the UI layer, packages-by-feature inside each
module, Koin for dependency injection, and Navigation 3 for type-safe routing. Full conventions —
module graph, DI/testing patterns, git workflow, commit format — live in
[AGENTS.md](AGENTS.md), the source of truth for contributing to this repo (human or AI).

**The app is fully localized and must stay that way.** Every user-facing string is a `strings.xml`
resource, added to its module's `values/` *and* all six `values-<lang>/` files. A feature is not
done until its copy is extracted. See the Localization section of [AGENTS.md](AGENTS.md).

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

## Releasing

`assembleRelease` and `bundleRelease` need signing material that is git-ignored and absent on a
fresh clone. Without it the build still succeeds — it prints a warning and produces an unsigned
artifact — so a release build that "worked" is not evidence it can be uploaded.

Two documents cover the whole process:

- **[docs/SIGNING.md](docs/SIGNING.md)** — which key signs this app, why the other aliases in the
  keystore are rejected by Play, and how to verify a built artifact before uploading.
- **[docs/PLAY_RELEASE.md](docs/PLAY_RELEASE.md)** — the Play Console declarations, data safety and
  ads answers, store listing copy, and the pre-upload checks.

**Install and launch the release build before uploading.** R8 minification breaks reflective code
that compiles and passes every test — version 2.0.0 shipped and crashed on every launch because a
constructor Room resolves by name was stripped. A compiled, signed release proves nothing about
whether the app starts.

## Website

`web-landing/` holds the Firebase-hosted landing page (English and Spanish) plus the privacy policy
and terms that the Play listing and the app's About screen link to. Deploy with `firebase deploy`
from that directory.

## Module Graph

```
:app
 ├── :feature:flashlight  ← flashlight control UI + ViewModel + domain + data
 ├── :feature:morse       ← reserved. Currently empty; Morse UI lives in :feature:flashlight,
 │                          and the engine in :core:morse.
 ├── :feature:settings    ← settings/about UI + ViewModel + domain + data
 ├── :core:ui             ← reserved: shared cross-feature Compose UI. Currently empty.
 ├── :core:designsystem   ← design system: theme, typography, color, reusable components
 ├── :core:morse          ← pure-Kotlin Morse encoding/timing/playback engine
 └── :core:common         ← shared utilities, extensions
```

See [AGENTS.md](AGENTS.md) for the full architecture rules (module dependency direction,
packages-by-feature layout, DI, testing conventions, git workflow).

## License

Not yet decided.
