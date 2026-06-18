package dev.rono.igniscore.resourcepack;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class CompiledBlockAssetTest {
    @Test
    void exposesCompiledAssetMetadata() {
        JsonObject blockModel = new JsonObject();
        blockModel.addProperty("parent", "minecraft:block/cube");
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", "igniscore:block/nuke");
        Map<String, String> textures = Map.of(
                "top", "top.png",
                "side", "side.png",
                "bottom", "bottom.png"
        );

        CompiledBlockAsset asset = new CompiledBlockAsset(
                "nuke",
                "paper",
                "carrot_on_a_stick",
                10001,
                "minecraft:tnt",
                textures,
                blockModel,
                itemModel
        );

        assertEquals("nuke", asset.getId());
        assertEquals("paper", asset.getBaseMaterial());
        assertEquals("carrot_on_a_stick", asset.getRenderMaterial());
        assertEquals(10001, asset.getCustomModelData());
        assertEquals("minecraft:tnt", asset.getTextureFallback());
        assertEquals("side.png", asset.getTextures().get("side"));
        assertSame(blockModel, asset.getBlockModel());
        assertSame(itemModel, asset.getItemModel());
    }
}
