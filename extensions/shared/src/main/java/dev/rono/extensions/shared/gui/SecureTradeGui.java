package dev.rono.extensions.shared.gui;

import dev.rono.igniscore.api.inventory.IgnisCustomInventory;
import dev.rono.igniscore.api.port.IgnisInventory;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Dual-player trade chest: each side offers items and confirms with lime dye.
 */
public final class SecureTradeGui implements IgnisCustomInventory {
    static final int OFFER_SLOTS = 7;
    static final int PLAYER_A_OFFER_START = 10;
    static final int PLAYER_B_OFFER_START = 28;
    static final int PLAYER_A_CONFIRM = 20;
    static final int PLAYER_B_CONFIRM = 24;
    static final int STATUS_SLOT = 22;
    static final int TOTAL_SLOTS = 45;

    private static final int[] DECORATOR_SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 21, 23, 25, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};

    private final IgnisLocation blockLocation;
    private final IgnisInventory inventory;
    private final ExtensionSupport extensionSupport;
    private UUID playerA;
    private UUID playerB;
    private Consumer<SecureTradeGui> onChanged = ignored -> {};

    public SecureTradeGui(IgnisLocation blockLocation, Component title, ExtensionSupport extensionSupport) {
        this.blockLocation = Locations.toBlock(blockLocation);
        this.extensionSupport = extensionSupport;
        this.inventory = extensionSupport.createInventory(null, TOTAL_SLOTS, title);
        fillDecorators();
        inventory.setItem(STATUS_SLOT, extensionSupport.createItem("paper", 1));
    }

    public void setOnChanged(Consumer<SecureTradeGui> onChanged) {
        this.onChanged = onChanged == null ? ignored -> { } : onChanged;
    }

    public IgnisLocation blockLocation() {
        return blockLocation;
    }

    public IgnisInventory inventory() {
        return inventory;
    }

    public void register() {
        extensionSupport.registerCustomInventory(inventory.nativeInventory(), this);
    }

    public void unregister() {
        extensionSupport.unregisterCustomInventory(inventory.nativeInventory());
    }

    public void open(IgnisPlayer player) {
        join(player);
        extensionSupport.openInventory(player, inventory);
    }

    public void join(IgnisPlayer player) {
        UUID id = player.getUniqueId();
        if (playerA == null) {
            playerA = id;
            return;
        }
        if (playerA.equals(id)) {
            return;
        }
        if (playerB == null || playerB.equals(id)) {
            playerB = id;
        }
    }

    public boolean bothConfirmed() {
        return isConfirmed(PLAYER_A_CONFIRM) && isConfirmed(PLAYER_B_CONFIRM);
    }

    public boolean executeTrade(ExtensionSupport extensionSupport) {
        if (!bothConfirmed() || playerA == null || playerB == null) {
            return false;
        }
        List<IgnisItem> offerA = copyOffer(PLAYER_A_OFFER_START);
        List<IgnisItem> offerB = copyOffer(PLAYER_B_OFFER_START);
        if (offerA.isEmpty() && offerB.isEmpty()) {
            return false;
        }
        clearOffer(PLAYER_A_OFFER_START);
        clearOffer(PLAYER_B_OFFER_START);
        clearConfirm(PLAYER_A_CONFIRM);
        clearConfirm(PLAYER_B_CONFIRM);
        inventory.setItem(STATUS_SLOT, extensionSupport.createItem("lime_dye", 1));
        return true;
    }

    public List<IgnisItem> offerFor(UUID playerId) {
        if (playerId != null && playerId.equals(playerA)) {
            return copyOffer(PLAYER_B_OFFER_START);
        }
        if (playerId != null && playerId.equals(playerB)) {
            return copyOffer(PLAYER_A_OFFER_START);
        }
        return List.of();
    }

    public List<IgnisItem> takeOffer(UUID playerId) {
        if (playerId != null && playerId.equals(playerA)) {
            return drainOffer(PLAYER_A_OFFER_START);
        }
        if (playerId != null && playerId.equals(playerB)) {
            return drainOffer(PLAYER_B_OFFER_START);
        }
        return List.of();
    }

    @Override
    public boolean accepts(IgnisItem stack) {
        return stack != null && !stack.isAir();
    }

    @Override
    public void restoreDecorations() {
        fillDecorators();
    }

    @Override
    public boolean isSeparatorSlot(int slot) {
        for (int decorator : DECORATOR_SLOTS) {
            if (slot == decorator) {
                return true;
            }
        }
        return slot == STATUS_SLOT;
    }

    @Override
    public void onChange() {
        updateStatus();
        onChanged.accept(this);
    }

    @Override
    public void onClose() {
        onChanged.accept(this);
    }

    private void updateStatus() {
        inventory.setItem(STATUS_SLOT, extensionSupport.createItem(
                bothConfirmed() ? "lime_dye" : "paper", 1));
    }

    private boolean isConfirmed(int slot) {
        IgnisItem item = inventory.getItem(slot);
        return item != null && !item.isAir()
                && "lime_dye".equalsIgnoreCase(item.getMaterialKey());
    }

    private void clearConfirm(int slot) {
        inventory.setItem(slot, null);
    }

    private List<IgnisItem> copyOffer(int start) {
        List<IgnisItem> items = new ArrayList<>();
        for (int slot = start; slot < start + OFFER_SLOTS; slot++) {
            IgnisItem item = inventory.getItem(slot);
            if (item != null && !item.isAir()) {
                items.add(extensionSupport.createItem(item.getMaterialKey(), item.getAmount()));
            }
        }
        return items;
    }

    private List<IgnisItem> drainOffer(int start) {
        List<IgnisItem> items = new ArrayList<>();
        for (int slot = start; slot < start + OFFER_SLOTS; slot++) {
            IgnisItem item = inventory.getItem(slot);
            if (item != null && !item.isAir()) {
                items.add(item);
                inventory.setItem(slot, null);
            }
        }
        return items;
    }

    private void clearOffer(int start) {
        for (int slot = start; slot < start + OFFER_SLOTS; slot++) {
            inventory.setItem(slot, null);
        }
    }

    private void fillDecorators() {
        for (int slot : DECORATOR_SLOTS) {
            inventory.setItem(slot, extensionSupport.createItem("gray_stained_glass_pane", 1));
        }
    }
}
