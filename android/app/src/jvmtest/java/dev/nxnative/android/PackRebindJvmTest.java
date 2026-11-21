package dev.nxnative.android;

import java.io.File;

/** PEANUTS: pure JVM regression test for cross-APK job.json pack-path rebinding. */
public final class PackRebindJvmTest {
    private static int checks;
    private static void check(boolean ok, String why) {
        if (!ok) throw new AssertionError(why);
        checks++;
    }

    public static void main(String[] args) {
        String id = "0100D1B006744000";
        String current = "dev.nxnative.android.crashnstv12fix";
        File files = new File("/storage/emulated/0/Android/data/" + current + "/files");
        File old = new File("/storage/emulated/0/Android/data/dev.nxnative.android.crashnsttest/files/packs/" + id);
        File expected = new File(files, "packs/" + id);
        check(expected.equals(PackRebind.target(files, old, id, current, true, false, false)), "old test app job rebind");
        check(PackRebind.target(files, old, id, current, false, false, false) == null, "user-owned pack");
        check(PackRebind.target(files, old, id, current, true, true, false) == null, "adopted pack");
        check(PackRebind.target(files, old, id, current, true, false, true) == null, "modded job");
        check(PackRebind.target(files, expected, id, current, true, false, false) == null, "already current");
        check(PackRebind.target(files, new File("/storage/emulated/0/Download/my-pack/" + id), id, current, true, false, false) == null, "custom shared pack");
        check(PackRebind.target(files, new File("/storage/emulated/1/Android/data/dev.nxnative.android.crashnsttest/files/packs/" + id), id, current, true, false, false) == null, "other storage volume");
        check(PackRebind.target(files, new File("/storage/emulated/0/Android/data/com.thirdparty/files/packs/" + id), id, current, true, false, false) == null, "unrelated third-party app");
        check(PackRebind.target(files, old, "0100DA801624E000", current, true, false, false) == null, "different game ID");
        check(PackRebind.target(files, old, id, "dev.nxnative.android.wrong", true, false, false) == null, "wrong current package");
        check(PackRebind.target(null, old, id, current, true, false, false) == null, "no external storage");
        check(PackRebind.target(files, new File("/storage/emulated/0/Android/data/dev.nxnative.android.crashnsttest/files/custom/" + id), id, current, true, false, false) == null, "not a default packs folder");
        System.out.println("PackRebindJvmTest passed: " + checks + " cases");
    }
}
