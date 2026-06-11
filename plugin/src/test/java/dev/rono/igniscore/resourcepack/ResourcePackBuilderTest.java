package dev.rono.igniscore.resourcepack;

import dev.rono.igniscore.api.model.BlockDefinition;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourcePackBuilderTest {
    @Test
    void usesCubeBottomTopModelForSingleSideTexture() throws Exception {
        CompiledBlockAsset asset = compileAsset(blockDefinition(null, null, null, null));

        assertEquals("minecraft:block/cube_bottom_top", asset.getBlockModel().get("parent").getAsString());
        assertEquals("igniscore:block/test/side", asset.getBlockModel().getAsJsonObject("textures").get("side").getAsString());
        assertEquals("side.png", asset.getTextures().get("side"));
        assertFalse(asset.getTextures().containsKey("side-1"));
    }

    @Test
    void usesCubeModelForPerSideTextures() throws Exception {
        CompiledBlockAsset asset = compileAsset(blockDefinition("north.png", "east.png", "south.png", "west.png"));

        assertEquals("minecraft:block/cube", asset.getBlockModel().get("parent").getAsString());
        var texturesJson = asset.getBlockModel().getAsJsonObject("textures");
        assertEquals("igniscore:block/test/side-1", texturesJson.get("north").getAsString());
        assertEquals("igniscore:block/test/side-2", texturesJson.get("east").getAsString());
        assertEquals("igniscore:block/test/side-3", texturesJson.get("south").getAsString());
        assertEquals("igniscore:block/test/side-4", texturesJson.get("west").getAsString());
        assertEquals("north.png", asset.getTextures().get("side-1"));
        assertEquals("east.png", asset.getTextures().get("side-2"));
        assertEquals("south.png", asset.getTextures().get("side-3"));
        assertEquals("west.png", asset.getTextures().get("side-4"));
        assertFalse(asset.getTextures().containsKey("side"));
    }

    @Test
    void preservesJpegSourceFilenamesInCompiledTextureMap() throws Exception {
        CompiledBlockAsset asset = compileAsset(blockDefinition("north.jpg", "east.jpeg", null, null));

        assertEquals("north.jpg", asset.getTextures().get("side-1"));
        assertEquals("east.jpeg", asset.getTextures().get("side-2"));
        assertEquals("side.png", asset.getTextures().get("side-3"));
    }

    @Test
    void perSideTexturesFallBackToSharedSideSourceFile() throws Exception {
        CompiledBlockAsset asset = compileAsset(blockDefinition("north.png", null, null, null));

        assertTrue(asset.getTextures().containsKey("side-1"));
        assertEquals("north.png", asset.getTextures().get("side-1"));
        assertEquals("side.png", asset.getTextures().get("side-2"));
        assertEquals("side.png", asset.getTextures().get("side-3"));
        assertEquals("side.png", asset.getTextures().get("side-4"));
    }

    private CompiledBlockAsset compileAsset(BlockDefinition definition) throws Exception {
        ResourcePackBuilder builder = new ResourcePackBuilder(null, null, null);
        Method compileBlocks = ResourcePackBuilder.class.getDeclaredMethod("compileBlocks", Map.class);
        compileBlocks.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<CompiledBlockAsset> assets = (List<CompiledBlockAsset>) compileBlocks.invoke(builder, Map.of(definition.getId(), definition));
        return assets.getFirst();
    }

    private BlockDefinition blockDefinition(String side1, String side2, String side3, String side4) {
        return new BlockDefinition(
                "test",
                "paper",
                "carrot_on_a_stick",
                Component.text("Test"),
                List.of(),
                true,
                true,
                "top.png",
                "side.png",
                "bottom.png",
                "default",
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                10001,
                false,
                false,
                false,
                "test-block",
                side1,
                side2,
                side3,
                side4
        );
    }
}
