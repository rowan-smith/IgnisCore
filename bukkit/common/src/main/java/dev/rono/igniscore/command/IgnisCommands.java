package dev.rono.igniscore.command;

import java.util.List;

public final class IgnisCommands {
    public static final String IGNIS = "ignis";
    public static final String DESCRIPTION = "Main command for IgnisCore";
    public static final String USAGE = "/ignis <give|pack|reload|debug|blocks|items>";
    public static final String PERMISSION = "igniscore.admin";
    public static final List<String> ALIASES = List.of("igniscore", "ic");

    private IgnisCommands() {
    }
}
