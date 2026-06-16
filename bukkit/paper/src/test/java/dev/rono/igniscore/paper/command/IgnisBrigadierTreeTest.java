package dev.rono.igniscore.paper.command;

import dev.rono.igniscore.command.IgnisCommandBridge;
import dev.rono.igniscore.command.IgnisCommands;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IgnisBrigadierTreeTest {

    @Test
    void buildsRootAndSubcommandLiterals() {
        var node = IgnisBrigadierTree.build(new IgnisCommandBridge());

        assertEquals(IgnisCommands.IGNIS, node.getName());
        assertTrue(node.getChildren().containsKey("give"));
        assertTrue(node.getChildren().containsKey("pack"));
        assertTrue(node.getChildren().containsKey("reload"));
        assertTrue(node.getChildren().containsKey("debug"));
        assertTrue(node.getChildren().containsKey("blocks"));
        assertTrue(node.getChildren().containsKey("items"));

        var reload = node.getChildren().get("reload");
        assertNotNull(reload);
        assertTrue(reload.getChildren().containsKey("all"));
        assertTrue(reload.getChildren().containsKey("blocks"));
        assertTrue(reload.getChildren().containsKey("items"));
        assertTrue(reload.getChildren().containsKey("server"));
    }
}
