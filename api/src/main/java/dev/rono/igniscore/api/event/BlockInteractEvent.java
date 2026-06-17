package dev.rono.igniscore.api.event;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;

/**
 * Fired after a block click resolves to {@link CustomBlockAction#OPEN} or similar custom handling.
 */
public final class BlockInteractEvent {
    private final BlockDefinition definition;
    private final IgnisLocation location;
    private final IgnisPlayer player;
    private final IgnisInteraction interaction;
    private final IgnisItem heldItem;
    private final CustomBlockAction action;

    public BlockInteractEvent(BlockDefinition definition,
                              IgnisLocation location,
                              IgnisPlayer player,
                              IgnisInteraction interaction,
                              IgnisItem heldItem,
                              CustomBlockAction action) {
        this.definition = definition;
        this.location = location;
        this.player = player;
        this.interaction = interaction;
        this.heldItem = heldItem;
        this.action = action;
    }

    public BlockDefinition definition() {
        return definition;
    }

    public IgnisLocation location() {
        return location;
    }

    public IgnisPlayer player() {
        return player;
    }

    public IgnisInteraction interaction() {
        return interaction;
    }

    public IgnisItem heldItem() {
        return heldItem;
    }

    public CustomBlockAction action() {
        return action;
    }
}
