package dev.rono.igniscore.core;

import dev.rono.igniscore.model.BlockInstance;
import org.bukkit.entity.Player;

public interface BlockBehaviorStrategy {
    default void onPlace(BlockInstance instance) {}
    default void onTick(BlockInstance instance) {}
    default void onInteract(BlockInstance instance, Player player) {}
    default void onBreak(BlockInstance instance) {}
    default void onTrigger(BlockInstance instance, Object context) {}
}
