package dev.rono.igniscore.manager;

import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;

public interface PlacedBlockRegistry extends BlockDefinitionLookup {

    void registerPlacedBlock(IgnisLocation location, String typeId, IgnisItem placedFrom);

    String getPlacedBlockType(IgnisLocation location);
}
