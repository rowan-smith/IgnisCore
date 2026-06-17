package dev.rono.igniscore.item.grenade;

import dev.rono.igniscore.api.event.ItemClickEvent;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import dev.rono.igniscore.testsupport.TestEventBus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BehaviorTest {
    @Test
    void itemUseConsumesStack() {
        TestEventBus.TestContext ctx = TestEventBus.createContext();
        ItemDefinition definition = ExtensionTestSupport.loadItemDefinition(BehaviorTest.class, "grenade", 20001);
        Strategy strategy = TestEventBus.activate(() -> new Strategy(ctx.context()), "grenade");
        TestPlayer player = new TestPlayer(ctx.world());
        TestItem item = new TestItem(1);

        ctx.eventBus().fireItemClick(
                new ItemClickEvent(player, definition, item, IgnisInteraction.RIGHT_CLICK_AIR, null, "throw"),
                "grenade");

        assertEquals(0, item.getAmount());
    }

    private static final class TestItem implements IgnisItem {
        private int amount;

        private TestItem(int amount) {
            this.amount = amount;
        }

        @Override
        public int getAmount() {
            return amount;
        }

        @Override
        public void setAmount(int amount) {
            this.amount = amount;
        }

        @Override
        public String getMaterialKey() {
            return "snowball";
        }

        @Override
        public boolean isAir() {
            return amount <= 0;
        }

        @Override
        public Object nativeItem() {
            return this;
        }
    }

    private static final class TestPlayer implements IgnisPlayer {
        private final IgnisWorld world;
        private final IgnisLocation eye = new IgnisLocation("world", 0, 1.6, 0);

        private TestPlayer(IgnisWorld world) {
            this.world = world;
        }

        @Override
        public UUID getUniqueId() {
            return UUID.randomUUID();
        }

        @Override
        public String getName() {
            return "tester";
        }

        @Override
        public IgnisLocation getLocation() {
            return eye;
        }

        @Override
        public IgnisLocation getEyeLocation() {
            return eye.withYawPitch(0f, 0f);
        }

        @Override
        public IgnisWorld getWorld() {
            return world;
        }

        @Override
        public void sendMessage(String miniMessage) {
        }

        @Override
        public void openInventory(Object nativeInventory) {
        }

        @Override
        public void applyPotionEffect(String effectKey, int durationTicks, int amplifier) {
        }
    }
}
