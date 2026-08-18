# ShakeMorseLamp — Roadmap

Living document — a backlog and suggested sequence, not a contract. Update it as items land or
priorities change; a stale roadmap is worse than none. Referenced from `AGENTS.md`'s Working
Instructions as something to consult under `docs/`.

## Engineering Setup (do first, in order)

1. Wire the version catalog + convention plugins for the stack documented in `AGENTS.md`: Koin
   (native compiler plugin, not KSP), JUnit 5, Turbine, MockK, Timber, Roborazzi, Navigation 3.
   **Done.**
2. Scaffold `:core:designsystem` (theme/typography/color, base components). **Done** — module
   created with the default Color/Theme/Type moved out of `:app`; real tokens/components deferred
   to step 3. The `:core:ui` module this step also mentioned was later deleted: nothing ever
   needed a cross-feature composite, so it stayed empty.
3. Generate a minimal design-system token/component set (colors, typography, spacing, and a
   handful of base components — buttons, text fields, cards) in `:core:designsystem`, based on a
   real design connected via Google Stitch over MCP rather than invented from scratch. **Done** —
   light + dark color schemes (Lumen Utility / Luminous Ecological), type/shape/spacing tokens,
   and SMLButton/SMLTextField/SMLCard/SMLSwitch, each with an internal `@PreviewLightDark` and a
   Roborazzi screenshot.
4. Build one feature end-to-end as a walking skeleton — MVI ViewModel, Koin DI, a
   design-system-based screen, Navigation 3 entry point, JUnit5+Turbine+MockK tests, one
   Roborazzi screenshot. Flashlight is the natural first pick (simpler than Morse encoding).
   **Done** — bare minimum slice (header + power toggle only, per explicit scope decision); the
   full "Main Array" screen with timer/broadcast/widget-promo is feature-build-order item 1 below.
5. Wire `:app` — `startKoin`, Navigation 3 `NavHost`, theme applied from `:core:designsystem`.
   **Done** as part of step 4 — `ShakeMorseLampApplication` + `NavDisplay` hosting the one
   `FlashlightRoute` entry that exists so far.
6. ~~Build out `:feature:morse` the same way~~ — **abandoned, and the module deleted.** Morse
   splits naturally along a different seam than anticipated: the engine is pure Kotlin in
   `:core:morse`, and the transmission UI belongs on the flashlight screen because it shares the
   torch session. A separate feature module would only have owned a screen that does not exist.
7. Add shake detection (sensor wrapper). **Done** — `AccelerometerShakeDetector` behind a
   `ShakeDetector` port, driven by `ShakeDetectionService`; see **Known Constraints**.
8. Fill the process gaps `AGENTS.md` references. **Partly done** — `README.md` and `docs/` exist
   (now including `SIGNING.md` and `PLAY_RELEASE.md`). Still missing:
   `.github/PULL_REQUEST_TEMPLATE.md` and `scripts/update_readme_loc_breakdown.sh`.
9. Localize the app. **Done** — 7 locales (en, ca, de, es, fr, it, pt), every user-facing string
   extracted to `strings.xml`, plus an in-app language picker. See AGENTS.md's Localization
   section; the per-app locale mechanism has a trap documented there.
10. Ship to Google Play. **Done** — release signing, R8, the Firebase landing site with privacy
    policy and terms, store listing copy in 7 locales, and the Console declarations. See
    `docs/SIGNING.md` and `docs/PLAY_RELEASE.md`.

## Known Constraints

- **Reliable background execution requires a foreground service — and the service must own the
  actual logic itself, not just keep a ViewModel alive.** Confirmed via on-device testing while
  building the auto-off timer: Android's app freezer suspends a backgrounded process — including
  pending coroutine timers *and* already-dispatched `WorkManager`/`JobScheduler` jobs — until
  something else unfreezes it (e.g. the user reopening the app). A `PARTIAL_WAKE_LOCK` and a
  `WorkManager` `OneTimeWorkRequest` backstop were both tried and both failed identically: the job
  sat queued the whole time the screen was locked and only ran the instant the process was
  unfrozen by reopening the app. Only a foreground service (with its required persistent
  notification) keeps a component alive through a screen lock — **but a first version of the
  auto-off timer kept the foreground service running while the actual countdown coroutine still
  lived in `FlashlightViewModel`'s `viewModelScope`, and that gap re-broke the same bug**: the
  service (and its notification) survived, but if the Activity the ViewModel is scoped to got torn
  down while backgrounded (for any reason — not just process freezing), the countdown coroutine
  died silently and the torch never turned off. The fix: the service must own the logic it's
  protecting directly, with its own `CoroutineScope`, not delegate it to a ViewModel. Applies to:
  - **Auto-off timer** (item 3 below) — needs a foreground service to guarantee the torch turns
    off on schedule even while locked. **Done** — `AutoOffKeepAliveService` (`app/.../autooff/`)
    owns the countdown directly; `FlashlightViewModel` only starts/stops it and mirrors its
    remaining-time and `FlashlightRepository.observeTorchState()` for the UI. Verified on-device
    (1- and 3-minute timers + screen lock, torch turns off on schedule).
  - **Shake detection** (items 2/5 below) — needs a foreground service to keep the accelerometer
    listener alive continuously in the background. **Done** — `ShakeDetectionService`
    (`app/.../shake/`), already followed this pattern from the start (owns its own loop directly,
    not delegated to a ViewModel), verified on-device surviving a screen lock.
- **A `delay()`-based countdown must self-correct against the real clock, not decrement a
  counter.** Confirmed on-device: `AutoOffKeepAliveService`'s countdown originally ticked by
  calling `delay(1_000)` in a loop and subtracting a fixed step each time. With the screen off, a
  foreground service keeps the *process* from being frozen but doesn't guarantee full-speed CPU
  scheduling, so individual `delay()` calls can quietly overrun their nominal duration — assuming
  each tick took exactly 1000ms let that per-tick jitter compound over dozens of ticks (a 1-minute
  timer was taking roughly twice as long to fire). Fix: compute a fixed target end-time up front
  via `SystemClock.elapsedRealtime()` and recompute the actual remaining time from the real clock
  on every tick, so any overrun self-corrects instead of accumulating. Applies to any future
  service-owned timer/countdown loop, not just this one.

- **A release build that compiles, signs and installs still proves nothing — run it.** Version
  2.0.0 shipped to Play and crashed on every launch. R8 had stripped the no-arg constructor of
  Room's generated `WorkDatabase_Impl`, which Room resolves reflectively by name, so WorkManager's
  `InitializationProvider` died before any app code ran. Every check passed: unit tests, lint,
  ktlint, detekt, screenshot tests, a signed artifact. None of them execute the minified build.
  Room ships `-keep class * extends androidx.room.RoomDatabase`, which preserves the class *name*
  but not its members — the fix is `{ <init>(); }`, in `app/proguard-rules.pro`. Anything resolved
  by reflection is exposed to the same failure: services named in the manifest, the Glance
  `ActionCallback`, the `@Serializable` Nav3 routes. **Install and launch the release APK before
  every upload**, and exercise the widget, the SOS tile and shake detection specifically.
- **Koin's compile-time DI check is unreliable on incremental builds.** The compiler plugin
  publishes each module's definitions as generated `Koin_hints_*` classes and reads its
  dependencies' hints to verify injection sites. On an incremental recompile it does not re-read
  them, so every cross-module definition is reported missing (`KOIN-D003`) even though the hints
  are present on the compile classpath — which is why `--rerun-tasks` makes the identical build
  pass. Release compilation is therefore forced non-incremental in `AndroidKoin.kt`. Do not remove
  that workaround without checking whether koin-compiler-gradle-plugin has fixed it; 1.1.0 was the
  newest published version when this was hit, and still had the bug.

## Feature List

Each entry: what it does, likely module home, and open questions to resolve before or while
building it.

1. **Flashlight + Morse screen**
   - Turn the flashlight on/off directly.
   - Enter free text and transmit it as Morse code via the flashlight (on/off blink pattern).
   - A "loop" mode can be enabled to repeat the transmitted message continuously until stopped.
   - **Done.** Module split landed as `:feature:flashlight` (torch control + transmission UI) and
     `:core:morse` (pure-Kotlin encoding/timing/playback). `:core:morse` reaches the torch through
     a `TorchController` port that `:feature:flashlight` implements, so the core module never
     depends on a feature.
   - **Open question resolved**: one combined screen. Transmission and torch control share a single
     on-air state, and splitting them would have meant two screens fighting over the same torch
     session.

2. **Settings screen**
   - Toggle the background service on/off for shake-event detection.
   - Theme setting (light/dark/system).
   - General app settings (placeholder for whatever else comes up).
   - **Done** — and it grew well past the original three bullets: gestures, hardware, transmission
     tuning, emergency mode, haptics, appearance, language, and the About screen.
   - **Open question resolved**: its own `:feature:settings` module. It is now the largest feature
     module in the project, so folding it in would have been the wrong call.
   - Persistence landed as Jetpack DataStore (eight separate preference stores) and is now in
     `AGENTS.md`'s tech stack.
   - See **Known Constraints** above — the shake-detection service needs to run as a foreground
     service to survive backgrounding, so this toggle should set user expectations accordingly
     (persistent notification while enabled).

3. **Auto-off timer** — **Done.** Non-linear stops from 1 minute to 8 hours.
   - Turn the flashlight off automatically after an elapsed/selected duration.
   - Lives with flashlight control (`:feature:flashlight`), most likely as part of its existing
     `UiState` (e.g. a countdown field) rather than a separate screen.
   - See **Known Constraints** above — requires a foreground service (persistent notification
     while the timer is counting down) to actually fire while the screen is locked; a plain
     coroutine timer, even backed by a wake lock or a WorkManager backstop, does not.

4. **Emergency / SOS shortcut**
   - One-tap shortcut to start transmitting a fixed emergency pattern (e.g. SOS) via the
     flashlight.
   - **Done** — resolved via item 5's Quick Settings Tile rather than a separate App Shortcut: the
     SOS tile (`app/.../tile/SosTileService.kt`) loops the saved emergency message (falling back
     to "SOS") through a dedicated foreground service (`SosTransmissionService`), with a "Test It"
     action in Settings' Emergency Mode card.

5. **Quick Settings Tile**
   - **Done** — built as the Emergency/SOS tile (item 4) rather than a plain flashlight toggle;
     also added an Android 13+ one-tap "Add to Quick Settings" action
     (`StatusBarManager.requestAddTileService()`, `app/.../tile/AndroidSosTileRequester.kt`) in
     Settings, instead of requiring the manual Edit Tiles flow.
   - Android Quick Settings (notification shade) tile to toggle the flashlight without opening
     the app.
   - Needs a `TileService`, which is a platform-level component — likely lives in `:app`, with
     the actual on/off logic delegated to the flashlight domain layer so behavior stays
     consistent with the in-app toggle.

6. **Home screen widget (Glance)** — **Done.**
   - Configurable quick phrase pinned to the home screen; tapping transmits it, tapping again
     stops a looping one.
   - **Open question resolved**: neither. It lives in `:app` (`app/.../widget/`), because Glance
     widgets are platform components like the Quick Settings tile, and `:feature:morse` no longer
     exists. `WidgetTransmissionService` owns the transmission, per **Known Constraints**.
   - Note: the widget does not follow the in-app language override — `provideGlance()` renders
     outside any Activity, so it follows the system locale.

7. **Dimmable flashlight (torch strength)** — **Done**, with both guards below implemented.
   - Control brightness, not just on/off, via `CameraManager.turnOnTorchWithStrengthLevel()`.
   - Module: `:feature:flashlight` — extends the existing torch-control use case/domain model to
     carry a strength level alongside on/off state; UI adds a slider/stepper to the flashlight
     screen's design-system controls.
   - **Open question / constraint**: `turnOnTorchWithStrengthLevel()` requires API 33
     (Tiramisu+), but `AGENTS.md` sets **minSdk 30**. Needs a runtime check
     (`Build.VERSION.SDK_INT >= 33`) with a plain on/off fallback below that, and a device
     capability check (`CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL` — some devices
     report a max of 1, i.e. no real dimming) before showing the dimmer UI at all.

8. **Morse receiver via camera** — *the only unbuilt feature.*
   - Point the camera at a blinking light source and decode the on/off pattern back into Morse,
     then text — the inverse of the existing transmit flow.
   - Module: `:core:morse` already owns the timing/encoding domain logic and is pure Kotlin, so
     the decoder belongs there; the camera pipeline and UI need a home in a feature module —
     either `:feature:flashlight` or a new one. `:feature:morse` no longer exists.
   - Needs the runtime `CAMERA` permission and a capture pipeline — CameraX `ImageAnalysis` is
     the natural fit over raw Camera2, sampling frame luminance over time to detect
     light/dark transitions.
   - Keep the actual decoder (transition timings → dots/dashes/letters) as pure, camera-agnostic
     domain logic so it's unit-testable in isolation from CameraX — the analyzer only feeds it
     timestamped on/off events.
   - **Open question**: real-time decode vs. record-then-decode, and how much timing tolerance/
     calibration is needed to handle ambient lighting and hand-shake noise.
   - Adds CameraX as a new dependency — add it to `AGENTS.md`'s tech stack when this is built.

## Build Order — as executed

Items 1–8 shipped in roughly the planned order, and the order held up: front-loading the screens
proved out MVI, DI, navigation and tests before any OS-integration surface was attempted, and each
of those surfaces (tile, widget, shake) then reused the same foreground-service pattern rather than
rediscovering it.

1. ✅ Flashlight + Morse screen
2. ✅ Auto-off timer
3. ✅ Dimmable flashlight (torch strength)
4. ✅ Settings screen
5. ✅ Shake detection background service
6. ✅ Emergency/SOS shortcut
7. ✅ Quick Settings Tile
8. ✅ Home screen widget (Glance)
9. ⬜ Morse receiver via camera — still last, and still the most complex: runtime `CAMERA`
   permission, a CameraX pipeline, and decode tolerance for ambient light and hand-shake.

Two things arrived that were not on the original list and took real effort: **localization** into
7 locales with an in-app picker, and **shipping to Play** — signing, R8, the landing site, the
legal pages, and the Console declarations. Both are done; see the Engineering Setup items above.

## What's left

- The Morse receiver (item 9) — the only unbuilt feature.
- `.github/PULL_REQUEST_TEMPLATE.md` and `scripts/update_readme_loc_breakdown.sh` from setup
  item 8.
- Native-speaker review of the six translated locales and the Spanish landing page. Machine
  translations shipped; they read plausibly but have not been checked by a speaker.
- Registering a separate Play upload key, so the app signing key is no longer used directly for
  uploads. See `docs/SIGNING.md`.
