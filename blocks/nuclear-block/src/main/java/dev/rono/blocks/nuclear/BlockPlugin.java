package dev.rono.blocks.nuclear;

import dev.rono.igniscore.api.extension.BlockExtensionContext;
import dev.rono.igniscore.api.extension.IgnisBlockPlugin;

public class BlockPlugin implements IgnisBlockPlugin {

    @Override
    public void onLoad(BlockExtensionContext context) {
        context.registerStrategy(new Strategy(context.getStrategyContext()));
    }
}
