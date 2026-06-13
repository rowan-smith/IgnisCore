package dev.rono.igniscore.folia.support;

public final class FoliaSupport {
    private static final String FOLIA_MARKER = "io.papermc.paper.threadedregions.RegionizedServer";
    private static final Boolean FOLIA = detect();

    private FoliaSupport() {
    }

    public static boolean isFolia() {
        return FOLIA;
    }

    private static boolean detect() {
        try {
            Class.forName(FOLIA_MARKER);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }
}
