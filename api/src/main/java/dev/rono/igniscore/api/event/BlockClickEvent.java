package dev.rono.igniscore.api.event;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;

/**
 * Fired when a player clicks a placed custom block.
 *
 * <p>Listeners may change {@link #result()} to control core handling (ignite, break, open).</p>
 */
public final class BlockClickEvent implements PlayerBlockEvent {
    private final BlockDefinition definition;
    private final IgnisLocation location;
    private final IgnisPlayer player;
    private final IgnisInteraction interaction;
    private final IgnisItem heldItem;
    private CustomBlockAction result;

    public BlockClickEvent(BlockDefinition definition,
                           IgnisLocation location,
                           IgnisPlayer player,
                           IgnisInteraction interaction,
                           IgnisItem heldItem,
                           CustomBlockAction defaultResult) {
        this.definition = definition;
        this.location = location;
        this.player = player;
        this.interaction = interaction;
        this.heldItem = heldItem;
        this.result = defaultResult;
    }

    @Override
    public BlockDefinition definition() {
        return definition;
    }

    @Override
    public IgnisLocation block() {
        return location;
    }

    /**
     * @deprecated use {@link #block()}
     */
    @Deprecated
    public IgnisLocation location() {
        return location;
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
