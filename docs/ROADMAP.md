# ShakeMorseLamp — Roadmap

Living document — a backlog and suggested sequence, not a contract. Update it as items land or
priorities change; a stale roadmap is worse than none. Referenced from `AGENTS.md`'s Working
Instructions as something to consult under `docs/`.

## Engineering Setup (do first, in order)

1. Wire the version catalog + convention plugins for the stack documented in `AGENTS.md`: Koin
   (native compiler plugin, not KSP), JUnit 5, Turbine, MockK, Timber, Roborazzi, Navigation 3.
   **Done.**
2. Scaffold `:core:designsystem` (theme/typography/color, base components); slim `:core:ui` down
   to its cross-feature composite role. **Done** — module created with the default
   Color/Theme/Type moved out of `:app`; real tokens/components deferred to step 3.
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
6. Build out `:feature:morse` the same way, now that the pattern is proven.
7. Add shake detection (sensor wrapper).
8. Fill the process gaps `AGENTS.md` already references but the repo doesn't have yet:
   `README.md`, this `docs/` folder (in progress), `.github/PULL_REQUEST_TEMPLATE.md`,
   `scripts/update_readme_loc_breakdown.sh`.

## Known Constraints

- **Reliable background execution requires a foreground service.** Confirmed via on-device
  testing while building the auto-off timer: Android's app freezer suspends a backgrounded
  process — including pending coroutine timers *and* already-dispatched `WorkManager`/
  `JobScheduler` jobs — until something else unfreezes it (e.g. the user reopening the app). A
  `PARTIAL_WAKE_LOCK` and a `WorkManager` `OneTimeWorkRequest` backstop were both tried and both
  failed identically: the job sat queued the whole time the screen was locked and only ran the
  instant the process was unfrozen by reopening the app. Only a foreground service (with its
  required persistent notification) keeps a component alive through a screen lock. Applies to:
  - **Auto-off timer** (item 3 below) — needs a foreground service to guarantee the torch turns
    off on schedule even while locked.
  - **Shake detection** (items 2/5 below) — needs a foreground service to keep the accelerometer
    listener alive continuously in the background.

## Feature List

Each entry: what it does, likely module home, and open questions to resolve before or while
building it.

1. **Flashlight + Morse screen**
   - Turn the flashlight on/off directly.
   - Enter free text and transmit it as Morse code via the flashlight (on/off blink pattern).
   - A "loop" mode can be enabled to repeat the transmitted message continuously until stopped.
   - Module: `:feature:flashlight` (torch control) + `:feature:morse` (encoding/playback) — per
     `AGENTS.md` these are separate modules that must not depend on each other.
   - **Open question**: is this one combined screen, or two screens (tabs/nav) sharing a single
     "flashlight session" state? Affects both navigation design and which module owns the
     shared on-air/transmitting state — resolve before wiring Navigation 3 routes.

2. **Settings screen**
   - Toggle the background service on/off for shake-event detection.
   - Theme setting (light/dark/system).
   - General app settings (placeholder for whatever else comes up).
   - **Open question**: new `:feature:settings` module, or folded into an existing feature while
     the app is small?
   - Needs a persistence mechanism (e.g. Jetpack DataStore) — not yet in `AGENTS.md`'s tech
     stack; add it there when this feature starts.
   - See **Known Constraints** above — the shake-detection service needs to run as a foreground
     service to survive backgrounding, so this toggle should set user expectations accordingly
     (persistent notification while enabled).

3. **Auto-off timer**
   - Turn the flashlight off automatically after an elapsed/selected duration.
   - Lives with flashlight control (`:feature:flashlight`), most likely as part of its existing
     `UiState` (e.g. a countdown field) rather than a separate screen.
   - See **Known Constraints** above — requires a foreground service (persistent notification
     while the timer is counting down) to actually fire while the screen is locked; a plain
     coroutine timer, even backed by a wake lock or a WorkManager backstop, does not.

4. **Emergency / SOS shortcut**
   - One-tap shortcut to start transmitting a fixed emergency pattern (e.g. SOS) via the
     flashlight.
   - **Open question**: a quick action on the flashlight screen, an Android App Shortcut
     (long-press launcher icon), or both — decide scope for v1.

5. **Quick Settings Tile**
   - Android Quick Settings (notification shade) tile to toggle the flashlight without opening
     the app.
   - Needs a `TileService`, which is a platform-level component — likely lives in `:app`, with
     the actual on/off logic delegated to the flashlight domain layer so behavior stays
     consistent with the in-app toggle.

6. **Home screen widget (Glance)**
   - Jetpack Glance widget for sending preset Morse messages (a short list of saved messages,
     tap to transmit).
   - Depends on preset-message storage, which ties into the Settings persistence question above.
   - **Open question**: new `:feature:widget` module, or bundled into `:feature:morse`?

7. **Dimmable flashlight (torch strength)**
   - Control brightness, not just on/off, via `CameraManager.turnOnTorchWithStrengthLevel()`.
   - Module: `:feature:flashlight` — extends the existing torch-control use case/domain model to
     carry a strength level alongside on/off state; UI adds a slider/stepper to the flashlight
     screen's design-system controls.
   - **Open question / constraint**: `turnOnTorchWithStrengthLevel()` requires API 33
     (Tiramisu+), but `AGENTS.md` sets **minSdk 30**. Needs a runtime check
     (`Build.VERSION.SDK_INT >= 33`) with a plain on/off fallback below that, and a device
     capability check (`CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL` — some devices
     report a max of 1, i.e. no real dimming) before showing the dimmer UI at all.

8. **Morse receiver via camera**
   - Point the camera at a blinking light source and decode the on/off pattern back into Morse,
     then text — the inverse of the existing transmit flow.
   - Module: natural home is `:feature:morse` (shares the Morse timing/decoding domain logic with
     the transmitter) — **open question** whether receiving is significant enough to warrant its
     own module instead.
   - Needs the runtime `CAMERA` permission and a capture pipeline — CameraX `ImageAnalysis` is
     the natural fit over raw Camera2, sampling frame luminance over time to detect
     light/dark transitions.
   - Keep the actual decoder (transition timings → dots/dashes/letters) as pure, camera-agnostic
     domain logic so it's unit-testable in isolation from CameraX — the analyzer only feeds it
     timestamped on/off events.
   - **Open question**: real-time decode vs. record-then-decode, and how much timing tolerance/
     calibration is needed to handle ambient lighting and hand-shake noise.
   - Adds CameraX as a new dependency — add it to `AGENTS.md`'s tech stack when this is built.

## Suggested Feature Build Order (after the walking skeleton)

1. Flashlight + Morse screen — core value prop, validates the architecture end-to-end.
2. Auto-off timer — small addition on top of flashlight.
3. Dimmable flashlight (torch strength) — natural extension of the same on/off control.
4. Settings screen — needed by shake detection, theme, and the widget's presets.
5. Shake detection background service — depends on the Settings toggle; see **Known
   Constraints** above (needs a foreground service to stay alive in the background).
6. Emergency/SOS shortcut.
7. Quick Settings Tile.
8. Home screen widget (Glance).
9. Morse receiver via camera — most complex (permissions, CameraX pipeline, decode tolerance),
   left for last once the transmit-side Morse domain logic is proven.

This order front-loads the screens that prove out the architecture (MVI, DI, navigation, tests)
and pushes the OS-integration surfaces and the camera receiver — each with their own
permission/packaging/calibration quirks — to the end, once the core patterns are settled.
