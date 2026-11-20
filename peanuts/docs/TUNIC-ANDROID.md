# TUNIC on Android (experimental)

**Status:** Source profile added for Switch TUNIC v1.0.3 (v196608), title ID `0100DA801624E000`. The imported shader map is from the public nxnative profile, which was validated **on a Mac**, not on Android. No Android boot or GPU compatibility has been confirmed. Do not call this game supported yet.

## APK without a PC

This fork includes the [TUNIC experimental Android APK workflow](../../.github/workflows/tunic-experimental-apk.yml). It compiles an isolated app (`dev.nxnative.android.tunictest`) with the **native on-device preparation engine**, so the regular Peanuts Native installation and saves are not overwritten. The Action uses GitHub-hosted build tools, not files from your phone. It does not build or publish any copyrighted game data, prod.keys, ROM contents, shaders, or game packs.

- Open **Actions** in the GitHub repository and find **TUNIC experimental Android APK**. This workflow runs on a pull request touching the TUNIC profile or workflow. Manual **Run workflow** is available once the workflow file exists on the default branch.
- If the Action succeeds, obtain `tunic-experimental-apk` from the run's **Artifacts**, extract the APK on your Android device and install it as a separate test app. A build artifact is not proof the game works.
- In the test app select your own `prod.keys`, firmware dump and game folder. Import your legitimately dumped TUNIC base game and the corresponding **1.0.3** update. Prepare it on the phone through the game library; this can take significant time and storage and may fail on an unsupported GPU.
- Start the game from the app's library. If it fails, use **Share log** on that game's page or the crash dialog. Share only logs after checking for private content, **never** game files, firmware or keys.
- Test the title screen and beach, gamepad, audio, graphics, saving, and monitor RAM and Vulkan errors before attempting any optimization. The system Vulkan driver is the starting point on non-Adreno hardware; Turnip does not directly support Xclipse.

## Limits

The upstream nxnative profile uses 60 fps and a 1 GiB application pool, but neither is a validated Android setting. The original Mac reports reached the opening area; later scenes were not verified. If an Android boot fails due to allocations or shader compilation, diagnose the log instead of blindly increasing the pool or removing draws.

The repository README recommends Android 13+, Snapdragon Vulkan 1.3 and 12 GiB or more of RAM. Devices outside that range (including Exynos/Xclipse phones with 8 GiB) are experimental and may fail at build, preparation, or game startup.

For a desktop-pack alternative, follow [NEW-GAME.md](NEW-GAME.md) and [BUILDER.md](BUILDER.md). Keep dumps, generated shader libraries, packs, and APKs out of Git commits.

## First-run checklist

1. Workflow passes and produces an APK (build verification only).
2. Game import and native Prepare succeed (record game update and shader counts).
3. The game starts without a crash and reaches its first 3D scene on the system Vulkan driver.
4. Real gamepad, audio, render correctness, memory usage, and saves are tested.
5. Only after on-device proof, consider adding TUNIC to `peanuts/release-titles.txt` and `peanuts/games.tsv` and calling it supported.
