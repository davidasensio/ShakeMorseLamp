# ShakeMorseLamp — Play Console Release Notes

Reference material for submitting to Google Play. Covers the declarations the Console asks for
and the answers this app should give, so they don't have to be re-derived at submission time.
Update it whenever a service, permission, or data practice changes — a stale answer here is worse
than none, because it will be pasted into a form that Google reviews.

## Blockers to clear before the first upload

This app replaces an **existing Play listing under the same package**
(`com.handysparksoft.shakelamp`), so both of these had to be settled before an upload could work.

1. **Version** — currently `versionCode = 21`, `versionName = "2.0.1"` in `app/build.gradle.kts`.
   2.0.0 (code 20) shipped and crashed on launch, so 21 is the hotfix. Play rejects any upload
   whose `versionCode` is not greater than the highest already published, and only the Console
   knows that number — **check it before every build**, and bump again for each new upload.
2. **Release signing is configured** — `signingConfigs { create("release") }` reads
   `keystore/keystore_old/keystore.properties`, with `SIGNING_*` Gradle properties or environment
   variables taking precedence so CI can sign without the file present. A missing file only warns,
   so debug builds and fresh clones still work.

## Signing

See **[SIGNING.md](SIGNING.md)** — it is the single source of truth for which key signs this app,
why the other aliases in the keystore are rejected, how to verify a build before uploading, and
the history behind the confusing folder names. Do not duplicate any of it here; the two would
drift and one of them would be wrong when it mattered.

The one line worth repeating: the upload must be signed with alias **`release_certificate`**,
SHA1 ending **`…BB:12:F0:30`**.

## Foreground service types declaration

Required because `targetSdk` is 36 (the rule applies from 34) and the app uses `specialUse`.
Found in Play Console under **App content**. Google reviews `specialUse` by hand and will reject
it if a defined type fits, so the justification matters.

Every service already declares `android:foregroundServiceType="specialUse"` plus the mandatory
`PROPERTY_SPECIAL_USE_FGS_SUBTYPE` property — without that property an app targeting 34+ crashes
when the service starts. The property values below are the source of truth; paste them into the
Console form so the two never disagree.

| Service | Declared justification |
|---|---|
| `ShakeDetectionService` | Detects the double-shake gesture the user opted into in Settings, while the screen is off, to switch the flashlight on or transmit their emergency Morse message |
| `AutoOffKeepAliveService` | Keeps the user-activated flashlight lit and counts down to the auto-off time the user selected, continuing while the screen is off, and switches the light off when it elapses |
| `SosTransmissionService` | Transmits the emergency Morse message the user configured by blinking the flashlight, repeating until the user stops it, started on demand from the SOS Quick Settings tile |
| `WidgetTransmissionService` | Transmits the quick phrase the user saved to their home screen widget by blinking the flashlight, started by the user tapping that widget |

### Why no defined type fits

Have this ready — it is what a rejection appeal turns on.

- **No sensor or accelerometer type exists.** `ShakeDetectionService` has no alternative.
- **`shortService` caps at roughly 3 minutes** and cannot be extended. The auto-off timer offers
  stops up to **480 minutes**, so it does not qualify.
- **`camera` does not apply**, despite the torch being reached through `CameraManager`.
  `setTorchMode()` opens no capture session and requires no `CAMERA` permission. Claiming the
  camera type would be inaccurate *and* would add a `CAMERA` permission to the listing for
  nothing.
- No remaining type (`location`, `mediaPlayback`, `dataSync`, `health`, `phoneCall`,
  `remoteMessaging`, `mediaProjection`, `microphone`, `connectedDevice`, `systemExempted`)
  describes blinking a torch or watching an accelerometer.

Every service is started by a deliberate user action — tapping the app, a Quick Settings tile, or
a home screen widget — and each shows an ongoing notification for as long as it runs. Expect Play
to ask for a **demo video** showing each feature in use, and expect an 8-hour torch timer to draw
battery-policy attention; it is defensible because the user chooses the duration and the
notification stays visible throughout.

## Permissions

The app manifest declares only what the app's own code needs:

- `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_SPECIAL_USE` — the four services above
- `POST_NOTIFICATIONS` — their ongoing status notifications

The **merged** manifest adds three more, all from `androidx.work:work-runtime`, which arrives
transitively through `androidx.glance:glance-appwidget` for home screen widget refresh:

- `WAKE_LOCK`, `ACCESS_NETWORK_STATE`, `RECEIVE_BOOT_COMPLETED`

The app's own code never uses these. **Do not strip them** with `tools:node="remove"`: WorkManager
holds wake locks while executing work and touches network state during constraint-tracker
initialisation, so removing them risks a `SecurityException` in the widget path — a failure that
would only show up on users' devices.

The merged manifest also contains `com.handysparksoft.shakelamp.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`.
That is a signature-level permission the app defines for itself, added by `androidx.core` to guard
dynamically registered non-exported receivers. It is not a system permission and does not appear
on the Play listing.

Auditing tip: read `<uses-permission>` elements specifically. A plain text search also matches
`android:permission=` attributes on `<service>`/`<receiver>` elements — currently `DUMP`,
`BIND_JOB_SERVICE`, `BIND_REMOTEVIEWS`, `BIND_QUICK_SETTINGS_TILE` — which declare what *other*
callers must hold to bind to those components. They are not permissions this app requests.

Note that `INTERNET` is **not** declared and must stay that way. The privacy policy's central
claim rests on it, and it is trivially verifiable from the Play listing. Opening the Play listing
or the website from the About screen needs no network permission, because the browser or Play app
does the networking.

## Data safety

**Declare: no data collected, no data shared.**

The app has no analytics, no crash reporting, no advertising, no accounts, and no network access.
Everything it stores — emergency message, message history, shake settings, brightness,
transmission speed, loop pause, haptics, theme, language — lives in on-device DataStore and is
deleted when the app is uninstalled.

`ACCESS_NETWORK_STATE` does not imply collection: it permits reading whether a connection exists,
not using it, and without `INTERNET` the app cannot transmit anything regardless.

## Ads declaration

**Declare: this app does not contain ads.**

The previous version of this listing declared ads. That declaration is editable at any time under
**App content → Ads** and is meant to describe the *current* app; leaving it saying "contains ads"
when the app has none is itself the policy problem. There is no ad SDK in this build.

That earlier declaration was accurate for v1, which embedded an AdMob banner and requested
`INTERNET` and `ACCESS_NETWORK_STATE` to serve it. The archived v1 source in
[former-app-codebase/](former-app-codebase/) is where to confirm that if the declaration is ever
questioned.

**v2 never requests `INTERNET`**, in the app manifest or anywhere in the merged one, so it cannot
contact an ad network. `ACCESS_NETWORK_STATE` *does* still appear in the merged manifest — but it
is added by `androidx.work:work-runtime` (a Glance transitive dependency), not by an ad SDK. Check
the merged manifest, not the app's own, before answering any permission question:

```bash
grep -o 'android.permission.[A-Z_]*' \
  app/build/intermediates/merged_manifests/release/processReleaseManifest/AndroidManifest.xml | sort -u
```

## Store listing links

Both are served by the Firebase site in `web-landing/` (project `morseshakelamp`) and are the same
URLs the About screen opens:

- Privacy policy: `https://morseshakelamp.web.app/privacy-policy`
- Terms of use: `https://morseshakelamp.web.app/terms`

Deploy the site **before** submitting — Play validates that the privacy policy URL resolves.

## Screenshots

`docs/google-play/images/screenshots/` holds the Roborazzi-generated set, regenerated with
`./gradlew :app:recordRoborazziDebug`. The same images, converted to JPEG, are the landing page's
imagery, so refreshing them updates both.
