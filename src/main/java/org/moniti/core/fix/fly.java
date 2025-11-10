package org.moniti.core.fix;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements the `/flyspeed` command to allow players to set their flight speed.
 * This class serves as a simple replacement for the EssentialsX flyspeed command.
 */
public class fly implements CommandExecutor, TabCompleter {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final float MIN_SPEED = 0.1f;
    private static final float MAX_SPEED = 10.0f;
    private static final float DEFAULT_SPEED = 1.0f;
    private static final float BUKKIT_DEFAULT_SPEED = DEFAULT_SPEED / 10.0f;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        // Player check
        if (!(sender instanceof Player player)) {
            sender.sendMessage(miniMessage.deserialize("<red>Only players can use this command.</red>"));
            return true;
        }

        // Permission check
        if (!player.hasPermission("core.command.flyspeed")) {
            player.sendMessage(miniMessage.deserialize("<red>You do not have permission to use this command.</red>"));
            return true;
        }

        if (args.length == 0) {
            // Show current speed
            float currentSpeed = player.getFlySpeed() * 10.0f;
            player.sendMessage(miniMessage.deserialize(
                    "<dark_gray>[</dark_gray><gradient:#F70E4D:#F12760>Core</gradient><dark_gray>] </dark_gray>Your current fly speed is <yellow><speed></yellow>.",
                    Placeholder.unparsed("speed", String.format("%.1f", currentSpeed))
            ));
            return true;
        }

        if (args[0].equalsIgnoreCase("default")) {
            // Apply Bukkit's default speed of 0.1f (1.0 for players)
            player.setFlySpeed(BUKKIT_DEFAULT_SPEED);
            player.sendMessage(miniMessage.deserialize(
                    "<dark_gray>[</dark_gray><gradient:#F70E4D:#F12760>Core</gradient><dark_gray>] </dark_gray>Fly speed reset to <yellow><speed></yellow> (default).",
                    Placeholder.unparsed("speed", String.format("%.1f", DEFAULT_SPEED))
            ));
            syncFlightState(player);
            return true;
        }

        // Parse speed argument
        float speedValue;
        try {
            // Player inputs 1-10, Bukkit uses 0.1-1.0 (multiplied by 10 to get user-friendly value)
            speedValue = Float.parseFloat(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(miniMessage.deserialize("<red>Invalid speed value. Must be a number.</red>"));
            return false;
        }

        // Bounds check
        if (speedValue < MIN_SPEED || speedValue > MAX_SPEED) {
            player.sendMessage(miniMessage.deserialize(
                    "<red>Speed must be between <min_speed> and <max_speed>.</red>",
                    Placeholder.unparsed("min_speed", String.format("%.1f", MIN_SPEED)),
                    Placeholder.unparsed("max_speed", String.format("%.1f", MAX_SPEED))
            ));
            return true;
        }

        // Apply speed
        float bukkitSpeed = speedValue / 10.0f;
        player.setFlySpeed(bukkitSpeed);

        player.sendMessage(miniMessage.deserialize(
                "<dark_gray>[</dark_gray><gradient:#F70E4D:#F12760>Core</gradient><dark_gray>] </dark_gray>Fly speed set to <yellow><speed></yellow>.",
                Placeholder.unparsed("speed", String.format("%.1f", speedValue))
        ));

        syncFlightState(player);

        return true;
    }

    /**
     * Toggles the player's flying state to force the client and server to resynchronize
     * allowed movement speed, which prevents console spam warnings on high fly speeds.
     * This is a standard workaround for the "moved too quickly" error when setting custom speeds.
     * @param player The player to synchronize.
     */
    private void syncFlightState(Player player) {
        // Only sync if the player is currently flying
        if (player.isFlying()) {
            player.setFlying(false);
            player.setFlying(true);
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            // Numerical speed suggestions
            List<Integer> speedSuggestions = List.of(1, 2, 5, 10);

            // Sort the suggestions numerically in descending order (10, 5, 2, 1)
            List<String> sortedSuggestions = speedSuggestions.stream()
                    .sorted(Comparator.reverseOrder())
                    .map(Object::toString)
                    .collect(Collectors.toCollection(ArrayList::new));

            sortedSuggestions.addFirst("default");
            return sortedSuggestions.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
