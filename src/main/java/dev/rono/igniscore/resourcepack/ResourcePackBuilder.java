package dev.rono.igniscore.resourcepack;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.model.BlockDefinition;

import java.io.*;
import java.nio.file.*;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ResourcePackBuilder {
    private final Main plugin;
    private final Gson gson = new Gson();

    public ResourcePackBuilder(Main plugin) {
        this.plugin = plugin;
    }

    public File buildPack(Map<String, BlockDefinition> types) throws IOException {
        Path tempDir = Files.createTempDirectory("igniscore_rp");
        Path assets = tempDir.resolve("assets/igniscore");
        Files.createDirectories(assets.resolve("textures/block"));
        Files.createDirectories(assets.resolve("models/item"));
        Files.createDirectories(tempDir.resolve("assets/minecraft/models/item"));

        // pack.mcmeta
        JsonObject pack = new JsonObject();
        JsonObject meta = new JsonObject();
        meta.addProperty("pack_format", 15); // 1.20
        meta.addProperty("description", "IgnisCore Custom TNT Pack");
        pack.add("pack", meta);
        Files.writeString(tempDir.resolve("pack.mcmeta"), gson.toJson(pack));

        // Overriding TNT item to use CustomModelData
        JsonObject tntOverride = new JsonObject();
        tntOverride.addProperty("parent", "minecraft:block/tnt");
        tntOverride.addProperty("gui_light", "front");
        
        java.util.List<BlockDefinition> sortedTypes = new java.util.ArrayList<>(types.values());
        sortedTypes.sort(java.util.Comparator.comparingInt(BlockDefinition::getCustomModelData));
        
        JsonArray overrides = new JsonArray();
        for (BlockDefinition type : sortedTypes) {
            String id = type.getId();
            // Create custom model for this block
            JsonObject model = createBlockModel(type);
            Files.writeString(assets.resolve("models/item/" + id + ".json"), gson.toJson(model));
            plugin.getLogger().info("[DEBUG] Created custom model: igniscore:item/" + id);
            
            // Add override to minecraft:item/tnt
            JsonObject override = new JsonObject();
            JsonObject predicate = new JsonObject();
            predicate.addProperty("custom_model_data", type.getCustomModelData());
            override.add("predicate", predicate);
            override.addProperty("model", "igniscore:item/" + id);
            overrides.add(override);
            
            // Copy textures from block folder
            copyTextures(type, assets.resolve("textures/block"));
        }
        
        tntOverride.add("overrides", overrides);
        Files.writeString(tempDir.resolve("assets/minecraft/models/item/tnt.json"), gson.toJson(tntOverride));
        plugin.getLogger().info("[DEBUG] Created tnt.json override with " + overrides.size() + " overrides.");

        // Zip it
        File zipFile = new File(plugin.getDataFolder(), "resourcepack.zip");
        if (zipFile.exists()) zipFile.delete();
        zip(tempDir, zipFile.toPath());
        
        // Cleanup temp dir
        deleteDirectory(tempDir.toFile());
        
        return zipFile;
    }

    private JsonObject createBlockModel(BlockDefinition def) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/cube_bottom_top");
        JsonObject textures = new JsonObject();
        String textureBase = "igniscore:block/" + def.getId() + "/";
        textures.addProperty("particle", textureBase + stripExtension(def.getSideTexture()));
        textures.addProperty("top", textureBase + stripExtension(def.getTopTexture()));
        textures.addProperty("bottom", textureBase + stripExtension(def.getBottomTexture()));
        textures.addProperty("side", textureBase + stripExtension(def.getSideTexture()));
        model.add("textures", textures);
        return model;
    }

    private String stripExtension(String fileName) {
        if (fileName.endsWith(".png")) {
            return fileName.substring(0, fileName.length() - 4);
        }
        return fileName;
    }

    private void copyTextures(BlockDefinition def, Path destBase) throws IOException {
        String id = def.getId();
        Path blocksDir = plugin.getDataFolder().toPath().resolve("blocks").resolve(id).resolve("textures");
        Path destDir = destBase.resolve(id);
        Files.createDirectories(destDir);

        if (Files.exists(blocksDir)) {
            try (var stream = Files.walk(blocksDir)) {
                stream.filter(Files::isRegularFile).forEach(path -> {
                    try {
                        Files.copy(path, destDir.resolve(path.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } else {
            // Fallback: try to copy from internal resources if it was a default one
            String[] parts = {def.getTopTexture(), def.getBottomTexture(), def.getSideTexture()};
            for (String part : parts) {
                String fullPart = part.endsWith(".png") ? part : part + ".png";
                String resourcePath = "blocks/" + id + "/textures/" + fullPart;
                InputStream is = plugin.getResource(resourcePath);
                if (is != null) {
                    Files.copy(is, destDir.resolve(fullPart), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    private void zip(Path sourceDirPath, Path zipFilePath) throws IOException {
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
            Files.walk(sourceDirPath)
                .filter(path -> !Files.isDirectory(path))
                .forEach(path -> {
                    ZipEntry zipEntry = new ZipEntry(sourceDirPath.relativize(path).toString().replace("\\", "/"));
                    try {
                        zs.putNextEntry(zipEntry);
                        Files.copy(path, zs);
                        zs.closeEntry();
                    } catch (IOException e) {
                        e.printStackTrace();
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
