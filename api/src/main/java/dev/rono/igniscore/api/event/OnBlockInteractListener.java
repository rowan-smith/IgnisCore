package dev.rono.igniscore.api.event;

@FunctionalInterface
public interface OnBlockInteractListener {
    void onBlockInteract(BlockInteractEvent event);
}
