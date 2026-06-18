package dev.rono.igniscore.support;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.config.BlockBehaviorConfig;
import dev.rono.igniscore.api.event.OnBlockClickListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.event.IgnisEventBusImpl;

public final class TestBlockClickListeners {
    private TestBlockClickListeners() {
    }

    public static void register(IgnisEventBusImpl eventBus, BlockDefinition definition) {
        eventBus.subscribe(definition.getExtensionId(), clickListener(definition));
    }

    private static OnBlockClickListener clickListener(BlockDefinition definition) {
        return event -> {
            if (event.interaction() == IgnisInteraction.LEFT_CLICK_BLOCK) {
                event.setResult(CustomBlockAction.BREAK);
                return;
            }
            if (event.interaction() != IgnisInteraction.RIGHT_CLICK_BLOCK) {
                return;
            }
            BlockBehaviorConfig behavior = BlockBehaviorConfig.from(definition.getBehaviorConfig());
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
        };
    }

    private static String materialKey(IgnisItem heldItem) {
        if (heldItem == null || heldItem.isAir()) {
            return "AIR";
        }
        String materialKey = heldItem.getMaterialKey();
        return materialKey == null || materialKey.isBlank() ? "AIR" : materialKey;
    }
}
