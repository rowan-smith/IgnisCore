package dev.rono.igniscore.api.event;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.PlacedBlock;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisPlayer;

/**
 * Fired when a player clicks a placed custom block.
 *
 * <p>Listeners may change {@link #result()} to control core handling (ignite, break, open).</p>
 */
public final class BlockClickEvent implements PlayerBlockEvent {
    private final PlacedBlock block;
    private final IgnisPlayer player;
    private final IgnisInteraction interaction;
    private final IgnisItem heldItem;
    private CustomBlockAction result;

    public BlockClickEvent(PlacedBlock block,
                           IgnisPlayer player,
                           IgnisInteraction interaction,
                           IgnisItem heldItem,
                           CustomBlockAction defaultResult) {
        this.block = block;
        this.player = player;
        this.interaction = interaction;
        this.heldItem = heldItem;
        this.result = defaultResult;
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

    public CustomBlockAction result() {
        return result;
    }

    public void setResult(CustomBlockAction result) {
        this.result = result;
    }
}
