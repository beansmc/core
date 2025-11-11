package org.moniti.core;

import org.moniti.core.utils.*;
import org.moniti.core.commands.*;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Core extends JavaPlugin {
    private static Core instance;
    private localeManager langManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        File dataFolder = this.getDataFolder();
        if (!dataFolder.exists()) {
            if (!dataFolder.mkdirs()) {
                io.log.severe("Failed to create main plugin data folder! File I/O will fail.");
            }
        }

        this.langManager = new localeManager(this);

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

    /**
     * Global accessor for the Language Manager.
     * @return The LangManager instance.
     */
    @SuppressWarnings("unused")
    public localeManager getLangManager() {
        return langManager;
    }
}
