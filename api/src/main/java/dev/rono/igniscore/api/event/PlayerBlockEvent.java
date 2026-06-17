package dev.rono.igniscore.api.event;

import dev.rono.igniscore.api.port.IgnisPlayer;

/**
 * Block events that include the acting player.
 */
public interface PlayerBlockEvent extends BlockEvent {

    IgnisPlayer player();
}
