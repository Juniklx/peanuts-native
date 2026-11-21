# Crash Bandicoot N. Sane Trilogy — Android experimental build

For the **Switch base game 1.0.0 (v0), title ID `0100D1B006744000`**, this branch imports source-only public game metadata from [DroidPlay Core](https://github.com/enjoyerofslop/droidplay-core/tree/8115593e8a7f9ebfa0659008a1accadb9c79a8e9/dm/games/0100D1B006744000). This source's reported 30-FPS Galaxy Z Fold 8 test is **not** a Galaxy S24 FE test.

## Without a PC

The GitHub Actions workflow **Crash NST experimental Android APK** on `feat/crash-nst-android-experimental` builds only an isolated APK. When successful, download the **crash-nst-experimental-apk** ZIP from its run's **Artifacts** and extract the APK. Its Android package name is `dev.nxnative.android.crashnstv12test`. The signed Peanuts Native Release and the TUNIC test apps have different package IDs and must not be uninstalled.

The APK contains neither a game nor private files. Use your own Switch 1.0.0 dump with the app's Build/Prepare function. Import any needed keys/firmware only into the local Android app; never commit, message or publish them. Because this game compiles **two GLSLC shaders during startup**, the app may need to run its automated shader-capture phase to finish Prepare. If Build fails, save its **Build log**, not the game files.

## Diagnostics and limitations

- Preserve the source profile's memory layout for the first run; it is not validated on the Galaxy S24 FE.
- The experimental branch contains a last-resort rewriter fallback for a known N. Sane Trilogy `jump table` form ([nxnative issue #396](https://github.com/seedds/nxnative/issues/396)). It is not yet tested on the S24 FE. Report any `jump table ...` error precisely rather than editing offsets speculatively.
- Start with Game owned / original **30-FPS** behavior. Unlocking frame rates is a later, separate experiment.
- If a new APK cannot update the same experimental test app, Android probably rejected its debug signature. Do **not** clear data or remove TUNIC/the official app. The workflow can accept a persistent private `CRASH_NST_TEST_KEYSTORE_B64` secret for subsequent builds; never commit a keystore or paste it into chat.
- Report the game's dump version, RADV Xclipse or Samsung GPU driver, Build/Prepare output, game log, any Vulkan `[RED]` lines, FPS readout and whether menus/first level/saves work.

## First S24 FE Build log (2026-11-20 21:11 local)

From the attached `build-log-0100D1B006744000-20261120-211116-7860a2410a414d6abd396874d78ccbe5.zip` (not uploaded into the repository):

- Identify succeeded; the native rewrite completed (1000/1000); the initial shader split could not read 166/166 `archives/*.pak` files and stopped with `igArchive version 12: only 13 is known`.
- Not a confirmed bad dump, OOM or rewriter bug. Host's v13 IGA parser was simply not made for the v12 format in this game.
- Fix in this branch: add the upstream DroidPlay Core MIT-licensed v12 reader `dm/gfx/tools/iga.py` (40-bit offsets, build-path names, bounds checks, LZMA), dispatch *only* v12 archives to it; leave v13 CTR reader unchanged. A source-only synthetic v12 test runs in Actions before the APK build.
- Test again in the **updated APK**, not Retry with the old APK: the on-device Prepare engine lives inside the installed APK. The existing partial Build folder can be retried; don't delete your original dump or any TUNIC files. Further failures should be reported using a new Build log, especially `GLSLC` capture, shader map mismatch or memory errors.

The **first** experimental APK used package `dev.nxnative.android.crashnsttest`. Its private runner signing key was not saved. The v12 IGA reader build therefore uses a new package `dev.nxnative.android.crashnstv12test` and installs alongside the first one, avoiding a forced uninstall. Preserve the existing work folder and do not clear either app's storage. Subsequent updates of `crashnstv12test` still require a persistent signing secret.
