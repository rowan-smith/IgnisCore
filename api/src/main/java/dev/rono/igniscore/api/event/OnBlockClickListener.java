package dev.rono.igniscore.api.event;

@FunctionalInterface
public interface OnBlockClickListener {
    void onBlockClick(BlockClickEvent event);
}
