package dev.rono.igniscore;

import dev.rono.igniscore.api.IgnisCoreAPI;
import dev.rono.igniscore.listener.BlockListener;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.resourcepack.ResourcePackBuilder;
import dev.rono.igniscore.resourcepack.ResourcePackServer;
import dev.rono.igniscore.service.NBTService;
import dev.rono.igniscore.service.ProtocolService;
import dev.rono.igniscore.service.RuntimeBlockService;
import dev.rono.igniscore.service.VisualEffectService;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Main extends org.bukkit.plugin.java.JavaPlugin {

    private BlockManager blockManager;
    private ResourcePackServer packServer;
    private String latestHash;

    private NBTService nbtService;
    private ProtocolService protocolService;
    private RuntimeBlockService runtimeBlockService;
    private VisualEffectService visualEffectService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Initialize Services
        this.nbtService = new NBTService();
        this.protocolService = new ProtocolService(this);
        this.runtimeBlockService = new RuntimeBlockService();
        this.visualEffectService = new VisualEffectService(protocolService);
        
        blockManager = new BlockManager(this);
        ResourcePackBuilder packBuilder = new ResourcePackBuilder(this);
        packServer = new ResourcePackServer(this);
        
        IgnisCoreAPI.init(this);
        
        getServer().getPluginManager().registerEvents(new BlockListener(this, blockManager), this);
        
        Objects.requireNonNull(getCommand("ignis")).setExecutor(new IgnisCommand());
        Objects.requireNonNull(getCommand("ignis")).setTabCompleter(new IgnisCommand());

        // Build resource pack
        try {
            ResourcePackBuilder.PackResult result = packBuilder.buildPack(blockManager.getBlockTypes());
            this.latestHash = result.getHash();
            packServer.registerPack(latestHash, result.getFile());
            getLogger().info("Resource pack generated successfully! Hash: " + latestHash);
            
            // Start server
            String host = getConfig().getString("resource-pack.host", "0.0.0.0");
            int port = getConfig().getInt("resource-pack.port", 8080);
            packServer.start(host, port);
        } catch (IOException e) {
            getLogger().severe("Failed to generate resource pack: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        if (blockManager != null) {
            blockManager.cleanup();
        }
        if (packServer != null) {
            packServer.stop();
        }
    }

    private class IgnisCommand implements CommandExecutor, TabCompleter {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            if (!sender.hasPermission("igniscore.admin")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission to use this command."));
                return true;
            }

            if (args.length == 0) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<gold>IgnisCore Commands:"));
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>/ignis give <player> <type>"));
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>/ignis pack - Apply resource pack"));
                return true;
            }

            if (args[0].equalsIgnoreCase("give") && args.length >= 3) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Player not found."));
                    return true;
                }
                String typeId = args[2];
                if (!blockManager.getBlockTypes().containsKey(typeId)) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Unknown block type."));
                    return true;
                }
                
                target.getInventory().addItem(createBlockItem(typeId));
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Gave " + typeId + " block to " + target.getName()));
                return true;
            }

            if (args[0].equalsIgnoreCase("pack")) {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Only players can use this."));
                    return true;
                }
                String url = getConfig().getString("resource-pack.public-url");
                if (url != null && !url.contains("your-server-ip")) {
                    if (latestHash != null) {
                        String versionedUrl = url.replace("resourcepack.zip", "resourcepack_" + latestHash + ".zip");
                        player.setResourcePack(versionedUrl, hexToBytes(latestHash), (net.kyori.adventure.text.Component) null, false);
                    } else {
                        player.setResourcePack(url, (byte[]) null, (net.kyori.adventure.text.Component) null, false);
                    }
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Resource pack requested."));
                } else {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Resource pack URL not configured in config.yml"));
                }
                return true;
            }

            return false;
        }

        private byte[] hexToBytes(String s) {
            int len = s.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                     + Character.digit(s.charAt(i+1), 16));
            }
            return data;
        }

        @Override
        public java.util.List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
            java.util.List<String> completions = new java.util.ArrayList<>();
            
            if (!sender.hasPermission("igniscore.admin")) {
                return completions;
            }

            if (args.length == 1) {
                completions.add("give");
                completions.add("pack");
            } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
                completions.addAll(blockManager.getBlockTypes().keySet());
            }

            String lastArg = args[args.length - 1].toLowerCase();
            return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(lastArg))
                .collect(Collectors.toList());
        }
    }

    public ItemStack createBlockItem(String typeId) {
        BlockDefinition type = blockManager.getBlockTypes().get(typeId);
        Material material = Material.matchMaterial(type.getBaseMaterial());
        if (material == null) material = Material.TNT;
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(type.getTitle());
            meta.lore(type.getDescription());
            meta.setCustomModelData(type.getCustomModelData());
            item.setItemMeta(meta);
        }

        // Use NBTService for structured metadata
        nbtService.editItem(item, nbt -> {
            nbt.setString("ignis:block_id", typeId);
            nbt.setString("ignis:strategy", type.getStrategy());
            nbt.setInteger("ignis:version", 1);
            nbt.setInteger("ignis:fuse", type.getFuse());
            // Future extensible metadata can be added here
        });

        return item;
    }

    public BlockManager getBlockManager() { return blockManager; }
    public NBTService getNbtService() { return nbtService; }
    public ProtocolService getProtocolService() { return protocolService; }
    public RuntimeBlockService getRuntimeBlockService() { return runtimeBlockService; }
    public VisualEffectService getVisualEffectService() { return visualEffectService; }
}
