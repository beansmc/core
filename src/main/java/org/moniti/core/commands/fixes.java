package org.moniti.core.commands;

import org.moniti.core.utils.*;
import org.moniti.core.fix.fly;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class fixes {
    private final JavaPlugin plugin;
    private final Map<String, CommandExecutor> commandExecutors = new HashMap<>();

    public fixes(JavaPlugin plugin) {
        this.plugin = plugin;
        registerFixes();
    }

    /**
     * Initializes and maps all command instances from the `org.moniti.core.fix` package.
     * All commands must be defined in plugin.yml still.
     */
    private void registerFixes() {
        // `/flyspeed` command
        fly flyCommand = new fly();
        commandExecutors.put("flyspeed", flyCommand);

        Objects.requireNonNull(plugin.getCommand("flyspeed")).setTabCompleter(flyCommand);
    }

    /**
     * Registers all command executors with the server.
     */
    public void registerAll() {
        log.info("Registering " + commandExecutors.size() + " fix/utility commands...");
        for (Map.Entry<String, CommandExecutor> entry : commandExecutors.entrySet()) {
            String commandName = entry.getKey();
            CommandExecutor executor = entry.getValue();

            // Register the executor for the command defined in plugin.yml
            command.register(plugin, commandName, executor);

            log.info("    - Registered /" + commandName);
        }
    }
}
