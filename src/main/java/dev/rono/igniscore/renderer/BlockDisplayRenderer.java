package dev.rono.igniscore.renderer;

import dev.rono.igniscore.Main;
import dev.rono.igniscore.model.TNTInstance;
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

    public void spawnDisplay(TNTInstance instance) {
        instance.getLocation().getWorld().spawn(instance.getLocation().clone().add(0.5, 0, 0.5), ItemDisplay.class, display -> {
            ItemStack item = new ItemStack(Material.TNT);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setCustomModelData(instance.getType().getCustomModelData());
                item.setItemMeta(meta);
            }
            display.setItemStack(item);
            
            // Initial transformation
            display.setTransformation(new Transformation(
                new Vector3f(0, 0.5f, 0),
                new Quaternionf(),
                new Vector3f(1, 1, 1),
                new Quaternionf()
            ));
            
            instance.setDisplayEntity(display);
        });
    }

    public void updateAnimation(TNTInstance instance) {
        if (instance.getDisplayEntity() == null) return;
        ItemDisplay display = (ItemDisplay) instance.getDisplayEntity();
        
        long time = System.currentTimeMillis();
        float bob = (float) Math.sin(time / 200.0) * 0.1f;
        float rotation = (time % 3600L) / 3600.0f * (float) Math.PI * 2;
        
        // Pulse scaling before explosion
        float scale = 1.0f;
        if (instance.getTicksLeft() < 20) {
            scale = 1.0f + (float) Math.sin(time / 50.0) * 0.2f;
        }

        Transformation current = display.getTransformation();
        display.setTransformation(new Transformation(
            new Vector3f(0, 0.5f + bob, 0),
            new Quaternionf().rotateY(rotation),
            new Vector3f(scale, scale, scale),
            new Quaternionf()
        ));
        
        // Use interpolation to make it smooth
        display.setInterpolationDuration(1);
        display.setInterpolationDelay(0);
    }

    public void removeDisplay(TNTInstance instance) {
        if (instance.getDisplayEntity() != null) {
            instance.getDisplayEntity().remove();
        }
    }
}
