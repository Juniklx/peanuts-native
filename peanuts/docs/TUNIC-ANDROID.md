# TUNIC on Android (experimental)

**Status:** Source profile added for Switch TUNIC v1.0.3 (v196608), title ID `0100DA801624E000`. The imported shader map is from the public nxnative profile, which was validated **on a Mac**, not on Android. An Android first-run log confirms guest initialization but a crash before a playable picture at the original 1 GiB application pool. Vulkan gameplay compatibility has **not** been confirmed. Do not call this game supported yet.

## APK without a PC

This fork includes the [TUNIC experimental Android APK workflow](../../.github/workflows/tunic-experimental-apk.yml). It compiles an isolated app (`dev.nxnative.android.tunicmemtest`, the 2-GiB test build) with the **native on-device preparation engine**, so the regular Peanuts Native installation and saves are not overwritten. The Action uses GitHub-hosted build tools, not files from your phone. It does not build or publish any copyrighted game data, prod.keys, ROM contents, shaders, or game packs.

- Open **Actions** in the GitHub repository and find **TUNIC experimental Android APK**. This workflow runs on a pull request touching the TUNIC profile or workflow. Manual **Run workflow** is available once the workflow file exists on the default branch.
- If the Action succeeds, obtain `tunic-experimental-apk` from the run's **Artifacts**, extract the APK on your Android device and install it as a separate test app. A build artifact is not proof the game works.
- In the test app select your own `prod.keys`, firmware dump and game folder. Import your legitimately dumped TUNIC base game and the corresponding **1.0.3** update. Prepare it on the phone through the game library; this can take significant time and storage and may fail on an unsupported GPU.
- Start the game from the app's library. If it fails, use **Share log** on that game's page or the crash dialog. Share only logs after checking for private content, **never** game files, firmware or keys.
- Test the title screen and beach, gamepad, audio, graphics, saving, and monitor RAM and Vulkan errors before attempting any optimization. The system Vulkan driver is the starting point on non-Adreno hardware; Turnip does not directly support Xclipse.

## Limits

The upstream nxnative profile used 60 fps and a 1 GiB application pool. The first Android run filled the 1 GiB application pool and crashed; the experimental profile now uses 2 GiB, **not yet retested** on this game. The 60 fps target was not applied by the host in that first log. The original Mac reports reached the opening area; later scenes were not verified. The 2 GiB test is based on the equivalent Chained Echoes Unity/SDK configuration. Monitor guest alias address-space use and Android memory before changing anything else. If a new run fails, diagnose the new log instead of blindly increasing the pool or removing draws.

The repository README recommends Android 13+, Snapdragon Vulkan 1.3 and 12 GiB or more of RAM. Devices outside that range (including Exynos/Xclipse phones with 8 GiB) are experimental and may fail at build, preparation, or game startup.

For a desktop-pack alternative, follow [NEW-GAME.md](NEW-GAME.md) and [BUILDER.md](BUILDER.md). Keep dumps, generated shader libraries, packs, and APKs out of Git commits.

## Preserving your compiled shaders when Android rejects an update

The original **1-GiB** test app has package ID `dev.nxnative.android.tunictest`, signed on an ephemeral GitHub runner. Its **2-GiB** successor now builds as `dev.nxnative.android.tunicmemtest` to install **alongside** it. No uninstall is needed; your existing TUNIC shader pack is stored at:

```text
/storage/emulated/0/Android/data/dev.nxnative.android.tunictest/files/packs/0100DA801624E000
```

You can reuse the existing generated shader library and rewritten modules: only the application's **runtime** `app_pool` changed; the shader map, rewriter build IDs and shader translator did not. **Do not delete the old test app or clear its storage** until the new one has launched with the copied pack.

With a trusted device-local ADB-shell interface (for example, a Shizuku-authorized terminal running as Android's `shell` user), copy the existing pack to a neutral directory first:

```sh
mkdir -p /sdcard/Download/TUNIC-pack-backup
cp -R /sdcard/Android/data/dev.nxnative.android.tunictest/files/packs/0100DA801624E000 /sdcard/Download/TUNIC-pack-backup/
```

Install and open the **new** 2-GiB test APK at least once. Then restore to the separate test app's folder:

```sh
mkdir -p /sdcard/Android/data/dev.nxnative.android.tunicmemtest/files/packs
cp -R /sdcard/Download/TUNIC-pack-backup/0100DA801624E000 /sdcard/Android/data/dev.nxnative.android.tunicmemtest/files/packs/
```

Some file managers cannot access `Android/data` on Android 11+. Use an authorized ADB shell or another supported Android storage manager; do **not** use `su` or weaken Android security. The game dump remains outside app storage at its existing user-selected location; reselect it in the new app. Old test-app settings, custom GPU driver files in private storage, keys and firmware may also need re-importing. If the new app checks the shader library and requests regeneration, stop and inspect its log before deleting or preparing anything.

### Persistent signatures for *future* test APK updates

A public APK contains the signing **certificate**, **not** its private key. GitHub-hosted runners generate fresh debug keys unless a keystore is explicitly saved. An APK signed by the old runner cannot be updated by a new key; this is not fixed by changing the APK version.

To keep **later** GitHub Actions APKs updatable under the **same package ID**, create a private Android debug keystore (alias `androiddebugkey`, store password and key password `android`) in a secure location and store **only its base64 encoding** in a private GitHub Actions repository secret called `TUNIC_TEST_KEYSTORE_B64`. The workflow restores the keystore to `~/.android/debug.keystore` **before** Gradle builds the APK. This secret cannot recover or match the unrecoverable old runner key. Protect the keystore and **never** paste it into an issue, pull request, commit or chat.

For example, using Termux with `keytool` and `base64` available (after installing OpenJDK in Termux):

```sh
keytool -genkeypair -alias androiddebugkey -keyalg RSA -keysize 3072 -validity 10000 -keystore "$HOME/tunic-test.keystore" -storepass android -keypass android -dname "CN=TUNIC Testing,O=Local,C=BR"
base64 -w 0 "$HOME/tunic-test.keystore" > "$HOME/tunic-test-secret.txt"
```

Paste the full contents of `tunic-test-secret.txt` into your GitHub repository's **Settings → Secrets and variables → Actions → New repository secret** as `TUNIC_TEST_KEYSTORE_B64` (privately). Keep a secure backup of the keystore. The next build will establish the persistent signature for the **new** test app; from then on, keep the secret unchanged for in-place upgrades.

## Test APK updates and signing

GitHub-hosted builds can sign debug APKs with **different keys**. If the APK install reports a signature conflict, do not uninstall the installed app without backing up data you want to keep. The original official Peanuts Native app is separate and must not be uninstalled. Do not copy keystores or game files into this public GitHub repository.

## First-run checklist

1. Workflow passes and produces an APK (build verification only).
2. Game import and native Prepare succeed (record game update and shader counts).
3. The game starts without a crash and reaches its first 3D scene on the system Vulkan driver.
4. Real gamepad, audio, render correctness, memory usage, and saves are tested.
5. Only after on-device proof, consider adding TUNIC to `peanuts/release-titles.txt` and `peanuts/games.tsv` and calling it supported.
