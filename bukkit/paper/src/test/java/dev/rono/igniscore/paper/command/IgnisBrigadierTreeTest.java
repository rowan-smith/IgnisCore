package dev.rono.igniscore.paper.command;

import dev.rono.igniscore.command.IgnisCommandBridge;
import dev.rono.igniscore.command.IgnisCommands;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IgnisBrigadierTreeTest {

    @Test
    void buildsRootAndSubcommandLiterals() {
        var node = IgnisBrigadierTree.build(new IgnisCommandBridge());

        assertEquals(IgnisCommands.IGNIS, node.getName());
        assertNotNull(node.getChild("give"));
        assertNotNull(node.getChild("pack"));
        assertNotNull(node.getChild("reload"));
        assertNotNull(node.getChild("debug"));
        assertNotNull(node.getChild("blocks"));
        assertNotNull(node.getChild("items"));

        var reload = node.getChild("reload");
        assertNotNull(reload);
        assertNotNull(reload.getChild("all"));
        assertNotNull(reload.getChild("blocks"));
        assertNotNull(reload.getChild("items"));
        assertNotNull(reload.getChild("server"));
    }
}
