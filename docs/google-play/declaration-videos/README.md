# Play Console declaration videos

Screen recordings Google asks for when reviewing a Play Console declaration. The video files
themselves are **git-ignored** — they are large binaries that would bloat the repository and change
on every re-record. This README is committed so the folder still explains itself.

Keep the recordings here locally, and back them up wherever you keep the signing material.

## Current videos

| file | declaration it supports |
|---|---|
| `foreground-service-declaration.mp4` | Foreground service types — `specialUse` |

## Foreground service declaration

Play reviews `specialUse` by hand and generally asks for a video showing each service actually
running. The written justification lives in [../../PLAY_RELEASE.md](../../PLAY_RELEASE.md); the
video needs to show the same four behaviours:

1. **Auto-off timer** — switch the torch on, choose a duration, lock the screen, show it turning
   itself off.
2. **Home screen widget** — tap a configured widget and show the phrase transmitting.
3. **Emergency SOS** — tap the Quick Settings tile and show the message looping, then stop it.
4. **Shake detection** — enable the gesture in Settings, lock the screen, shake, show the torch
   coming on.

**Grant the notification permission before recording.** The justification rests on each service
showing an ongoing notification for as long as it runs. On Android 13+ a denied `POST_NOTIFICATIONS`
suppresses those notifications from the drawer — the services still run, but the recording would
contradict the written justification.

Upload the file to YouTube as **unlisted** and paste that link into the Console; it does not accept
a direct file.
