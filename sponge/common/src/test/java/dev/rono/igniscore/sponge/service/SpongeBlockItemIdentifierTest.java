package dev.rono.igniscore.sponge.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class SpongeBlockItemIdentifierTest {

    @Test
    void returnsNullForNullItem() {
        SpongeBlockItemIdentifier identifier = new SpongeBlockItemIdentifier(new NoopNbtService());
        assertNull(identifier.resolveTypeId(null));
    }

    private static final class NoopNbtService implements dev.rono.igniscore.api.service.IgnisNbtService {
        @Override public boolean isEnabled() { return true; }
        @Override public String providerName() { return "test"; }
        @Override public boolean supportsEntityData() { return false; }
        @Override public void setItemString(dev.rono.igniscore.api.port.IgnisItem item, String key, String value) {}
        @Override public String getItemString(dev.rono.igniscore.api.port.IgnisItem item, String key) { return null; }
        @Override public void setItemInt(dev.rono.igniscore.api.port.IgnisItem item, String key, int value) {}
        @Override public int getItemInt(dev.rono.igniscore.api.port.IgnisItem item, String key, int defaultValue) { return defaultValue; }
        @Override public void setItemBoolean(dev.rono.igniscore.api.port.IgnisItem item, String key, boolean value) {}
        @Override public boolean getItemBoolean(dev.rono.igniscore.api.port.IgnisItem item, String key, boolean defaultValue) { return defaultValue; }
        @Override public void setEntityString(Object nativeEntity, String key, String value) {}
        @Override public String getEntityString(Object nativeEntity, String key) { return null; }
    }
}
