package org.moniti.core;

import org.moniti.core.commands.cc;
import org.moniti.core.commands.radio;

import org.bukkit.plugin.java.JavaPlugin;

public final class Core extends JavaPlugin {
    // Instance of the plugin, often called a Singleton, for easy access in other classes.
    private static Core instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        getLogger().info("Core (`org.moniti.core`) has started!");
        registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Core (`org.moniti.core`) has shut down.");
    }

    /**
     * Registers all custom commands for the plugin.
     */
    private void registerCommands() {
        // Create an instance of each command class and register it.
        // All commands must be defined in plugin.yml
        getCommand("core").setExecutor(new cc());
        getCommand("radio").setExecutor(new radio(this));
    }

    /**
     * Global accessor for the plugin instance.
     * @return The main plugin instance.
     */
    public static Core getInstance() {
        return instance;
    }
}
