package dev.rono.igniscore.api.extension;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtensionManifestTest {

    @Test
    void parsesRequiredIntegrations() {
        ExtensionManifest manifest = ExtensionManifest.fromMap(Map.of(
                "id", "ore-xray-goggles",
                "strategy", "dev.rono.igniscore.item.orexraygoggles.Strategy",
                "requires-integrations", List.of("protocol", "nbt-entity")
        ), null);

        assertEquals(2, manifest.getRequiredIntegrations().size());
        assertEquals(ExtensionIntegration.PROTOCOL, manifest.getRequiredIntegrations().get(0));
        assertEquals(ExtensionIntegration.NBT_ENTITY, manifest.getRequiredIntegrations().get(1));
    }

    @Test
    void parsesProfiles() {
        ExtensionManifest manifest = ExtensionManifest.fromMap(Map.of(
                "id", "prep-counter",
                "strategy", "dev.rono.igniscore.block.prepcounter.Strategy",
                "profiles", List.of("interact", "placed-hooks", "processing-gui")
        ), null);

        assertEquals(3, manifest.getProfiles().size());
        assertEquals(ExtensionProfile.INTERACT, manifest.getProfiles().get(0));
        assertEquals(ExtensionProfile.PLACED_HOOKS, manifest.getProfiles().get(1));
        assertEquals(ExtensionProfile.PROCESSING_GUI, manifest.getProfiles().get(2));
    }

    @Test
    void rejectsUnknownIntegration() {
        assertThrows(IllegalArgumentException.class,
                () -> ExtensionIntegration.fromManifest("unknown-integration"));
    }

    @Test
    void requirementsValidateWhenCapabilitiesMissing() {
        ExtensionManifest manifest = ExtensionManifest.fromMap(Map.of(
                "id", "test",
                "strategy", "dev.rono.TestStrategy",
                "requires-integrations", List.of("protocol")
        ), null);

        assertThrows(ExtensionRequirementException.class, () ->
                ExtensionRequirements.validate(manifest, new ExtensionRuntimeCapabilities(false, true), false));
    }

    @Test
    void requirementsWarnWhenCapabilitiesMissing() {
        ExtensionManifest manifest = ExtensionManifest.fromMap(Map.of(
                "id", "test",
                "strategy", "dev.rono.TestStrategy",
                "requires-integrations", List.of("protocol")
        ), null);

        List<String> warnings = ExtensionRequirements.validate(
                manifest, new ExtensionRuntimeCapabilities(false, true), true);
        assertEquals(1, warnings.size());
        assertTrue(warnings.getFirst().contains("protocol"));
    }
}
