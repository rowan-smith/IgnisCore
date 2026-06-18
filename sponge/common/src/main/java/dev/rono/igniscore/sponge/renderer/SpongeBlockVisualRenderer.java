package dev.rono.igniscore.sponge.renderer;

import com.google.inject.Inject;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.BlockVisualRenderer;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.sponge.SpongePluginContext;
import dev.rono.igniscore.sponge.adapter.SpongeBridge;
import dev.rono.igniscore.sponge.service.SpongeBlockItemFactory;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.display.ItemDisplay;
import org.spongepowered.api.entity.display.ItemDisplayTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.api.util.Transform;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

import java.util.Map;

public class SpongeBlockVisualRenderer implements BlockVisualRenderer {
    private final SpongePluginContext pluginContext;
    private final SpongeBlockItemFactory itemFactory;
    private final PlatformAdapter platformAdapter;

    @Inject
    public SpongeBlockVisualRenderer(SpongePluginContext pluginContext,
                                     SpongeBlockItemFactory itemFactory,
                                     PlatformAdapter platformAdapter) {
        this.pluginContext = pluginContext;
        this.itemFactory = itemFactory;
        this.platformAdapter = platformAdapter;
    }

    @Override
    public void spawnAnimatedDisplay(RuntimeBlockInstance instance) {
        pluginContext.debug("Spawning animated display for " + instance.getDefinition().getId());
        ServerLocation spawnLocation = resolveLocation(instance.getLocation()).add(0.5, 0, 0.5);
        Entity entity = spawnLocation.createEntity(EntityTypes.ITEM_DISPLAY.get());
        if (!(entity instanceof ItemDisplay display)) {
            return;
        }
        configureDisplay(display, instance.getDefinition());
        display.offer(Keys.TRANSFORM, createTransformation(instance.getDefinition(), 0, 1.0f));
        spawnLocation.spawnEntity(display);
        instance.setDisplayEntity(display);
    }

    @Override
    public Object spawnStaticDisplay(IgnisLocation location, BlockDefinition type) {
        ServerLocation spawnLocation = resolveLocation(location).add(0.5, 0, 0.5);
        pluginContext.debug("Spawning static display for " + type.getId() + " at " + spawnLocation.blockPosition());
        Entity entity = spawnLocation.createEntity(EntityTypes.ITEM_DISPLAY.get());
        if (!(entity instanceof ItemDisplay display)) {
            return null;
        }
        configureDisplay(display, type);
        display.offer(Keys.TRANSFORM, createTransformation(type, -0.35f, 0.2f));
        display.offer(Keys.INTERPOLATION_DURATION, Ticks.of(6));
        display.offer(Keys.INTERPOLATION_DELAY, Ticks.of(0));
        spawnLocation.spawnEntity(display);
        platformAdapter.getScheduler().runGlobalLater(
                () -> {
                    if (!display.isRemoved()) {
                        display.offer(Keys.TRANSFORM, createTransformation(type, 0, 1.0f));
                    }
                },
                1L);
        return display;
    }

    @Override
    public void updateAnimation(RuntimeBlockInstance instance) {
        if (!(instance.getDisplayEntity() instanceof ItemDisplay display)) {
            return;
        }

        BlockDefinition definition = instance.getDefinition();
        long time = System.currentTimeMillis();
        float bob = definition.isFloatBob() ? (float) Math.sin(time / 200.0) * 0.1f : 0;

        float scale = 1.0f;
        if (definition.isPulse() && instance.getTicksLeft() < 20) {
            scale = 1.0f + (float) Math.sin(time / 50.0) * 0.2f;
        }

        display.offer(Keys.TRANSFORM, createTransformation(definition, bob, scale));
        display.offer(Keys.INTERPOLATION_DURATION, Ticks.of(1));
        display.offer(Keys.INTERPOLATION_DELAY, Ticks.of(0));
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
        ItemStack itemStack = itemFactory.createBlockItem(type.getId());
        display.item().set(itemStack.createSnapshot());
        display.offer(Keys.BLOCK_LIGHT, getInt(settings, "block_light", 15));
        display.offer(Keys.SKY_LIGHT, getInt(settings, "sky_light", 15));
        display.offer(Keys.VIEW_RANGE, getDouble(settings, "view_range", 1.0));
        display.offer(Keys.ITEM_DISPLAY_TYPE, ItemDisplayTypes.FIXED.get());
    }

    private Transform createTransformation(BlockDefinition type, float bob, float animationScale) {
        Map<String, Object> settings = type.getDisplaySettings();
        double offsetX = getDouble(settings, "offset_x", 0);
        double offsetY = getDouble(settings, "offset_y", 0.5);
        double offsetZ = getDouble(settings, "offset_z", 0);
        float scaleX = (float) (getDouble(settings, "scale_x", getDouble(settings, "scale", 1.01)) * animationScale);
        float scaleY = (float) (getDouble(settings, "scale_y", getDouble(settings, "scale", 1.01)) * animationScale);
        float scaleZ = (float) (getDouble(settings, "scale_z", getDouble(settings, "scale", 1.01)) * animationScale);

        return Transform.of(
                Vector3d.from(offsetX, offsetY + bob, offsetZ),
                Vector3d.ZERO,
                Vector3d.from(scaleX, scaleY, scaleZ));
    }

    private ServerLocation resolveLocation(IgnisLocation location) {
        var world = platformAdapter.resolveWorld(location);
        if (world instanceof dev.rono.igniscore.sponge.adapter.SpongeIgnisWorld spongeWorld) {
            return SpongeBridge.toSponge(location, spongeWorld.getHandle());
        }
        throw new IllegalStateException("Unable to resolve world for display spawn");
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
