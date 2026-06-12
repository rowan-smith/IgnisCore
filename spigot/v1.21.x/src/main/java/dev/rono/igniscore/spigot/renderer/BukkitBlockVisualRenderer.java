package dev.rono.igniscore.spigot.renderer;

import com.google.inject.Inject;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.BlockVisualRenderer;
import dev.rono.igniscore.api.port.IgnisCustomItemFactory;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.common.runtime.IgnisRuntimeHost;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;

public class BukkitBlockVisualRenderer implements BlockVisualRenderer {
    private final IgnisRuntimeHost host;
    private final IgnisCustomItemFactory itemFactory;
    private final JavaPlugin plugin;

    @Inject
    public BukkitBlockVisualRenderer(IgnisRuntimeHost host,
                                     IgnisCustomItemFactory itemFactory,
                                     JavaPlugin plugin) {
        this.host = host;
        this.itemFactory = itemFactory;
        this.plugin = plugin;
    }

    @Override
    public void spawnAnimatedDisplay(RuntimeBlockInstance instance) {
        host.debug("Spawning animated display for " + instance.getDefinition().getId());
        Location spawnLocation = BukkitBridge.toBukkit(instance.getLocation()).clone().add(0.5, 0, 0.5);
        spawnLocation.getWorld().spawn(spawnLocation, ItemDisplay.class, display -> {
            configureDisplay(display, instance.getDefinition());
            display.setTransformation(createTransformation(instance.getDefinition(), 0, 1.0f));
            instance.setDisplayEntity(display);
        });
    }

    @Override
    public Object spawnStaticDisplay(IgnisLocation location, BlockDefinition type) {
        Location bukkitLocation = BukkitBridge.toBukkit(location);
        host.debug("Spawning static display for " + type.getId() + " at " + bukkitLocation.toVector());
        return bukkitLocation.getWorld().spawn(bukkitLocation.clone().add(0.5, 0, 0.5), ItemDisplay.class, display -> {
            configureDisplay(display, type);
            display.setTransformation(createTransformation(type, -0.35f, 0.2f));
            display.setInterpolationDuration(6);
            display.setInterpolationDelay(0);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        if (!display.isValid()) {
                            return;
                        }
                        display.setTransformation(createTransformation(type, 0, 1.0f));
                    }, 1L);
        });
    }

    @Override
    public void updateAnimation(RuntimeBlockInstance instance) {
        if (!(instance.getDisplayEntity() instanceof ItemDisplay display)) {
            return;
        }

        BlockDefinition def = instance.getDefinition();
        long time = System.currentTimeMillis();
        float bob = def.isFloatBob() ? (float) Math.sin(time / 200.0) * 0.1f : 0;
        float rotation = def.isRotate() ? (time % 3600L) / 3600.0f * (float) Math.PI * 2 : 0;

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
        display.setInterpolationDuration(1);
        display.setInterpolationDelay(0);
    }

    @Override
    public void removeDisplay(RuntimeBlockInstance instance) {
        if (instance.getDisplayEntity() instanceof Entity entity) {
            entity.remove();
        }
    }

    @Override
    public void removeStaticDisplay(Object nativeDisplay) {
        if (nativeDisplay instanceof Entity entity) {
            entity.remove();
        }
    }

    private void configureDisplay(ItemDisplay display, BlockDefinition type) {
        Map<String, Object> settings = type.getDisplaySettings();
        ItemStack itemStack = BukkitBridge.unwrap(itemFactory.createBlockItem(type.getId()));
        display.setItemStack(itemStack);
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

    private int getInt(Map<String, Object> source, String key, int defaultValue) {
        if (source == null) {
            return defaultValue;
        }
        Object value = source.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        return defaultValue;
    }

    private double getDouble(Map<String, Object> source, String key, double defaultValue) {
        if (source == null) {
            return defaultValue;
        }
        Object value = source.get(key);
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return defaultValue;
    }
}
