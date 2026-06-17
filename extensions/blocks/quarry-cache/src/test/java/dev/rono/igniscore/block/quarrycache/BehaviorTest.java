package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.config.BlockBehaviorConfig;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.testsupport.BehaviorTestSupport;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class BehaviorTest {
    @Test
    void profileOpensOnRightClick() {
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(BehaviorTest.class, "quarry-cache", 10001);
        Strategy strategy = new Strategy(BehaviorTestSupport.createContext().context());
        StrategyProfile profile = BlockBehaviorConfig.from(definition.getBehaviorConfig())
                .merge(strategy.profile(definition));

        assertEquals(CustomBlockAction.OPEN, profile.getRightClickAction());
        assertFalse(profile.isCombustible());
    }
}
