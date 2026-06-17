package dev.rono.igniscore.testsupport;

import dev.rono.igniscore.api.event.IgnisEventBus;
import dev.rono.igniscore.api.event.OnBlockActivateListener;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.api.event.OnBlockClickListener;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.event.OnBlockTickListener;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.event.OnItemClickListener;

public final class NoopEventBus implements IgnisEventBus {
    public static final NoopEventBus INSTANCE = new NoopEventBus();

    private NoopEventBus() {
    }

    @Override
    public void subscribe(OnBlockPlaceListener listener) {
    }

    @Override
    public void subscribe(OnBlockClickListener listener) {
    }

    @Override
    public void subscribe(OnBlockInteractListener listener) {
    }

    @Override
    public void subscribe(OnBlockBreakListener listener) {
    }

    @Override
    public void subscribe(OnBlockActivateListener listener) {
    }

    @Override
    public void subscribe(OnBlockTickListener listener) {
    }

    @Override
    public void subscribe(OnBlockTriggerListener listener) {
    }

    @Override
    public void subscribe(OnItemClickListener listener) {
    }

    @Override
    public void subscribe(String extensionId, OnBlockPlaceListener listener) {
    }

    @Override
    public void subscribe(String extensionId, OnBlockClickListener listener) {
    }

    @Override
    public void subscribe(String extensionId, OnBlockInteractListener listener) {
    }

    @Override
    public void subscribe(String extensionId, OnBlockBreakListener listener) {
    }

    @Override
    public void subscribe(String extensionId, OnBlockActivateListener listener) {
    }

    @Override
    public void subscribe(String extensionId, OnBlockTickListener listener) {
    }

    @Override
    public void subscribe(String extensionId, OnBlockTriggerListener listener) {
    }

    @Override
    public void subscribe(String extensionId, OnItemClickListener listener) {
    }

    @Override
    public void unsubscribe(Object listener) {
    }
}
