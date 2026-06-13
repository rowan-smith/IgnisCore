package dev.rono.igniscore.item.detonator;

import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.service.IgnisNbtService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class DetonatorLinkStorage {
    static final String LINKED_BOMBS_KEY = "ignis:linked_bombs";

    private final IgnisNbtService nbtService;

    DetonatorLinkStorage(IgnisNbtService nbtService) {
        this.nbtService = nbtService;
    }

    List<String> readLinkedBombs(IgnisItem item) {
        String encoded = nbtService.getItemString(item, LINKED_BOMBS_KEY);
        if (encoded == null || encoded.isBlank()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(encoded.split("\\|")));
    }

    void writeLinkedBombs(IgnisItem item, List<String> linkedBombs) {
        if (linkedBombs.isEmpty()) {
            nbtService.setItemString(item, LINKED_BOMBS_KEY, "");
            return;
        }
        nbtService.setItemString(item, LINKED_BOMBS_KEY, String.join("|", linkedBombs));
    }
}
