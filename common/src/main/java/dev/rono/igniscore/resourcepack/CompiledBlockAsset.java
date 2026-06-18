package dev.rono.igniscore.resourcepack;

import com.google.gson.JsonObject;
import java.util.Map;

public class CompiledBlockAsset {
    private final String id;
    private final String baseMaterial;
    private final String renderMaterial;
    private final int customModelData;
    private final String textureFallback;
    private final Map<String, String> textures;
    private final JsonObject blockModel;
    private final JsonObject itemModel;

    public CompiledBlockAsset(String id, String baseMaterial, String renderMaterial, int customModelData,
                              String textureFallback, Map<String, String> textures, JsonObject blockModel,
                              JsonObject itemModel) {
        this.id = id;
        this.baseMaterial = baseMaterial;
        this.renderMaterial = renderMaterial;
        this.customModelData = customModelData;
        this.textureFallback = textureFallback;
        this.textures = textures;
        this.blockModel = blockModel;
        this.itemModel = itemModel;
    }

    public String getId() { return id; }
    public String getBaseMaterial() { return baseMaterial; }
    public String getRenderMaterial() { return renderMaterial; }
    public int getCustomModelData() { return customModelData; }
    public String getTextureFallback() { return textureFallback; }
    public Map<String, String> getTextures() { return textures; }
    public JsonObject getBlockModel() { return blockModel; }
    public JsonObject getItemModel() { return itemModel; }
}
