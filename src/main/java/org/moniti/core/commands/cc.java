package org.moniti.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import org.jetbrains.annotations.NotNull;

public class cc implements CommandExecutor {
    // The MiniMessage instance for deserializing text.
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        // --- Command Logic ---

        // Console Check (always good to have for player-specific commands)
        if (args.length == 0 && !(sender instanceof Player)) {
            // Allow console to see the main message
            sendMainMessage(sender);
            return true;
        }

        // Handle sub-commands (e.g., /core help)
        if (args.length > 0) {
            String subCommand = args[0].toLowerCase();

            // 1. /core help
            if (subCommand.equals("help")) {
                sendHelpMessage(sender);
                return true;
            }

            // 2. /core player {player}
            else if (subCommand.equals("player")) {
                if (args.length < 2) {
                    sender.sendMessage(miniMessage.deserialize("Usage: /core player {player}"));
                    return true;
                }
                String targetPlayerName = args[1];
                sendPlayerInfoMessage(sender, targetPlayerName);
                return true;
            }
        }

        // Default action (no subcommand or unrecognized subcommand)
        sendMainMessage(sender);

        return true;
    }

    /**
     * Sends a detailed main message to the CommandSender using MiniMessage.
     * @param sender The entity to send the message to.
     */
    private void sendMainMessage(@NotNull CommandSender sender) {
        String mainMessage =
                "<br><br><shadow:black>                     <dark_gray>---</dark_gray> <gray>/</gray><gradient:#F70E4D:#F12760>Core</gradient> <dark_gray>---</dark_gray> </shadow><br>       The main <shadow:black><gradient:#F95A22:#FFA200>Beans</gradient><gray>-</gray><color:#D4DDE0>m</color><color:#dead50>c</color></shadow> server <shadow:black><gradient:#F70E4D:#F12760>Core</gradient></shadow> plugin.<br>                    <shadow:black> <dark_gray>------------</dark_gray> </shadow><br><shadow:black>                   <gray>/</gray><gradient:#F70E4D:#F12760>Core</gradient> help <gray>{page} </shadow><br><shadow:black>                <gray>/</gray><gradient:#F70E4D:#F12760>Core</gradient> player <gray>{player} </shadow><br><br>";

        sender.sendMessage(miniMessage.deserialize(mainMessage));
    }

    /**
     * Sends a detailed help message to the CommandSender using MiniMessage.
     * @param sender The entity to send the message to.
     */
    private void sendHelpMessage(@NotNull CommandSender sender) {
        String helpMessage =
                "<br><br><shadow:black>              <dark_gray>---</dark_gray> <gray>/</gray><gradient:#F70E4D:#F12760>Core</gradient> help <gray>{page}</gray> <dark_gray>---</dark_gray> </shadow><br><br><shadow:black>                <gray>/</gray><gradient:#F70E4D:#F12760>Core</gradient> player <gray>{player} </shadow><br>         Shows info about a specific player.<br>  <br>           <shadow:black> <dark_gray>----------------------</dark_gray> </shadow><br><br><br>";

        sender.sendMessage(miniMessage.deserialize(helpMessage));
    }

    /**
     * Sends player information to the CommandSender using MiniMessage.
     * This demonstrates fetching basic player data and using MiniMessage placeholders.
     * @param sender The entity receiving the message.
     * @param targetPlayerName The name of the player to look up.
     */
    private void sendPlayerInfoMessage(@NotNull CommandSender sender, @NotNull String targetPlayerName) {
        Player target = Bukkit.getPlayer(targetPlayerName);

        if (target == null) {
            sender.sendMessage(miniMessage.deserialize("Player <yellow>" + targetPlayerName + "</yellow> is <red>not</red> currently online."));
            return;
        }

        String nameMCUrl = "https://namemc.com/profile/" + target.getName();

        Component clickableNameComponent = Component.text(target.getName())
                .color(NamedTextColor.GOLD)
                .clickEvent(ClickEvent.openUrl(nameMCUrl))
                .hoverEvent(HoverEvent.showText(
                        miniMessage.deserialize("<gray>Click to view <yellow>" + target.getName() + "</yellow>'s NameMC profile")
                ));

        String infoMessage =
                "<br><br><shadow:black>           <dark_gray>---</dark_gray> <gray>/</gray><gradient:#F70E4D:#F12760>Core</gradient> player <gray>{player}</gray> <dark_gray>---</dark_gray> </shadow><br><shadow:black>                     <yellow><player_name></yellow> </shadow><br><br>                     <player_hp><red>♥</red><br>                  <player_location><blue>✈</blue><br><br>         <shadow:black> <dark_gray>---------------------------</dark_gray> </shadow><br><br><br>";

        Component message = miniMessage.deserialize(
                infoMessage,

                Placeholder.component("player_name", clickableNameComponent),
                Placeholder.unparsed("player_uuid", target.getUniqueId().toString()),
                Placeholder.unparsed("player_hp", String.format("%.1f", target.getHealth())),
                Placeholder.unparsed("player_location", String.format("%s (%.0f, %.0f, %.0f)",
                        target.getWorld().getName(),
                        target.getLocation().getX(),
                        target.getLocation().getY(),
                        target.getLocation().getZ()
                ))
        );

        sender.sendMessage(message);
    }
}
