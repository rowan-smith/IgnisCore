package dev.rono.igniscore.renderer;

import dev.rono.igniscore.Main;
import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.RuntimeBlockInstance;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BlockDisplayRenderer {
    private final Main plugin;

    public BlockDisplayRenderer(Main plugin) {
        this.plugin = plugin;
    }

    public void spawnDisplay(RuntimeBlockInstance instance) {
        plugin.getLogger().info("[DEBUG] Spawning animated display for " + instance.getDefinition().getId());
        instance.getLocation().getWorld().spawn(instance.getLocation().clone().add(0.5, 0, 0.5), ItemDisplay.class, display -> {
            Material material = Material.matchMaterial(instance.getDefinition().getBaseMaterial());
            if (material == null) material = Material.TNT;
            
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setCustomModelData(instance.getDefinition().getCustomModelData());
                item.setItemMeta(meta);
            }
            display.setItemStack(item);
            display.setBrightness(new org.bukkit.entity.Display.Brightness(15, 15));
            display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
            
            // Initial transformation
            display.setTransformation(new Transformation(
                new Vector3f(0, 0.5f, 0),
                new Quaternionf(),
                new Vector3f(1.01f, 1.01f, 1.01f),
                new Quaternionf()
            ));
            
            instance.setDisplayEntity(display);
        });
    }

    public ItemDisplay spawnStaticDisplay(org.bukkit.Location location, BlockDefinition type) {
        plugin.getLogger().info("[DEBUG] Spawning static display for " + type.getId() + " at " + location.toVector());
        return location.getWorld().spawn(location.clone().add(0.5, 0, 0.5), ItemDisplay.class, display -> {
            Material material = Material.matchMaterial(type.getBaseMaterial());
            if (material == null) material = Material.TNT;
            
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setCustomModelData(type.getCustomModelData());
                item.setItemMeta(meta);
            }
            display.setItemStack(item);
            display.setBrightness(new org.bukkit.entity.Display.Brightness(15, 15));
            display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
            display.setTransformation(new org.bukkit.util.Transformation(
                new org.joml.Vector3f(0, 0.5f, 0),
                new org.joml.Quaternionf(),
                new org.joml.Vector3f(1.01f, 1.01f, 1.01f),
                new org.joml.Quaternionf()
            ));
        });
    }

    public void updateAnimation(RuntimeBlockInstance instance) {
        if (instance.getDisplayEntity() == null) return;
        ItemDisplay display = (ItemDisplay) instance.getDisplayEntity();
        BlockDefinition def = instance.getDefinition();
        
        long time = System.currentTimeMillis();
        float bob = def.isFloatBob() ? (float) Math.sin(time / 200.0) * 0.1f : 0;
        float rotation = def.isRotate() ? (time % 3600L) / 3600.0f * (float) Math.PI * 2 : 0;
        
        // Pulse scaling
        float scale = 1.0f;
        if (def.isPulse() && instance.getTicksLeft() < 20) {
            scale = 1.0f + (float) Math.sin(time / 50.0) * 0.2f;
        }

        display.setTransformation(new Transformation(
            new Vector3f(0, 0.5f + bob, 0),
            new Quaternionf().rotateY(rotation),
            new Vector3f(1.01f * scale, 1.01f * scale, 1.01f * scale),
            new Quaternionf()
        ));
        
        // Use interpolation to make it smooth
        display.setInterpolationDuration(1);
        display.setInterpolationDelay(0);
    }

    public void removeDisplay(RuntimeBlockInstance instance) {
        if (instance.getDisplayEntity() != null) {
            instance.getDisplayEntity().remove();
        }
    }
}
