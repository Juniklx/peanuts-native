# Crash Bandicoot 4: It's About Time — Android feasibility survey

**Status: metadata identification only, NOT playable.** The base Switch title ID is
`010073401175E000`. Unlike N. Sane Trilogy (Vicarious Visions' Alchemy),
Crash 4 uses Unreal Engine 4. The fork's source and the checked upstream
`seedds/nxnative` and `enjoyerofslop/droidplay-core` repositories currently
have **no validated game profile** for this ID. Public short build IDs from
FPSLocker/cheat databases cannot safely replace full ExeFS build IDs, shader
tables and runtime validation.

## What this experiment provides

An **isolated** Android APK (`dev.nxnative.android.crash4survey`) adds a
read-only diagnostic to the normal **Add game** flow. Select a locally owned
Switch `.nup` or extracted folder for Crash 4. When the app correctly reports
that the title is not included, it will also offer **Copy metadata** containing:

- title ID and reported numeric dump versions;
- full NSO header build IDs for up to the newest three versions and known
  modules `main`, `subsdk0`, `subsdk1`, `subsdk2`, `sdk` when present.

The capture reads only NSO headers, never exports code, ROM data, keys,
shader binaries, local file paths or game content. Inspect the copied text
before sharing. An unreadable NSP/NUP is not proof of a damaged original game.

This app includes one **existing, unrelated** profile only to build the
launcher; it does **not** offer Crash 4 Prepare/Play, and the build explicitly
uses the **fake** Prepare engine to avoid producing misleading packs.
Do not try to launch Crash 4 or transfer saves with it.

## Android build

Workflow: `Crash 4 Android metadata survey APK` on branch
`feat/crash4-android-survey`. Upon a successful run download the
`crash4-metadata-survey-apk` artifact and install the APK independently.
Do not uninstall the user's working TUNIC, N. Sane Trilogy, combined test
or release apps. This test has a separate package ID; GitHub ephemeral
debug signing may prevent later in-place updates.

## Follow-up after the survey

The header report lets development target the **actual** game version and
modules. It does not itself provide a game profile. The next development step
is a source-only profile derived from the user's exact title build and
shader/RomFS facts, followed by independent compilation and on-device logs.
No FPS, graphics or compatibility claims are justified before that test.
