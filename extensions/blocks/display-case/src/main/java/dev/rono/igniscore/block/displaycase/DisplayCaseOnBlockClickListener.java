package dev.rono.igniscore.block.displaycase;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockClickEvent;
import dev.rono.igniscore.api.event.OnBlockClickListener;
import dev.rono.igniscore.api.port.IgnisInteraction;

final class DisplayCaseOnBlockClickListener implements OnBlockClickListener {
    @Override
    public void onBlockClick(BlockClickEvent event) {
        if (event.interaction() == IgnisInteraction.LEFT_CLICK_BLOCK) {
            event.setResult(CustomBlockAction.BREAK);
        } else if (event.interaction() == IgnisInteraction.RIGHT_CLICK_BLOCK) {
            event.setResult(CustomBlockAction.OPEN);
        }
    }
}
