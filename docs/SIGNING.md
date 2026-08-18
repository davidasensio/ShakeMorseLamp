# ShakeMorseLamp — Signing & Keystore

Everything needed to build a release Play will accept. Read this before touching anything under
`keystore/`, and before running any signing tool.

**Passwords are not in this document and must never be.** They live only in the git-ignored
`keystore/keystore_old/keystore.properties`.

## The short version

| | |
|---|---|
| Keystore the build uses | `keystore/keystore_old/release_certificate.jks` |
| Alias | **`release_certificate`** |
| Certificate | `CN=rocketsoft`, valid 2015-03-11 → 2040-03-04 |
| SHA1 Play checks against | **`…BB:12:F0:30`** |
| Configured in | `keystore/keystore_old/keystore.properties`, read by `app/build.gradle.kts` |

## ⚠️ This keystore is shared across several apps

`release_certificate.jks` holds signing keys for **more than one published app**, not just this
one. It currently contains three private key entries:

| alias | certificate | SHA1 ends | belongs to |
|---|---|---|---|
| `release_certificate` | `CN=rocketsoft`, 2015 | `…BB:12:F0:30` | **this app** — the one Play accepts |
| `release_certificate_bundle` | `CN=David Asensio, L=Valencia`, 2021 | `…D5:F0:BB:02` | another app's App Bundle migration |
| `release_certificate_bundle_01` | `CN=David Asensio`, 2021 | `…CE:EA:85:8E` | another app's App Bundle migration |

Two things follow.

**Never prune "unused" aliases.** Two of the three look irrelevant to this project. They are other
apps' keys. Deleting them destroys those apps' ability to ship updates.

**Back this file up off-machine, and treat it as critical.** Losing it does not cost one listing,
it costs every app whose key it holds. Neither `CN=rocketsoft` nor the 2021 keys can be
regenerated.

## Which alias signs the upload, and why the others fail

Play requires **`release_certificate`**. The app is signed with its original 2015 key, and no
separate upload key was ever registered with Google for this listing, so uploads must carry the
app signing key itself.

Signing with either `_bundle` alias produces a build that compiles, signs, and installs locally
without complaint, then is rejected at upload:

> Your Android App Bundle is signed with the wrong key … expected fingerprint SHA1 `…BB:12:F0:30`
> but the certificate used has fingerprint SHA1 `…CE:EA:85:8E`

**Do not validate a build by comparing it against a previous local build.** A locally signed
artifact proves only that signing succeeded, not that the right key was used. That reasoning error
is how the wrong alias reached an upload attempt. Compare against Play Console → Setup → App
signing, or against the fingerprint above.

Check any build before uploading:

```bash
unzip -p app/build/outputs/bundle/release/app-release.aab 'META-INF/*.RSA' \
  | openssl pkcs7 -inform DER -print_certs | openssl x509 -noout -fingerprint -sha1
```

## Folder layout, and why the names mislead

```
keystore/                              ← the only home for signing material
├── keystore_old/                      ← ACTIVE. The build reads this.
│   ├── release_certificate.jks        ← valid JKS, all three aliases
│   ├── keystore.properties            ← the only .properties with the correct alias
│   └── keystore.properties.bak
├── keystore_new/                      ← the 2021 PKCS12 conversion. NOT what the build uses.
│   ├── release_certificate_pkcs12.jks ← same three aliases, PKCS12 format
│   └── keystore_pkcs12.properties     ← names the alias Play REJECTS (see below)
└── private_key.pepk                   ← 2021 Play App Signing export (see history)
```

Everything signing-related lives here and nowhere else. Duplicate copies previously existed under
`docs/google-play/` and in a `keystore_new_other/` folder; both were removed because three copies
on one disk protect against a stray overwrite but not against losing the machine, and each extra
copy carried a `.properties` file naming the wrong alias.

The names are backwards: **`keystore_old` is the live one**, and `keystore_new` is the older 2021
migration artifact. The build was once wired to `keystore_new` on the assumption that "new" meant
current; it does not. Renaming `keystore_old` → something honest would prevent a repeat, but it
touches signing material, so it has not been done unilaterally.

The PKCS12 (7131 bytes, magic `30 82`) holds the same three aliases as the JKS, so it *could* sign
correctly — **provided the alias is `release_certificate`**. Its
`keystore_new/keystore_pkcs12.properties` specifies `release_certificate_bundle_01`, the alias Play
rejects. Fix the alias before ever pointing a build at that folder.

### Hazard: PEPK overwrites its destination

`keystore_new/release_certificate_pkcs12.jks` was once destroyed by pointing `pepk.jar --output=`
at the path where the keystore already lived. PEPK writes its encrypted export over the
destination without warning. The result was a 1.6 KB file starting `0x13` (DER `PrintableString`)
where a keystore must start `FEED FEED` (JKS) or `30 82` (PKCS12), which fails as:

```
Failed to read key release_certificate from store "…/release_certificate_pkcs12.jks":
toDerInputStream rejects tag type 19
```

That file was restored from a copy. **Always write PEPK output to a fresh filename** — the run that
caused this had been given the keystore's own path — and keep a copy of any keystore off-machine
before running signing tools near it. Nothing here is unrecoverable except the keys themselves:
`pepk.jar` and Google's encryption public key are re-downloadable from Play Console, and a fresh
PEPK export can be produced from the keystore at any time.

## History: why a PKCS12 file exists at all

In **November 2021** the App Bundle migration required enrolling apps in Play App Signing, which
means exporting the existing signing key to Google with `pepk.jar`. PEPK needed the keystore in
PKCS12 format, because Java deprecated JKS as its default in Java 9 and `keytool` began advising
migration. The migration command keeps the original filename, which is how a PKCS12 file ended up
named `..._pkcs12.jks`.

Evidence that this was a format conversion and not a new key: in the PKCS12 the
`release_certificate` entry has creation date 2021-11-30 while its certificate is still valid from
2015-03-11, and its fingerprint is unchanged. Same key, re-housed.

**That migration was carried out for other apps, not this one.** The `_bundle` aliases and
`private_key.pepk` are residue from that work. This listing never registered a separate upload
key, which is why Play still expects the 2015 key here.

If a separate upload key is ever wanted for this app — worth having, since an upload key can be
reset if compromised while an app signing key cannot — `release_certificate_bundle_01` already
exists and would need registering through Play Console support.

## Build configuration

`app/build.gradle.kts` reads `keystore/keystore_old/keystore.properties`, with `SIGNING_KEY_ALIAS`,
`SIGNING_KEY_PASSWORD`, `SIGNING_STORE_FILE` and `SIGNING_STORE_PASSWORD` Gradle properties or
environment variables taking precedence, so CI can sign without the file present. A missing file
only prints a warning, so debug builds and fresh clones still work.

`storeFile` in that properties file is resolved relative to the `app/` module directory, not the
repo root.

## What is git-ignored

The whole `keystore/` directory, plus `*.pepk`, `*.p12`, `*.pfx`, `*.jkss`, `pepk.jar` and
`keystore*.properties`. Nothing signing-related has ever been
committed, and it must stay that way — see `AGENTS.md`.

Because `keystore/` is ignored as a *directory*, a README placed inside it could never be
committed: git cannot re-include a file whose parent directory is excluded. That is why this
document lives in `docs/`.
