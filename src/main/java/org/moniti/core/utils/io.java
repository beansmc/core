package org.moniti.core.utils;

import org.moniti.core.Core;
import org.moniti.core.utils.gui.*;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Global Input/Output (IO) Utility API.
 * Provides simple methods for chat messages, logging, and GUI input.
 */
public final class io {
    // MiniMessage instance for MM-formatted text
    public static final MiniMessage MM_SERIALIZER = MiniMessage.miniMessage();
    // Legacy serializer for &c-style codes
    public static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder().character('&').build();
    // Plain text serializer instance
    public static final PlainTextComponentSerializer PT_SERIALIZER = PlainTextComponentSerializer.plainText();

    // Private constructor to prevent instantiation of a utility class
    private io() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static final class locale {
        private locale() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
        }

        /**
         * Retrieves a localized message string from the active language file using a key.
         * @param key The message key (e.g., "org.moniti.core.prefix").
         * @return The localized message string, or a fallback if the key is missing.
         */
        @SuppressWarnings("unused")
        public static String getMsg(String key) {
            return Core.getInstance().getLangManager().getMessage(key);
        }
    }

    public enum inputType {
        ANVIL,
        CHAT
    }

    /**
     * Opens an input method (defaults to Chat) to get text input from a player.
     * This is the default alias, defaulting to the less intrusive Chat input.
     * @param player The player to prompt.
     * @param prompt The initial text or prompt message.
     * @return A CompletableFuture that completes with the user's input string, or null if cancelled.
     */
    @SuppressWarnings("unused")
    public static CompletableFuture<String> input(Player player, String prompt) {
        return input(inputType.CHAT, player, prompt);
    }

    /**
     * Opens a specific input method (Anvil or Chat) to get text input from a player.
     * @param player The player to prompt.
     * @param prompt The initial text or prompt message.
     * @param type The desired input method (ANVIL or CHAT).
     * @return A CompletableFuture that completes with the user's input string, or null if cancelled.
     */
    @SuppressWarnings("unused")
    public static CompletableFuture<String> input(inputType type, Player player, String prompt) {
        if (type == inputType.ANVIL) {
            anvilInput anvil = new anvilInput(player, prompt);
            return anvil.open();
        } else {
            chatInput chat = new chatInput(player, prompt);
            return chat.open();
        }
    }

    /**
     * Converts a MiniMessage string into a Kyori Adventure Component.
     * @param message The MiniMessage formatted string (e.g., "<red>Hello</red>").
     * @return The resulting Adventure Component.
     */
    @SuppressWarnings("unused")
    public static Component toMM(String message) {
        return MM_SERIALIZER.deserialize(message);
    }

    /**
     * Converts a Legacy formatted string (using '&' color codes) into a Kyori Adventure Component.
     * @param message The Legacy formatted string (e.g., "&cHello").
     * @return The resulting Adventure Component.
     */
    @SuppressWarnings("unused")
    public static Component toLegacy(String message) {
        return LEGACY_SERIALIZER.deserialize(message);
    }

    /**
     * Converts a Kyori Adventure Component to its raw, unformatted text string.
     * @param component The Adventure Component to serialize.
     * @return The raw text string.
     */
    @SuppressWarnings("unused")
    public static String toPlainText(Component component) {
        return PT_SERIALIZER.serialize(component).trim();
    }

    /**
     * This is an alias of toPlainText(Component component) .
     * Converts a Kyori Adventure Component to its raw, unformatted text string.
     * @param component The Adventure Component to serialize.
     * @return The raw text string.
     */
    @SuppressWarnings("unused")
    public static String toPT(Component component) { return toPlainText(component); }

    /**
     * Contains all static methods related to sending chat messages.
     */
    public static final class send {
        /**
         * Sends a message with default Minecraft (&c) color codes (Legacy).
         * @param sender The recipient (Player or Console).
         * @param message The message string.
         */
        public send(CommandSender sender, String message) {
            Component component = LEGACY_SERIALIZER.deserialize(message);
            sender.sendMessage(component);
        }

        /**
         * Sends a message with default Minecraft (&c) color codes (Legacy).
         * @param sender The recipient (Player or Console).
         * @param message The message string.
         */
        public static void legacy(CommandSender sender, String message) {
            Component component = LEGACY_SERIALIZER.deserialize(message);
            sender.sendMessage(component);
        }

        public static void mm(CommandSender sender, String message) {
            Component component = MM_SERIALIZER.deserialize(message);
            sender.sendMessage(component);
        }
    }

    /**
     * Alias for io.send.legacy(sender, message). Sends messages with default Minecraft (&c) color codes.
     */
    @SuppressWarnings("unused")
    public static void legacy(CommandSender sender, String message) { send.legacy(sender, message); }

    /**
     * Alias for io.send.mm(sender, message). Sends messages with MiniMessage (<red>) color codes.
     */
    @SuppressWarnings("unused")
    public static void mm(CommandSender sender, String message) { send.mm(sender, message); }

    @SuppressWarnings("unused")
    public static final class chat {
        /**
         * Alias for io.send.legacy(sender, message). Sends messages with default Minecraft (&c) color codes.
         */
        public chat(CommandSender sender, String message) { send.legacy(sender, message); }

        /**
         * Sends a message with MiniMessage color codes.
         * @param sender The recipient (Player or Console).
         * @param message The message string.
         */
        public static void mm(CommandSender sender, String message) { send.mm(sender, message); }

        /**
         * Clears the player's chat window.
         * @param player The player whose chat to clear.
         */
        public static void clear(Player player) {
            // Sends 100 empty lines to push existing chat off the screen.
            for (int i = 0; i < 250; i++) {
                player.sendMessage(Component.empty());
            }
        }
    }

    /**
     * A logging API to simplify logging throughout the plugin.
     * It uses the Logger from the main Core plugin instance.
     * All methods are static for easy access (e.g., io.log.info("message");).
     */
    @SuppressWarnings("unused")
    public static final class log {
        private log() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
        }

        /**
         * Gets the main plugin logger instance.
         * @return The Java Logger object for the Core plugin.
         */
        private static Logger getLogger() {
            // Use the Singleton pattern to safely retrieve the plugin instance and its logger.
            // If Core.getInstance() returns null (which should only happen if used before onEnable),
            // it falls back to a console-level logger.
            Core plugin = Core.getInstance();
            return (plugin != null) ? plugin.getLogger() : Logger.getLogger("Core(Uninitialized)");
        }

        /**
         * Logs an INFO message. (Used for general, non-critical output)
         * @param message The message to log.
         */
        public static void info(String message) {
            getLogger().log(Level.INFO, message);
        }

        /**
         * Logs a WARNING message. (Used for non-fatal issues or deprecations)
         * @param message The message to log.
         */
        public static void warn(String message) {
            getLogger().log(Level.WARNING, message);
        }

        /**
         * Logs a SEVERE message. (Used for errors and critical failures)
         * @param message The message to log.
         */
        public static void severe(String message) {
            getLogger().log(Level.SEVERE, message);
        }
    }
}
