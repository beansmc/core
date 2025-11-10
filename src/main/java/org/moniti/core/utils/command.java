package org.moniti.core.utils;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class command {
    // Private constructor to prevent instantiation of a utility class
    private command() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * Registers a single command executor to the server.
     * <p>
     * It uses Objects.requireNonNull() to ensure the command name is defined in plugin.yml.
     * If missing, it throws an IllegalStateException for clearer error reporting on plugin startup.
     * </p>
     * @param plugin The main JavaPlugin instance (Core.getInstance()).
     * @param name The command name (e.g., "core", "msg").
     * @param executor The CommandExecutor instance.
     */
    public static void register(JavaPlugin plugin, String name, CommandExecutor executor) {
        Objects.requireNonNull(
                plugin.getCommand(name),
                "Command '" + name + "' is not defined in plugin.yml! Registration failed."
        ).setExecutor(executor);
    }
}
