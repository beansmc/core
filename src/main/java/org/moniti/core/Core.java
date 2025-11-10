package org.moniti.core;

import org.moniti.core.utils.*;
import org.moniti.core.commands.*;

import org.bukkit.plugin.java.JavaPlugin;

public final class Core extends JavaPlugin {
    private static Core instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        io.log.info("Core (`org.moniti.core`) has started!");
        registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        io.log.info("Core (`org.moniti.core`) has shut down.");
    }

    /**
     * Registers all custom commands for the plugin.
     * Concepts:
     * - Permissions: `core.command.NAME`
     */
    private void registerCommands() {
        // Create an instance of each command class and register it.
        // All commands must be defined in plugin.yml
        command.register(this,"core", new cc());
        command.register(this,"msg", new msg());
        //command.register(this, "radio", new radio(this));

        // org.moniti.core.fix package commands
        new fixes(this).registerAll();
    }

    /**
     * Global accessor for the plugin instance.
     * @return The main plugin instance.
     */
    @SuppressWarnings("unused")
    public static Core getInstance() {
        return instance;
    }
}
