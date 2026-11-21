package dev.nxnative.android;

import java.io.File;

/**
 * PEANUTS: recover a resumed on-device Prepare job whose pack path still points into the
 * Android/data directory of an older, separately installed nxnative test APK.
 *
 * Pure path policy: this only suggests a new target. It never reads, copies, deletes or
 * changes the other application's private data. In particular, user-picked pack paths,
 * adopted packs, fake/modded jobs and already-current app packs are not redirected.
 */
final class PackRebind {
    private PackRebind() {}

    static File target(File thisFiles, File recordedPack, String titleId, String thisPackage,
                       boolean jobOwnsPack, boolean adopted, boolean modded) {
        if (!jobOwnsPack || adopted || modded || thisFiles == null || recordedPack == null
                || titleId == null || thisPackage == null || !titleId.matches("[0-9A-F]{16}")
                || !thisPackage.startsWith("dev.nxnative.android.")) return null;

        File thisApp = thisFiles.getParentFile();
        File dataRoot = thisApp == null ? null : thisApp.getParentFile();
        File androidRoot = dataRoot == null ? null : dataRoot.getParentFile();
        File oldPacks = recordedPack.getParentFile();
        File oldFiles = oldPacks == null ? null : oldPacks.getParentFile();
        File oldApp = oldFiles == null ? null : oldFiles.getParentFile();
        if (thisApp == null || dataRoot == null || androidRoot == null || oldApp == null
                || !"files".equals(thisFiles.getName()) || !thisPackage.equals(thisApp.getName())
                || !"data".equals(dataRoot.getName()) || !"Android".equals(androidRoot.getName())
                || !"packs".equals(oldPacks.getName()) || !"files".equals(oldFiles.getName())
                || !titleId.equals(recordedPack.getName())
                || !dataRoot.equals(oldApp.getParentFile()) || thisApp.equals(oldApp)
                || !oldApp.getName().startsWith("dev.nxnative.android.")) return null;
        return new File(new File(thisFiles, "packs"), titleId);
    }
}
