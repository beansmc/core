package org.moniti.core.utils;

import org.moniti.core.Core;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A utility class to simplify logging throughout the plugin.
 * It uses the Logger from the main Core plugin instance.
 * All methods are static for easy access (e.g., log.info("message");).
 */
public final class log {
    // Private constructor to prevent instantiation of a utility class
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
    @SuppressWarnings("unused")
    public static void info(String message) {
        getLogger().log(Level.INFO, message);
    }

    /**
     * Logs a WARNING message. (Used for non-fatal issues or deprecations)
     * @param message The message to log.
     */
    @SuppressWarnings("unused")
    public static void warn(String message) {
        getLogger().log(Level.WARNING, message);
    }

    /**
     * Logs a SEVERE message. (Used for errors and critical failures)
     * @param message The message to log.
     */
    @SuppressWarnings("unused")
    public static void severe(String message) {
        getLogger().log(Level.SEVERE, message);
    }
}
