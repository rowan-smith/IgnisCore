package dev.rono.igniscore.strategies;

import dev.rono.igniscore.model.RuntimeBlockInstance;
import org.bukkit.entity.Player;

public interface BlockBehaviorStrategy {
    default void onPlace(RuntimeBlockInstance instance) {}
    default void onTick(RuntimeBlockInstance instance) {}
    default void onInteract(RuntimeBlockInstance instance, Player player) {}
    default void onBreak(RuntimeBlockInstance instance) {}
    default void onTrigger(RuntimeBlockInstance instance, Object context) {}
}
