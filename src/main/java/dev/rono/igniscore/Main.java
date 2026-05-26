package dev.rono.igniscore;

import dev.rono.igniscore.api.IgnisCoreAPI;
import dev.rono.igniscore.listener.TNTListener;
import dev.rono.igniscore.manager.TNTManager;
import dev.rono.igniscore.model.TNTDefinition;
import dev.rono.igniscore.resourcepack.ResourcePackBuilder;
import dev.rono.igniscore.resourcepack.ResourcePackServer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Main extends org.bukkit.plugin.java.JavaPlugin {

    private TNTManager tntManager;
    private ResourcePackBuilder packBuilder;
    private ResourcePackServer packServer;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        tntManager = new TNTManager(this);
        packBuilder = new ResourcePackBuilder(this);
        packServer = new ResourcePackServer(this);
        
        IgnisCoreAPI.init(tntManager);
        
        getServer().getPluginManager().registerEvents(new TNTListener(this, tntManager), this);
        
        Objects.requireNonNull(getCommand("ignis")).setExecutor(new IgnisCommand());
        Objects.requireNonNull(getCommand("ignis")).setTabCompleter(new IgnisCommand());

        // Build resource pack
        try {
            packBuilder.buildPack(tntManager.getTntTypes());
            getLogger().info("Resource pack generated successfully!");
            
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
        if (tntManager != null) {
            tntManager.cleanup();
        }
        if (packServer != null) {
            packServer.stop();
        }
    }

    private class IgnisCommand implements CommandExecutor, TabCompleter {
        @Override
        public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, String[] args) {
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
                if (!tntManager.getTntTypes().containsKey(typeId)) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Unknown TNT type."));
                    return true;
                }
                
                target.getInventory().addItem(createTNTItem(typeId));
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Gave " + typeId + " TNT to " + target.getName()));
                return true;
            }

            if (args[0].equalsIgnoreCase("pack")) {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Only players can use this."));
                    return true;
                }
                String url = getConfig().getString("resource-pack.public-url");
                if (url != null && !url.contains("your-server-ip")) {
                    player.setResourcePack(url);
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Resource pack requested."));
                } else {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Resource pack URL not configured in config.yml"));
                }
                return true;
            }

            return false;
        }

        @Override
        public java.util.List<String> onTabComplete(CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
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
                completions.addAll(tntManager.getTntTypes().keySet());
            }

            String lastArg = args[args.length - 1].toLowerCase();
            return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(lastArg))
                .collect(Collectors.toList());
        }
    }

    public ItemStack createTNTItem(String typeId) {
        TNTDefinition type = tntManager.getTntTypes().get(typeId);
        ItemStack item = new ItemStack(Material.TNT);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(type.getTitle());
            meta.lore(type.getDescription());
            meta.getPersistentDataContainer().set(new NamespacedKey(this, "tnt_type"), PersistentDataType.STRING, typeId);
            meta.setCustomModelData(type.getCustomModelData());
            item.setItemMeta(meta);
        }
        return item;
    }
}
