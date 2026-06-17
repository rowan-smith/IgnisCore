package dev.rono.igniscore.block.coffeebrewer;

import dev.rono.extensions.shared.strategy.ProcessingGuiSupport;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.block.coffeebrewer.CoffeeBrewerSupport;

final class CoffeeBrewerOnBlockInteractListener implements OnBlockInteractListener {
    private final CoffeeBrewerRuntime runtime;

    CoffeeBrewerOnBlockInteractListener(CoffeeBrewerRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.action() != CustomBlockAction.OPEN) {
            return;
        }
        if (event.heldItem() != null && !event.heldItem().isAir() && ProcessingGuiSupport.matches(event.heldItem(), "potion", "glass_bottle")) {
            event.player().applyPotionEffect("SPEED", 300, 0);
            event.heldItem().setAmount(event.heldItem().getAmount() - 1);
            event.player().sendMessage("<gold>Coffee boosts your speed.</gold>");
        }
        runtime.registry.openBlock(event.player(), event.block().location());
    }
}

