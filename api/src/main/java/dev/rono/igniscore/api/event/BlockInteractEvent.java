package dev.rono.igniscore.api.event;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.PlacedBlock;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisPlayer;

/**
 * Fired after a block click resolves to {@link CustomBlockAction#OPEN} or similar custom handling.
 */
public final class BlockInteractEvent implements PlayerBlockEvent {
    private final PlacedBlock block;
    private final IgnisPlayer player;
    private final IgnisInteraction interaction;
    private final IgnisItem heldItem;
    private final CustomBlockAction action;

    public BlockInteractEvent(PlacedBlock block,
                              IgnisPlayer player,
                              IgnisInteraction interaction,
                              IgnisItem heldItem,
                              CustomBlockAction action) {
        this.block = block;
        this.player = player;
        this.interaction = interaction;
        this.heldItem = heldItem;
        this.action = action;
    }

    @Override
    public PlacedBlock block() {
        return block;
    }

    @Override
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
