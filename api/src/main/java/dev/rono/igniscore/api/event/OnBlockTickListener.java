package dev.rono.igniscore.api.event;

@FunctionalInterface
public interface OnBlockTickListener {
    void onBlockTick(BlockTickEvent event);
}
