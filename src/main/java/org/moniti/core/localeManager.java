package org.moniti.core;

import org.moniti.core.utils.io;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * Manages the plugin's localization system (i18n).
 * It handles loading the active language file and provides methods to retrieve messages.
 */
public class localeManager {
    private final Core plugin;
    private YamlConfiguration activeLangConfig;
    private final String activeLangCode = "en"; // TODO: set this to only a default value. Not hardcoded / optional.

    public localeManager(Core plugin) {
        this.plugin = plugin;
        setupLanguageFiles();
        loadActiveLanguage();
    }

    /**
     * Ensures all default language files exist in the plugin's data folder.
     * If they don't exist, they are copied from the JAR resources.
     */
    private void setupLanguageFiles() {
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists() && !langDir.mkdirs()) {
            io.log.severe("Failed to create the 'lang' directory in the plugin folder!");
            return;
        }

        copyDefaultLangFile("en");
        copyDefaultLangFile("fr");

        io.log.info("Default language files copied or checked.");
    }

    /**
     * Helper method to copy a default language file if it doesn't exist.
     * @param langCode The language code (e.g., "en", "fr").
     */
    private void copyDefaultLangFile(String langCode) {
        String resourcePath = "lang/" + langCode + ".yml";
        File targetFile = new File(plugin.getDataFolder(), resourcePath);

        if (!targetFile.exists()) {
            // saveResource copies the file from the JAR to the data folder only if it doesn't exist
            plugin.saveResource(resourcePath, false);
            io.log.info("Copied default " + langCode + ".yml to the plugin folder.");
        }
    }

    /**
     * Loads the active language configuration file into memory.
     */
    private void loadActiveLanguage() {
        // TODO: should load the language preference from a main config file.
        File langFile = new File(plugin.getDataFolder(), "lang/" + activeLangCode + ".yml");

        if (langFile.exists()) {
            this.activeLangConfig = YamlConfiguration.loadConfiguration(langFile);
            io.log.info("Successfully loaded active language: " + activeLangCode);
        } else {
            io.log.severe("Active language file lang/" + activeLangCode + ".yml not found! Using hardcoded defaults.");
            // If the file is missing, the manager can't function correctly, but we'll try to continue.
        }
    }

    /**
     * Retrieves a message string from the active language configuration.
     * @param key The message key (e.g., "org.moniti.core.prefix").
     * @return The message string, or a fallback error message if the key is not found.
     */
    public String getMessage(String key) {
        if (activeLangConfig == null) {
            // Fallback if the language config failed to load entirely
            return io.toPlainText(io.toMM("<red>Error:</red> Language configuration not loaded."));
        }

        String message = activeLangConfig.getString(key);

        if (message == null || message.isEmpty()) {
            io.log.warn("Missing language key: " + key + " in " + activeLangCode + ".yml");
            return io.toPlainText(io.toMM("<red>[Missing Key: " + key + "]</red>"));
        }

        return message;
    }

    /**
     * Alias of getMessage(String key) .
     * Retrieves a message string from the active language configuration.
     * @param key The message key (e.g., "org.moniti.core.prefix").
     * @return The message string, or a fallback error message if the key is not found.
     */
    @SuppressWarnings("unused")
    public String getMsg(String key) { return getMessage(key); }
}
