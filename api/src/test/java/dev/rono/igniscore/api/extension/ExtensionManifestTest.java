package dev.rono.igniscore.api.extension;

import dev.rono.igniscore.api.IgnisApiVersion;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtensionManifestTest {
    @Test
    void parsesStrategyManifestFromYaml() {
        String yaml = """
                id: nuke
                name: Nuke Block
                version: 2.0.0
                api-version: 1.0.0
                author: Tester
                strategy: dev.rono.igniscore.block.nuke.Strategy
                """;

        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)),
                "block-extension.yml");

        assertEquals("nuke", manifest.getId());
        assertEquals("Nuke Block", manifest.getName());
        assertEquals("2.0.0", manifest.getVersion());
        assertEquals("dev.rono.igniscore.block.nuke.Strategy", manifest.getStrategyClass());
        assertEquals("Tester", manifest.getAuthor());
        assertEquals("1.0.0", manifest.getApiVersion());
    }

    @Test
    void resolvesLegacyMainClassToStrategy() {
        String yaml = """
                id: nuke
                main: dev.rono.igniscore.block.nuke.BlockPlugin
                """;

        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)),
                "block-extension.yml");

        assertEquals("dev.rono.igniscore.block.nuke.Strategy", manifest.getStrategyClass());
    }

    @Test
    void infersStrategyClassFromBlockExtensionId() {
        String yaml = """
                id: nuke
                """;

        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)),
                "block-extension.yml");

        assertEquals("dev.rono.igniscore.block.nuke.Strategy", manifest.getStrategyClass());
    }

    @Test
    void infersStrategyClassFromItemExtensionId() {
        String yaml = """
                id: grenade
                """;

        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)),
                "item-extension.yml");

        assertEquals("dev.rono.igniscore.item.grenade.Strategy", manifest.getStrategyClass());
    }

    @Test
    void appliesDefaultsForMissingMetadata() {
        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream("id: wormhole-tnt\n".getBytes(StandardCharsets.UTF_8)),
                "block-extension.yml");

        assertEquals("wormhole-tnt", manifest.getId());
        assertEquals("wormhole-tnt", manifest.getName());
        assertEquals("1.0.0", manifest.getVersion());
        assertEquals(IgnisApiVersion.CURRENT, manifest.getApiVersion());
        assertEquals("unknown", manifest.getAuthor());
        assertEquals("dev.rono.igniscore.block.wormholetnt.Strategy", manifest.getStrategyClass());
    }

    @Test
    void requiresExtensionId() {
        NullPointerException error = assertThrows(NullPointerException.class,
                () -> ExtensionManifest.fromStream(
                        new ByteArrayInputStream("name: Missing Id\n".getBytes(StandardCharsets.UTF_8)),
                        "block-extension.yml"));

        assertEquals("block-extension.yml requires id", error.getMessage());
    }

    @Test
    void resolvesIdFromConfigWhenManifestOmitsIt() {
        ExtensionManifest manifest = ExtensionManifest.fromJarContents(
                Map.of("name", "Detonator"),
                Map.of("id", "detonator"),
                "item-extension.yml",
                "detonator");

        assertEquals("detonator", manifest.getId());
        assertEquals("dev.rono.igniscore.item.detonator.Strategy", manifest.getStrategyClass());
    }

    @Test
    void resolvesIdFromJarNameWhenManifestAndConfigOmitIt() {
        ExtensionManifest manifest = ExtensionManifest.fromJarContents(
                Map.of("name", "Detonator"),
                Map.of(),
                "item-extension.yml",
                "detonator");

        assertEquals("detonator", manifest.getId());
    }

    @Test
    void requiresResolvableStrategyClass() {
        NullPointerException error = assertThrows(NullPointerException.class,
                () -> ExtensionManifest.fromStream(
                        new ByteArrayInputStream("id: custom-extension\n".getBytes(StandardCharsets.UTF_8)),
                        "unknown-manifest.yml"));

        assertEquals("extension manifest requires strategy", error.getMessage());
    }

    @Test
    void parsesRequiresIntegrations() {
        String yaml = """
                id: nuke
                requires-integrations:
                  - protocol
                  - nbt-entity
                """;

        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)),
                "block-extension.yml");

        assertEquals(2, manifest.getRequiredIntegrations().size());
        assertEquals(ExtensionIntegration.PROTOCOL, manifest.getRequiredIntegrations().get(0));
        assertEquals(ExtensionIntegration.NBT_ENTITY, manifest.getRequiredIntegrations().get(1));
    }

    @Test
    void parsesRequiresIntegrationsWithUnderscores() {
        String yaml = """
                id: nuke
                requires-integrations:
                  - nbt_entity
                """;

        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)),
                "block-extension.yml");

        assertEquals(1, manifest.getRequiredIntegrations().size());
        assertEquals(ExtensionIntegration.NBT_ENTITY, manifest.getRequiredIntegrations().get(0));
    }

    @Test
    void defaultsRequiresIntegrationsToEmpty() {
        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream("id: nuke\n".getBytes(StandardCharsets.UTF_8)),
                "block-extension.yml");

        assertTrue(manifest.getRequiredIntegrations().isEmpty());
    }

    @Test
    void rejectsUnknownRequiresIntegrationsEntry() {
        String yaml = """
                id: nuke
                requires-integrations:
                  - unknown-integration
                """;

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> ExtensionManifest.fromStream(
                        new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)),
                        "block-extension.yml"));

        assertEquals("Unknown requires-integrations entry: unknown-integration", error.getMessage());
    }

    @Test
    void parsesProfiles() {
        String yaml = """
                id: nuke
                profiles:
                  - fuse
                  - placed-hooks
                  - drop-collector
                """;

        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)),
                "block-extension.yml");

        assertEquals(3, manifest.getProfiles().size());
        assertEquals(ExtensionProfile.FUSE, manifest.getProfiles().get(0));
        assertEquals(ExtensionProfile.PLACED_HOOKS, manifest.getProfiles().get(1));
        assertEquals(ExtensionProfile.DROP_COLLECTOR, manifest.getProfiles().get(2));
    }

    @Test
    void parsesProfilesWithUnderscores() {
        String yaml = """
                id: grenade
                profiles:
                  - item_use
                  - processing_gui
                """;

        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)),
                "item-extension.yml");

        assertEquals(2, manifest.getProfiles().size());
        assertEquals(ExtensionProfile.ITEM_USE, manifest.getProfiles().get(0));
        assertEquals(ExtensionProfile.PROCESSING_GUI, manifest.getProfiles().get(1));
    }

    @Test
    void defaultsProfilesToEmpty() {
        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream("id: nuke\n".getBytes(StandardCharsets.UTF_8)),
                "block-extension.yml");

        assertTrue(manifest.getProfiles().isEmpty());
    }

    @Test
    void rejectsUnknownProfilesEntry() {
        String yaml = """
                id: nuke
                profiles:
                  - unknown-profile
                """;

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> ExtensionManifest.fromStream(
                        new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)),
                        "block-extension.yml"));

        assertEquals("Unknown profiles entry: unknown-profile", error.getMessage());
    }
}
