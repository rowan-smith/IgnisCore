package dev.rono.igniscore.sponge.adapter;

import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.sponge.support.SpongeRegistrySupport;
import org.spongepowered.api.block.BlockSnapshot;

public final class SpongeIgnisBlock implements IgnisBlock {
    private final BlockSnapshot handle;

    public SpongeIgnisBlock(BlockSnapshot handle) {
        this.handle = handle;
    }

    public BlockSnapshot getHandle() {
        return handle;
    }

    @Override
    public IgnisLocation getLocation() {
        return handle.location()
                .map(SpongeBridge::toIgnis)
                .orElseGet(() -> new IgnisLocation(handle.world().asString(), handle.position().x(), handle.position().y(), handle.position().z()));
    }

    @Override
    public String getMaterialKey() {
        return SpongeBridge.materialKey(handle.state().type());
    }

    @Override
    public void setMaterialKey(String materialKey) {
        handle.location().ifPresent(location -> {
            var resolved = org.spongepowered.api.ResourceKey.resolve(materialKey);
            SpongeRegistrySupport.findBlockType(resolved)
                    .ifPresent(type -> location.restoreSnapshot(
                            org.spongepowered.api.block.BlockSnapshot.builder()
                                    .from(location)
                                    .blockState(type.defaultState())
                                    .build(),
                            false,
                            org.spongepowered.api.world.BlockChangeFlags.ALL));
        });
    }
}
