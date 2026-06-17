package dev.rono.igniscore.block.tunnelingtnt;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.config.BlockBehaviorConfig;
import dev.rono.igniscore.api.event.BlockClickEvent;
import dev.rono.igniscore.api.event.OnBlockClickListener;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;

final class TunnelingOnBlockClickListener implements OnBlockClickListener {
    @Override
    public void onBlockClick(BlockClickEvent event) {
        if (event.interaction() == IgnisInteraction.LEFT_CLICK_BLOCK) {
            event.setResult(CustomBlockAction.BREAK);
            return;
        }
        if (event.interaction() != IgnisInteraction.RIGHT_CLICK_BLOCK) {
            return;
        }
        BlockBehaviorConfig behavior = BlockBehaviorConfig.from(event.block().definition().getBehaviorConfig());
        if (!behavior.combustible()) {
            return;
        }
        String material = materialKey(event.heldItem());
        for (String ignition : behavior.ignitionMaterials()) {
            if (material.equalsIgnoreCase(ignition)) {
                event.setResult(CustomBlockAction.IGNITE);
                return;
            }
        }
    }

    private static String materialKey(IgnisItem heldItem) {
        if (heldItem == null || heldItem.isAir()) {
            return "AIR";
        }
        String materialKey = heldItem.getMaterialKey();
        return materialKey == null || materialKey.isBlank() ? "AIR" : materialKey;
    }
}
