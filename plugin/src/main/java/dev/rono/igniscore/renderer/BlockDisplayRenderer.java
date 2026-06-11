package dev.rono.igniscore.renderer;

import dev.rono.igniscore.Main;
import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.RuntimeBlockInstance;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;

public class BlockDisplayRenderer {
    private final Main plugin;

    public BlockDisplayRenderer(Main plugin) {
        this.plugin = plugin;
    }

    public void spawnDisplay(RuntimeBlockInstance instance) {
        plugin.debug("Spawning animated display for " + instance.getDefinition().getId());
        instance.getLocation().getWorld().spawn(instance.getLocation().clone().add(0.5, 0, 0.5), ItemDisplay.class, display -> {
            configureDisplay(display, instance.getDefinition());
            
            display.setTransformation(createTransformation(instance.getDefinition(), 0, 1.0f));
            
            instance.setDisplayEntity(display);
        });
    }

    public ItemDisplay spawnStaticDisplay(org.bukkit.Location location, BlockDefinition type) {
        plugin.debug("Spawning static display for " + type.getId() + " at " + location.toVector());
        return location.getWorld().spawn(location.clone().add(0.5, 0, 0.5), ItemDisplay.class, display -> {
            configureDisplay(display, type);
            display.setTransformation(createTransformation(type, -0.35f, 0.2f));
            display.setInterpolationDuration(6);
            display.setInterpolationDelay(0);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (!display.isValid()) return;
                display.setTransformation(createTransformation(type, 0, 1.0f));
            }, 1L);
        });
    }

    private void configureDisplay(ItemDisplay display, BlockDefinition type) {
        Map<String, Object> settings = type.getDisplaySettings();
        display.setItemStack(plugin.createBlockItem(type.getId()));
        display.setBrightness(new org.bukkit.entity.Display.Brightness(
                getInt(settings, "block_light", 15),
                getInt(settings, "sky_light", 15)
        ));
        display.setViewRange((float) getDouble(settings, "view_range", 1.0));
        display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
    }

    private Transformation createTransformation(BlockDefinition type, float bob, float animationScale) {
        Map<String, Object> settings = type.getDisplaySettings();
        double offsetX = getDouble(settings, "offset_x", 0);
        double offsetY = getDouble(settings, "offset_y", 0.5);
        double offsetZ = getDouble(settings, "offset_z", 0);
        float scaleX = (float) (getDouble(settings, "scale_x", getDouble(settings, "scale", 1.01)) * animationScale);
        float scaleY = (float) (getDouble(settings, "scale_y", getDouble(settings, "scale", 1.01)) * animationScale);
        float scaleZ = (float) (getDouble(settings, "scale_z", getDouble(settings, "scale", 1.01)) * animationScale);

        return new Transformation(
            new Vector3f((float) offsetX, (float) offsetY + bob, (float) offsetZ),
            new Quaternionf(),
            new Vector3f(scaleX, scaleY, scaleZ),
            new Quaternionf()
        );
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

        Transformation base = createTransformation(def, bob, scale);
        display.setTransformation(new Transformation(
            base.getTranslation(),
            new Quaternionf().rotateY(rotation),
            base.getScale(),
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

    private int getInt(Map<String, Object> source, String key, int defaultValue) {
        if (source == null) return defaultValue;
        Object value = source.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value != null) {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private double getDouble(Map<String, Object> source, String key, double defaultValue) {
        if (source == null) return defaultValue;
        Object value = source.get(key);
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value != null) {
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}
