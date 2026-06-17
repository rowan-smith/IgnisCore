package dev.rono.igniscore.api.event;

@FunctionalInterface
public interface OnBlockBreakListener {
    void onBlockBreak(BlockBreakEvent event);
}
