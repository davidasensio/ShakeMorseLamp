# ShakeLamp v1 — archived source

`ShakeLamp.zip` is a snapshot of the **original app this project replaced**. ShakeMorseLamp is not
a new listing: it is a ground-up rewrite published over the same Play entry, under the same
`applicationId`. This archive is kept so that history is recoverable — nothing here is built,
tested, or referenced by the current app.

**Do not restore anything from it into the app.** It is a reference for questions like "what did
the old version do?", "why does the store listing say X?", or "what did users of v1 expect?".

## What it is

| | |
|---|---|
| Archive | `ShakeLamp.zip` — 225 KB, 111 entries |
| Version in the snapshot | `1.0.5` (versionCode 5) |
| Application ID | `com.handysparksoft.shakelamp` — **same as v2** |
| Language | Java (11 source files), XML layouts |
| SDK | minSdk 21, target/compileSdk 29, Java 1.6 source compatibility |
| Key dependencies | `com.android.support:appcompat-v7:22.0.0`, `play-services:6.5.87`, AGP 4.1.1 |
| First released | 2015-04-17 (per the change log in `MainActivity.java`) |

Source files: `MainActivity`, `ShakeLamp`, `ShakeService`, `SettingsActivity`, `SeekBarPreference`,
`BootUpCompletedReceiver`, `ScreenStateReceiver`, `ShareApp`, `AdCustomListener`, `Constants`.

## What changed between v1 and v2

These differences are the reason the app was renamed and rewritten rather than updated.

- **v1 had no Morse code at all.** It was a flashlight with shake-to-toggle and a settings screen.
  Every Morse feature — encoding, transmission, speed, loop pause, quick signals, SOS tile, the
  send history — is new in v2. Hence *ShakeLamp* → *Shake**Morse**Lamp*.
- **v1 showed ads and v2 does not.** v1 embedded an AdMob banner and declared `INTERNET` and
  `ACCESS_NETWORK_STATE` to serve it. v2 contains no ad SDK and never requests `INTERNET`.
  (`ACCESS_NETWORK_STATE` does still reach v2's *merged* manifest, but from WorkManager via Glance
  — not from ads.) This matters when answering the Play Console's ads and data-safety questions,
  since the listing's *previous* answers described v1 — see
  [../PLAY_RELEASE.md](../PLAY_RELEASE.md).
- **v1 shipped in English and Spanish only** (`values/` and `values-es/`). v2 ships 7 locales with
  an in-app language picker.
- **v1 used the Android Support Library and `ActionBarActivity`.** v2 is Compose, Kotlin,
  multi-module and Clean Architecture.

## Version numbering

The snapshot sits at versionCode 5; v2 started at versionCode 20 (`2.0.0`) and is currently 21.
The gap is deliberate headroom — **whatever the listing published between 1.0.5 and the rewrite is
not represented in this archive**, so do not read versionCode 5 as "the last released v1".

## It will not build today

This is an archive, not a working project. `jcenter()` — which both `build.gradle` files rely on
— has been shut down, `play-services:6.5.87` is not on Maven Central, the `compile` configuration
was removed in Gradle 7, and the Support Library has been superseded by AndroidX. Reviving it
would be a porting exercise, not a checkout.

The zip also carries `__MACOSX/` and `.DS_Store` entries from the machine it was made on. They are
harmless; it is left byte-for-byte as received rather than repacked, so it stays a faithful copy.

## Contents check

The archive holds no signing material: no keystore, no `signingConfig`, no `google-services.json`,
and no credentials. Its `local.properties` contains only a stale local SDK path.

It does contain two AdMob ad unit IDs in `strings.xml` — one of them Google's public test unit.
Ad unit IDs are not secrets: they are compiled into every shipped APK and readable by anyone who
decompiles it. Signing material, by contrast, never belongs in the repository — see
[../SIGNING.md](../SIGNING.md).
