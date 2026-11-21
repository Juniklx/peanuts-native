# PEANUTS: TUNIC + Crash N. Sane Trilogy combined Android test

The separate Android APK includes **TUNIC** Switch update **1.0.3** (`0100DA801624E000`) and **Crash Bandicoot N. Sane Trilogy** Switch base **1.0.0** (`0100D1B006744000`). TUNIC retains the 2-GiB guest pool and optional FPS presentation caps; Crash retains the igArchive v12/NUP member handling, recovery of stale test pack destinations, and presentation caps. This is not a game ROM, compiler-output bundle, or a public release.

## Build on GitHub, without a PC

Open GitHub Actions on branch `feat/tunic-crash-unified-android`, select **TUNIC and Crash NST unified Android APK**, then its successful run and artifact **tunic-crash-unified-apk**. Unzip and install the APK. The package ID is `dev.nxnative.android.tuniccrashdual`. Both game profiles are compiled via `-PnxGames=0100DA801624E000,0100D1B006744000`. The app includes the native on-device Prepare engine but **does not include game files, ROMs, keys, saved games or compiled shader libraries**.

## Storage and first test

The dual-game app uses its *own* `Android/data` directory. Keep the two known-working test apps installed; their packs, keys, saves and settings are **not automatically copied** to this new app. If necessary, run Prepare separately with your own locally stored dumps. You can consider a carefully verified, backed-up migration of individual pack folders or the application's save export/import tool where supported. Never delete the existing single-game apps or their working data just to fix an update/signature issue. The Crash PackRebind recovery only corrects specific stale default work-folder targets, not arbitrary pack or save migration.

Test launching TUNIC and Crash separately from the unified library, gamepad, sound, intro, first level, save/reload and real FPS. Both menus can expose higher **presentation-only FPS caps**: TUNIC explicitly allows 15–120, Crash 20–120. The selectable number is a ceiling, **not proof the game engine unlocked above 30 FPS**. Start with Game owned, then experiment with 60 FPS and check speed/timing. Return to Game owned if behavior changes unexpectedly.

## Signing and limitations

The workflow optionally reads GitHub secret `DUAL_TEST_KEYSTORE_B64` (a private debug keystore encoded as base64) to keep a stable signature for future dual-app updates; never commit the keystore. Without it, each fresh Actions runner may sign the APK with a different ephemeral certificate, making same-package upgrades fail. Keep known-good builds safe. A successful compilation is not proof of dual-game runtime or save compatibility; collect the Build/game log if something fails.
