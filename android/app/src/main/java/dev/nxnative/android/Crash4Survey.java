package dev.nxnative.android;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * PEANUTS: read-only feasibility survey for Crash Bandicoot 4 on Switch.
 * This title has no verified nxnative profile: do not offer Prepare or Play.
 *
 * Only reads the first 0x60 bytes of existing ExeFS NSO headers through
 * DumpLayout/BuildCheck (including virtual NUP paths). It never hashes an
 * entire game module, writes game files, scans keys or uploads anything.
 * The returned report intentionally omits local file names/paths.
 */
final class Crash4Survey {
    static final String TITLE_ID = "010073401175E000";
    private static final String[] MODULES = {"main", "subsdk0", "subsdk1", "subsdk2", "sdk"};
    private Crash4Survey() {}

    static String report(File game) {
        StringBuilder out = new StringBuilder()
                .append("Crash Bandicoot 4: It's About Time")
                .append("\nTitle ID: ").append(TITLE_ID)
                .append("\nStatus: no verified static-recompilation profile; metadata only.")
                .append("\nNo game data, keys, paths or shaders are included in this report.");

        List<Long> versions = DumpLayout.versions(game);
        if (versions.isEmpty()) return out.append("\nNo ExeFS build version detected. Try a complete .nup or extracted dump.").toString();

        // Bounded by three recent versions. A very large title archive must not
        // cause an unbounded series of reads just to display a dialog.
        for (int vi = Math.max(0, versions.size() - 3); vi < versions.size(); vi++) {
            long v = versions.get(vi);
            out.append("\n\nVersion: v").append(v);
            boolean seen = false;
            for (String name : MODULES) {
                try {
                    BuildCheck.Mod mod = BuildCheck.mod(game, Long.toString(v), name);
                    if (!mod.exists()) continue;
                    seen = true;
                    String id = BuildCheck.nsoBuildId(mod.head(0x60), name);
                    // Full 64-hex NSO header identifier. Profiles use its first 32 hex.
                    out.append("\n").append(name).append("=").append(id);
                } catch (IOException | RuntimeException e) {
                    seen = true;
                    // Do not propagate exception text: it may contain a private
                    // absolute path from the Android storage provider.
                    out.append("\n").append(name).append("=<unreadable NSO header>");
                }
            }
            if (!seen) out.append("\nNo recognized NSO module header found.");
        }
        out.append("\n\nShare only this copied metadata, not the dump or keys.");
        return out.toString();
    }
}
