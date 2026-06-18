package dev.rono.igniscore.sponge.service;

import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.sponge.adapter.SpongeBridge;
import org.junit.jupiter.api.Test;
import org.spongepowered.api.item.inventory.ItemStack;

import static org.junit.jupiter.api.Assertions.assertNull;

class SpongeBlockItemIdentifierTest {

    @Test
    void returnsNullForMissingOrBlankIds() {
        RecordingNbtService nbtService = new RecordingNbtService();
        SpongeBlockItemIdentifier identifier = new SpongeBlockItemIdentifier(nbtService);

        assertNull(identifier.resolveTypeId(null));
        assertNull(identifier.resolveTypeId(ItemStack.empty()));

        IgnisItem ignisItem = SpongeBridge.wrap(ItemStack.empty());
        nbtService.setItemString(ignisItem, "ignis:block_id", " ");
        assertNull(identifier.resolveTypeId(SpongeBridge.unwrap(ignisItem)));
    }

    private static final class RecordingNbtService implements IgnisNbtService {
        private final java.util.Map<IgnisItem, java.util.Map<String, String>> values = new java.util.HashMap<>();

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public String providerName() {
            return "test";
        }

        @Override
        public boolean supportsEntityData() {
            return false;
        }

        @Override
        public void setItemString(IgnisItem item, String key, String value) {
            values.computeIfAbsent(item, ignored -> new java.util.HashMap<>())
                    .put(normalize(key), value);
        }

        @Override
        public String getItemString(IgnisItem item, String key) {
            java.util.Map<String, String> itemValues = values.get(item);
            return itemValues == null ? null : itemValues.get(normalize(key));
        }

        @Override
        public void setItemInt(IgnisItem item, String key, int value) {
        }

        @Override
        public int getItemInt(IgnisItem item, String key, int defaultValue) {
            return defaultValue;
        }

        @Override
        public void setItemBoolean(IgnisItem item, String key, boolean value) {
        }

        @Override
        public boolean getItemBoolean(IgnisItem item, String key, boolean defaultValue) {
            return defaultValue;
        }

        @Override
        public void setEntityString(Object nativeEntity, String key, String value) {
        }

        @Override
        public String getEntityString(Object nativeEntity, String key) {
            return null;
        }

        private static String normalize(String key) {
            return key.startsWith("ignis:") ? key.substring("ignis:".length()) : key;
        }
    }
}
