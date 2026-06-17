package dev.rono.igniscore.block.picnicbasket;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class PicnicBasketRuntime {
    final IgnisStrategyContext context;
    final BlockStorageRegistry registry;

    PicnicBasketRuntime(IgnisStrategyContext context) {
        this.context = context;
                this.registry = new BlockStorageRegistry(context, "picnic-basket");
    }
}

