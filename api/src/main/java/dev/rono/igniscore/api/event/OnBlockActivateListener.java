package dev.rono.igniscore.api.event;

@FunctionalInterface
public interface OnBlockActivateListener {
    void onBlockActivate(BlockActivateEvent event);
}
