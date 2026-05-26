package dev.rono.igniscore.resourcepack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.model.BlockDefinition;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ResourcePackBuilder {
    private final Main plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static class PackResult {
        private final File file;
        private final String hash;

        public PackResult(File file, String hash) {
            this.file = file;
            this.hash = hash;
        }

        public File getFile() { return file; }
        public String getHash() { return hash; }
    }

    public ResourcePackBuilder(Main plugin) {
        this.plugin = plugin;
    }

    public PackResult buildPack(Map<String, BlockDefinition> definitions) throws IOException {
        // Phase 1: Compilation
        List<CompiledBlockAsset> compiledAssets = compile(definitions);

        // Phase 2: Packaging
        return packageAssets(compiledAssets);
    }

    private List<CompiledBlockAsset> compile(Map<String, BlockDefinition> definitions) {
        List<CompiledBlockAsset> assets = new ArrayList<>();
        for (BlockDefinition def : definitions.values()) {
            try {
                assets.add(compileAsset(def));
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to compile asset for block " + def.getId() + ": " + e.getMessage());
            }
        }
        return assets;
    }

    private CompiledBlockAsset compileAsset(BlockDefinition def) {
        Map<String, String> textures = new LinkedHashMap<>();
        textures.put("top", def.getTopTexture());
        textures.put("side", def.getSideTexture());
        textures.put("bottom", def.getBottomTexture());

        // Block Model: assets/igniscore/models/block/<id>.json
        JsonObject blockModel = new JsonObject();
        blockModel.addProperty("parent", "minecraft:block/cube_bottom_top");
        JsonObject texturesJson = new JsonObject();
        String textureBase = "igniscore:block/" + def.getId() + "/";
        
        // Use deterministic texture names (top.png, side.png, bottom.png) inside the pack
        texturesJson.addProperty("particle", textureBase + "side");
        texturesJson.addProperty("top", textureBase + "top");
        texturesJson.addProperty("bottom", textureBase + "bottom");
        texturesJson.addProperty("side", textureBase + "side");
        blockModel.add("textures", texturesJson);

        // Item Model: assets/igniscore/models/item/<id>.json
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", "igniscore:block/" + def.getId());

        return new CompiledBlockAsset(
                def.getId(),
                def.getBaseMaterial(),
                def.getCustomModelData(),
                textures,
                blockModel,
                itemModel
        );
    }

    private PackResult packageAssets(List<CompiledBlockAsset> assets) throws IOException {
        Path tempDir = Files.createTempDirectory("igniscore_rp");
        try {
            // pack.mcmeta
            writePackMeta(tempDir);

            // Group by base material for item overrides
            Map<String, List<CompiledBlockAsset>> byBaseMaterial = new HashMap<>();
            
            for (CompiledBlockAsset asset : assets) {
                byBaseMaterial.computeIfAbsent(asset.getBaseMaterial(), k -> new ArrayList<>()).add(asset);
                
                // Write block model: assets/igniscore/models/block/<id>.json
                Path blockModelPath = tempDir.resolve("assets/igniscore/models/block/" + asset.getId() + ".json");
                Files.createDirectories(blockModelPath.getParent());
                Files.writeString(blockModelPath, gson.toJson(asset.getBlockModel()));

                // Write item model: assets/igniscore/models/item/<id>.json
                Path itemModelPath = tempDir.resolve("assets/igniscore/models/item/" + asset.getId() + ".json");
                Files.createDirectories(itemModelPath.getParent());
                Files.writeString(itemModelPath, gson.toJson(asset.getItemModel()));

                // Write textures: assets/igniscore/textures/block/<id>/
                Path textureDir = tempDir.resolve("assets/igniscore/textures/block/" + asset.getId());
                Files.createDirectories(textureDir);
                copyTextures(asset, textureDir);
            }

            // Write item overrides: assets/minecraft/models/item/<base_item>.json
            for (Map.Entry<String, List<CompiledBlockAsset>> entry : byBaseMaterial.entrySet()) {
                String baseItem = entry.getKey();
                List<CompiledBlockAsset> materialAssets = entry.getValue();
                materialAssets.sort(Comparator.comparingInt(CompiledBlockAsset::getCustomModelData));
                
                writeItemOverride(tempDir, baseItem, materialAssets);
            }

            // Zip it
            Path tempZip = Files.createTempFile("rp_temp", ".zip");
            zip(tempDir, tempZip);
            
            String hash;
            try {
                hash = calculateHash(tempZip.toFile());
            } catch (NoSuchAlgorithmException e) {
                throw new IOException("Hash algorithm not found", e);
            }
            
            File packsDir = new File(plugin.getDataFolder(), "packs");
            if (!packsDir.exists()) packsDir.mkdirs();
            
            File finalZip = new File(packsDir, "resourcepack_" + hash + ".zip");
            Files.move(tempZip, finalZip.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            return new PackResult(finalZip, hash);
        } finally {
            deleteDirectory(tempDir.toFile());
        }
    }

    private String calculateHash(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        try (InputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
        }
        byte[] hashBytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private void writePackMeta(Path tempDir) throws IOException {
        JsonObject pack = new JsonObject();
        JsonObject meta = new JsonObject();
        meta.addProperty("pack_format", 15);
        meta.addProperty("description", "IgnisCore Custom Blocks Pack");
        pack.add("pack", meta);
        Files.writeString(tempDir.resolve("pack.mcmeta"), gson.toJson(pack));
    }

    private void writeItemOverride(Path tempDir, String baseItem, List<CompiledBlockAsset> assets) throws IOException {
        Path overridePath = tempDir.resolve("assets/minecraft/models/item/" + baseItem + ".json");
        Files.createDirectories(overridePath.getParent());

        JsonObject root = new JsonObject();
        // Use correct item model parent
        root.addProperty("parent", "minecraft:item/generated");
        root.addProperty("gui_light", "front");

        JsonArray overrides = new JsonArray();
        for (CompiledBlockAsset asset : assets) {
            JsonObject override = new JsonObject();
            JsonObject predicate = new JsonObject();
            predicate.addProperty("custom_model_data", asset.getCustomModelData());
            override.add("predicate", predicate);
            override.addProperty("model", "igniscore:item/" + asset.getId());
            overrides.add(override);
        }
        root.add("overrides", overrides);

        Files.writeString(overridePath, gson.toJson(root));
        plugin.getLogger().info("[DEBUG] Created " + baseItem + ".json override with " + overrides.size() + " overrides.");
    }

    private void copyTextures(CompiledBlockAsset asset, Path destDir) throws IOException {
        for (Map.Entry<String, String> entry : asset.getTextures().entrySet()) {
            String key = entry.getKey();
            String fileName = entry.getValue();
            if (fileName == null || fileName.isEmpty()) continue;
            
            try (InputStream is = getTextureStream(asset.getId(), fileName)) {
                if (is != null) {
                    Files.copy(is, destDir.resolve(key + ".png"), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    plugin.getLogger().warning("Texture missing for block " + asset.getId() + ": " + fileName);
                }
            }
        }
    }

    private InputStream getTextureStream(String blockId, String fileName) throws IOException {
        File dataFolder = plugin.getDataFolder();
        File sourceFile = new File(dataFolder, "blocks/" + blockId + "/textures/" + fileName);
        if (sourceFile.exists()) {
            return new FileInputStream(sourceFile);
        }
        return plugin.getResource("blocks/" + blockId + "/textures/" + fileName);
    }

    private void zip(Path sourceDirPath, Path zipFilePath) throws IOException {
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipFilePath));
             java.util.stream.Stream<Path> paths = Files.walk(sourceDirPath)) {
            paths.filter(path -> !Files.isDirectory(path))
                .forEach(path -> {
                    ZipEntry zipEntry = new ZipEntry(sourceDirPath.relativize(path).toString().replace("\\", "/"));
                    try {
                        zs.putNextEntry(zipEntry);
                        Files.copy(path, zs);
                        zs.closeEntry();
                    } catch (IOException e) {
                        plugin.getLogger().severe("Failed to add entry to zip: " + zipEntry.getName());
                    }
                });
        }
    }

    private void deleteDirectory(File directory) {
        File[] allContents = directory.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directory.delete();
    }
}
