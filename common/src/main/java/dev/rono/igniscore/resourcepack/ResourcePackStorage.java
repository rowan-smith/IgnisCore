package dev.rono.igniscore.resourcepack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public final class ResourcePackStorage {
    private static final String PACK_PREFIX = "resourcepack_";
    private static final String PACK_SUFFIX = ".zip";

    private ResourcePackStorage() {
    }

    public static Set<String> determineRetainedHashes(Path packsDirectory, String activeHash, int retainCount) {
        Set<String> retained = new HashSet<>();
        if (activeHash != null && !activeHash.isBlank()) {
            retained.add(activeHash);
        }

        List<PackFile> packs = listPackFiles(packsDirectory);
        packs.sort((left, right) -> Long.compare(right.lastModifiedMillis(), left.lastModifiedMillis()));
        for (PackFile pack : packs) {
            if (retained.size() >= retainCount) {
                break;
            }
            retained.add(pack.hash());
        }
        return retained;
    }

    public static int deleteUnretainedPacks(Path packsDirectory, Set<String> retainedHashes) throws IOException {
        if (!Files.isDirectory(packsDirectory)) {
            return 0;
        }

        int deleted = 0;
        for (PackFile pack : listPackFiles(packsDirectory)) {
            if (retainedHashes.contains(pack.hash())) {
                continue;
            }
            if (Files.deleteIfExists(pack.path())) {
                deleted++;
            }
        }
        return deleted;
    }

    static List<PackFile> listPackFiles(Path packsDirectory) {
        List<PackFile> packs = new ArrayList<>();
        if (!Files.isDirectory(packsDirectory)) {
            return packs;
        }

        try (Stream<Path> entries = Files.list(packsDirectory)) {
            entries.filter(Files::isRegularFile)
                    .filter(path -> {
                        String name = path.getFileName().toString();
                        return name.startsWith(PACK_PREFIX) && name.endsWith(PACK_SUFFIX);
                    })
                    .forEach(path -> packs.add(new PackFile(path, hashFromFileName(path.getFileName().toString()))));
        } catch (IOException ignored) {
        }
        return packs;
    }

    static String hashFromFileName(String fileName) {
        if (!fileName.startsWith(PACK_PREFIX) || !fileName.endsWith(PACK_SUFFIX)) {
            throw new IllegalArgumentException("Unexpected pack file name: " + fileName);
        }
        return fileName.substring(PACK_PREFIX.length(), fileName.length() - PACK_SUFFIX.length());
    }

    record PackFile(Path path, String hash) {
        long lastModifiedMillis() {
            try {
                return Files.getLastModifiedTime(path).toMillis();
            } catch (IOException error) {
                return 0L;
            }
        }
    }
}
