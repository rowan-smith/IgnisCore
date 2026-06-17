package dev.rono.igniscore.api.event;

import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisPlayer;

/**
 * Fired when a player uses a custom item.
 *
 * <p>{@link #actionToken()} is the normalized YAML {@code behavior} token when configured,
 * or {@code null} when no behavior action applies to the interaction.</p>
 */
public final class ItemClickEvent {
    private final IgnisPlayer player;
    private final ItemDefinition definition;
    private final IgnisItem item;
    private final IgnisInteraction interaction;
    private final IgnisBlock clickedBlock;
    private final String actionToken;

    public ItemClickEvent(IgnisPlayer player,
                          ItemDefinition definition,
                          IgnisItem item,
                          IgnisInteraction interaction,
                          IgnisBlock clickedBlock,
                          String actionToken) {
        this.player = player;
        this.definition = definition;
        this.item = item;
        this.interaction = interaction;
        this.clickedBlock = clickedBlock;
        this.actionToken = actionToken;
    }

    public IgnisPlayer player() {
        return player;
    }

    public ItemDefinition definition() {
        return definition;
    }

    public IgnisItem item() {
        return item;
    }

    public IgnisInteraction interaction() {
        return interaction;
    }

    public IgnisBlock clickedBlock() {
        return clickedBlock;
    }

    public String actionToken() {
        return actionToken;
    }
}
