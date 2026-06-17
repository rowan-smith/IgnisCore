package dev.rono.igniscore.api.event;

@FunctionalInterface
public interface OnBlockPlaceListener {
    void onBlockPlace(BlockPlaceEvent event);
}
