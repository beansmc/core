package org.moniti.core.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.NotNull;

public class cc implements CommandExecutor {
    // The MiniMessage instance for deserializing text.
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        // --- Command Logic ---

        // Console Check (always good to have for player-specific commands)
        if (!(sender instanceof org.bukkit.entity.Player)) {
            Component consoleMessage = miniMessage.deserialize("<gold>The core command was executed by the console. Arguments: <yellow>" + String.join(" ", args));
            sender.sendMessage(consoleMessage);
            return true;
        }

        // Check for sub-commands (e.g., /core help)
        if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
            sendHelpMessage(sender);
            return true;
        }

        // A. Define the MiniMessage string for the main /core command.
        String miniMessageFormat =
            "<gray>[<gradient:dark_red:gold>CORE</gradient>]</gray> " +
            "Welcome to the <#55FF55>Core</#55FF55> Plugin! <br>" +
            "<hover:show_text:'<yellow>Click to view help!'> " +
            "<click:run_command:'/core help'>[<aqua>Help</aqua>]</click></hover>";

        // B. Deserialize the MiniMessage string into an Adventure Component object.
        Component finalMessage = miniMessage.deserialize(miniMessageFormat);

        // C. Send the Component to the Player
        sender.sendMessage(finalMessage);

        return true;
    }

    /**
     * Sends a detailed help message to the CommandSender using MiniMessage.
     * @param sender The entity to send the message to.
     */
    private void sendHelpMessage(@NotNull CommandSender sender) {
        String helpMessage =
                "<dark_gray>--- <gradient:gold:yellow>Core Plugin Help</gradient> ---<br>" +
                "<gold>/core</gold> <gray>- Shows the main plugin info.<br>" +
                "<gold>/core help</gold> <gray>- Displays this help menu.<br>" +
                "<gold>/core status</gold> <gray>- Check the plugin's status. (Command to be added later!)<br>" +
                "<gray>---------------------------";

        sender.sendMessage(miniMessage.deserialize(helpMessage));
    }
}
