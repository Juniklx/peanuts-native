# Crash Bandicoot N. Sane Trilogy — Android experimental build

For the **Switch base game 1.0.0 (v0), title ID `0100D1B006744000`**, this branch imports source-only public game metadata from [DroidPlay Core](https://github.com/enjoyerofslop/droidplay-core/tree/8115593e8a7f9ebfa0659008a1accadb9c79a8e9/dm/games/0100D1B006744000). This source's reported 30-FPS Galaxy Z Fold 8 test is **not** a Galaxy S24 FE test.

## Without a PC

The GitHub Actions workflow **Crash NST experimental Android APK** on `feat/crash-nst-android-experimental` builds only an isolated APK. When successful, download the **crash-nst-experimental-apk** ZIP from its run's **Artifacts** and extract the APK. Its Android package name is `dev.nxnative.android.crashnsttest`. The signed Peanuts Native Release and the TUNIC test apps have different package IDs and must not be uninstalled.

The APK contains neither a game nor private files. Use your own Switch 1.0.0 dump with the app's Build/Prepare function. Import any needed keys/firmware only into the local Android app; never commit, message or publish them. Because this game compiles **two GLSLC shaders during startup**, the app may need to run its automated shader-capture phase to finish Prepare. If Build fails, save its **Build log**, not the game files.

## Diagnostics and limitations

- Preserve the source profile's memory layout for the first run; it is not validated on the Galaxy S24 FE.
- The experimental branch contains a last-resort rewriter fallback for a known N. Sane Trilogy `jump table` form ([nxnative issue #396](https://github.com/seedds/nxnative/issues/396)). It is not yet tested on the S24 FE. Report any `jump table ...` error precisely rather than editing offsets speculatively.
- Start with Game owned / original **30-FPS** behavior. Unlocking frame rates is a later, separate experiment.
- If a new APK cannot update the same experimental test app, Android probably rejected its debug signature. Do **not** clear data or remove TUNIC/the official app. The workflow can accept a persistent private `CRASH_NST_TEST_KEYSTORE_B64` secret for subsequent builds; never commit a keystore or paste it into chat.
- Report the game's dump version, RADV Xclipse or Samsung GPU driver, Build/Prepare output, game log, any Vulkan `[RED]` lines, FPS readout and whether menus/first level/saves work.
