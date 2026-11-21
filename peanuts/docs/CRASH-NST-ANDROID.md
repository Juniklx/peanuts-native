# Crash Bandicoot N. Sane Trilogy — Android experimental build

For the **Switch base game 1.0.0 (v0), title ID `0100D1B006744000`**, this branch imports source-only public game metadata from [DroidPlay Core](https://github.com/enjoyerofslop/droidplay-core/tree/8115593e8a7f9ebfa0659008a1accadb9c79a8e9/dm/games/0100D1B006744000). This source's reported 30-FPS Galaxy Z Fold 8 test is **not** a Galaxy S24 FE test.

## Without a PC

The GitHub Actions workflow **Crash NST experimental Android APK** on `feat/crash-nst-android-experimental` builds only an isolated APK. When successful, download the **crash-nst-experimental-apk** ZIP from its run's **Artifacts** and extract the APK. Its Android package name is `dev.nxnative.android.crashnstnupfix`. The signed Peanuts Native Release and the TUNIC test apps have different package IDs and must not be uninstalled.

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

The **first** experimental APK used package `dev.nxnative.android.crashnsttest`. Its private runner signing key was not saved. The v12 IGA reader build therefore uses a new package `dev.nxnative.android.crashnstnupfix` and installs alongside the first one, avoiding a forced uninstall. Preserve the existing work folder and do not clear either app's storage. Subsequent updates of `crashnstv12test` still require a persistent signing secret.


## Second Galaxy S24 FE Build log: old app-private pack target (2026-11-20, 22:50 local)

The uploaded ZIP \`build-log-0100D1B006744000-20261120-225049-1ba60d21c1df4fda8dce56032f3ea155.zip\` identifies the installed APK as \`dev.nxnative.android.crashnstv12test\` at commit \`9f5d857\`. But the resumed \`<dump parent>/0100D1B006744000-work/ondevice/job.json\` was created on the *first* test APK and retained a pack destination \`/storage/emulated/0/Android/data/dev.nxnative.android.crashnsttest/files/packs/0100D1B006744000\`. All three retries from 22:37 through 22:47 failed at rewrite with \`PermissionError: [Errno 13]\` creating the *old app's* private directory. The split log in the ZIP is the **old** 21:10 v13 failure; it does **not** show that the current v12 parser failed.

The fix on this experimental branch is **source-level automatic pack target recovery**, not requiring uninstall, deletion of the shared work folder or re-dumping the game. When Retry/Resume starts, \`PackService\` checks whether an unfinished job owns a default \`packs/<title ID>\` in another \`dev.nxnative.android.*\` app's inaccessible \`Android/data/<old package>/files\`; if so, and its *current app* destination is empty, the service saves a corrected pack path \`Android/data/<current package>/files/packs/<title ID>\` to the same \`job.json\`. A small standalone Java policy, \`PackRebind.java\`, limits that migration to default app-private paths; intentionally selected paths, adopted packs, code mods and existing populated destination folders remain untouched. Existing shared working files stay in place and the service verifies which steps must run again. The workflow tests this policy using pure JVM cases.

The new APK has Android app ID \`dev.nxnative.android.crashnstnupfix\` to avoid a signing-certificate conflict with the installed \`crashnstv12test\` build. The old Crash test apps and TUNIC stay intact. Select the same dump and press Retry/Resume; the next **fresh** log should name the new app ID in both the summary and pack path. If it still displays an old package ID under \`pack:\`, save a new log and stop rather than clearing private data. The v12 shader parser has not yet been validated on this phone's dump.

## Third Galaxy S24 FE Build log: virtual NUP archives (2026-11-20 23:08 local)

The attached Build log from APK commit \`4330e8e\` confirms cross-APK pack rebinding succeeded: the destination is \`dev.nxnative.android.crashnstv12fix/files/packs/0100D1B006744000\`; the native rewrite completed; the split advanced to 171/171 files. However, 166 RomFS \`archives/*.pak\` entries failed as \`FileNotFoundError\` using paths of the form \`Crash Bandicoot N. Sane Trilogy.nup:base/romfs/archives/shaders_nx.pak\`. This is **a virtual NUP member path**, not an actual filesystem path, so the diagnostic's generic suggestion to redump is unsupported. The split only collected 10 shaders from \`main\`.

The cause is isolated: \`shader_split.py\` already imports \`open\` from \`dm/tools/romfs.py\`, which handles these virtual NUP member paths. The imported \`dm/gfx/tools/iga.py\` v12 reader's \`shader_files\` defaults to builtin \`open\`, which treats \`.nup:base/romfs/...\` as a literal filename. The fixed dispatcher now passes \`opener=open\` explicitly, preserving its existing v13 dispatch. An AST-extracted regression test in \`dm/tools/tests/test_split_iga12.py\` simulates a virtual NUP stream, checks both reads use the virtual opener, and runs in GitHub Actions preflight.

The test APK is now \`dev.nxnative.android.crashnstnupfix\`, intentionally separate from previous debug-signed builds. A resumed job remains in the shared work folder beside the dump; the cross-APK pack redirect migrates its old destination to the new app's empty pack folder. The game files, saves and TUNIC installation are untouched. The full shader library, dynamic GLSLC capture and the game's actual launch remain **unverified**.

## S24 FE FPS menu experiment (2026-11-20)

The user reports that Crash NST v0 now launches and plays on a Galaxy S24 FE, with
a frame-rate display around 30 FPS and all explicit frame-rate caps greyed out,
as in the initial TUNIC test. The profile had no frame-rate UI rows, so this
branch now declares `fps_limit_present 1`, `fps_limit_min 15`, and
`fps_limit_max 120`, matching TUNIC's opt-in *presentation-cap* experiment.
Those settings only make the selector available. They do not prove or force
a change to Crash's original Switch game-engine 30-FPS cadence. Test 60 FPS
first (then 90/120 if appropriate), checking real frame delivery, game speed,
cutscenes, saves and responsiveness; switch back to Game owned if anything
behaves incorrectly. The source-supported Switch 1.0.0 60-FPS patch is a
separate prospective engine-level investigation, not applied by this change.

The FPS test APK uses `dev.nxnative.android.crashnstfpstest` to avoid a
temporary GitHub Actions signing-key conflict. It installs alongside the
known-working `crashnstnupfix` and TUNIC APKs; do not uninstall them or
delete their data. A new app package may need its own Prepare data.
