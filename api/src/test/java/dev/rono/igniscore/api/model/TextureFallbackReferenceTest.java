package dev.rono.igniscore.api.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextureFallbackReferenceTest {
    @Test
    void parsesMinecraftNamespace() {
        TextureFallbackReference reference = TextureFallbackReference.parse("minecraft:tnt");

        assertTrue(reference.isMinecraft());
        assertEquals("minecraft", reference.namespace());
        assertEquals("tnt", reference.id());
        assertEquals("minecraft:tnt", reference.canonical());
    }

    @Test
    void parsesBareNameAsIgniscoreCatalog() {
        TextureFallbackReference reference = TextureFallbackReference.parse("grenade");

        assertTrue(reference.isIgniscoreCatalog());
        assertEquals("igniscore", reference.namespace());
        assertEquals("grenade", reference.id());
    }

    @Test
    void parsesExplicitIgniscoreNamespace() {
        TextureFallbackReference reference = TextureFallbackReference.parse("igniscore:mine");

        assertTrue(reference.isIgniscoreCatalog());
        assertEquals("mine", reference.id());
    }

    @Test
    void blankValuesReturnNull() {
        assertNull(TextureFallbackReference.parse(null));
        assertNull(TextureFallbackReference.parse(""));
        assertNull(TextureFallbackReference.parse("   "));
    }
}
