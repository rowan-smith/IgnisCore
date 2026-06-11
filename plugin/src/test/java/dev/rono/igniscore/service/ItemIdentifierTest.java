package dev.rono.igniscore.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class ItemIdentifierTest {
    @Test
    void returnsNullForNullItem() {
        ItemIdentifier identifier = new ItemIdentifier(new NBTService());
        assertNull(identifier.resolveTypeId(null));
    }
}
