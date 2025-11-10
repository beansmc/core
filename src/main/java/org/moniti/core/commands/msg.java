package org.moniti.core.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements a custom `/msg` (or `/tell`, /w) command to override the server default `/msg` command
 */
public class msg implements CommandExecutor, TabCompleter {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(miniMessage.deserialize("<red>Usage: /<command> <player> <message></red>"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);

        if (target == null || !target.isOnline()) {
            sender.sendMessage(miniMessage.deserialize("<red>Player '<player>' not found or is offline.</red>",
                    Placeholder.unparsed("player", args[0])));
            return true;
        }

        String messageContent = String.join(" ", args).substring(args[0].length()).trim();

        // Message to the sender
        sender.sendMessage(miniMessage.deserialize(
                "<gray>To <yellow><target></yellow>: <white><message></white>",
                Placeholder.unparsed("target", target.getName()),
                Placeholder.unparsed("message", messageContent)
        ));

        // Message to the receiver (target)
        target.sendMessage(miniMessage.deserialize(
                "<gray>From <yellow><sender></yellow>: <white><message></white>",
                Placeholder.unparsed("sender", sender.getName()),
                Placeholder.unparsed("message", messageContent)
        ));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            // Suggest online player names for the first argument (target player)
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
