package dev.rono.igniscore.manager;

import dev.rono.igniscore.Main;
import dev.rono.igniscore.model.TNTDefinition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TNTDefinitionLoader {
    private final Main plugin;

    public TNTDefinitionLoader(Main plugin) {
        this.plugin = plugin;
    }

    public Map<String, TNTDefinition> loadDefinitions() {
        Map<String, TNTDefinition> definitions = new HashMap<>();
        File blocksFolder = new File(plugin.getDataFolder(), "blocks");
        
        if (!blocksFolder.exists()) {
            blocksFolder.mkdirs();
            // Copy default blocks from resources if they don't exist
            saveDefaultBlock("nuke", true);
            saveDefaultBlock("spider-storm", false);
        }

        File[] folders = blocksFolder.listFiles(File::isDirectory);
        if (folders != null) {
            int modelData = 1;
            for (File folder : folders) {
                TNTDefinition def = loadFromFolder(folder, modelData++);
                if (def != null) {
                    definitions.put(def.getId(), def);
                }
            }
        }
        
        return definitions;
    }

    private void saveDefaultBlock(String id, boolean useUnderscore) {
        String separator = useUnderscore ? "_" : "-";
        saveFile("blocks/" + id + "/config.yml");
        saveFile("blocks/" + id + "/textures/" + id + separator + "top.png");
        saveFile("blocks/" + id + "/textures/" + id + separator + "side.png");
        saveFile("blocks/" + id + "/textures/" + id + separator + "bottom.png");
    }

    private void saveFile(String resourcePath) {
        File outFile = new File(plugin.getDataFolder(), resourcePath);
        if (outFile.exists()) return;
        
        outFile.getParentFile().mkdirs();
        
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in == null) return;
            try (FileOutputStream out = new FileOutputStream(outFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Could not save " + resourcePath + " to " + outFile.getName());
        }
    }

    private TNTDefinition loadFromFolder(File folder, int modelData) {
        File configFile = new File(folder, "config.yml");
        if (!configFile.exists()) return null;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        String id = config.getString("id", folder.getName());
        
        String titleStr = config.getString("display.title", id);
        Component title = LegacyComponentSerializer.legacyAmpersand().deserialize(titleStr);
        
        List<String> descStrings = config.getStringList("display.description");
        List<Component> description = new ArrayList<>();
        for (String s : descStrings) {
            description.add(LegacyComponentSerializer.legacyAmpersand().deserialize(s));
        }
        
        String top = config.getString("textures.top", id + "-top.png");
        String side = config.getString("textures.side", id + "-side.png");
        String bottom = config.getString("textures.bottom", id + "-bottom.png");
        
        String explosionStrategy = config.getString("explosion.strategy", "default");
        int fuse = config.getInt("explosion.fuse", 80);
        double radius = config.getDouble("explosion.radius", 4.0);
        double power = config.getDouble("explosion.power", 4.0);
        double multiplier = config.getDouble("explosion.multiplier", 1.0);
        
        boolean fire = config.getBoolean("explosion.effects.fire", false);
        boolean blockDamage = config.getBoolean("explosion.effects.destroy_blocks", true);
        boolean screenShake = config.getBoolean("explosion.effects.screen_shake", false);
        
        String entityPayloadType = config.getString("explosion.entity_payload.type");
        int entityPayloadCount = config.getInt("explosion.entity_payload.count", 0);
        String entityPayloadBehavior = config.getString("explosion.entity_payload.behavior", "normal");
        boolean entityPayloadTargetPlayers = config.getBoolean("explosion.entity_payload.target_players", false);

        return new TNTDefinition(id, title, description, fuse, power, radius, multiplier, 
                                 fire, blockDamage, screenShake, top, side, bottom, 
                                 explosionStrategy, modelData, entityPayloadType, entityPayloadCount,
                                 entityPayloadBehavior, entityPayloadTargetPlayers);
    }
}
