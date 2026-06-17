package dev.rono.igniscore.api.event;

@FunctionalInterface
public interface OnBlockTriggerListener {
    void onBlockTrigger(BlockTriggerEvent event);
}
