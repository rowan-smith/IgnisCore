package dev.rono.igniscore.block.icecreamfreezer;

import dev.rono.extensions.shared.strategy.ProcessingGuiSupport;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.block.icecreamfreezer.IceCreamFreezerSupport;

final class IceCreamFreezerOnBlockInteractListener implements OnBlockInteractListener {
    private final IceCreamFreezerRuntime runtime;

    IceCreamFreezerOnBlockInteractListener(IceCreamFreezerRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.action() != CustomBlockAction.OPEN) {
            return;
        }
        if (event.heldItem() != null && !event.heldItem().isAir() && ProcessingGuiSupport.matches(event.heldItem(), "bowl")) {
            event.player().applyPotionEffect("FIRE_RESISTANCE", 200, 0);
            event.heldItem().setAmount(event.heldItem().getAmount() - 1);
            event.player().sendMessage("<aqua>Ice cream grants brief fire resistance.</aqua>");
        }
        runtime.registry.openBlock(event.player(), event.block().location());
    }
}

