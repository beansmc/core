package org.moniti.core.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.key.Key;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class radio implements CommandExecutor {
    private final JavaPlugin plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    // State Management
    private final List<Material> musicDiscs = Arrays.asList(
            Material.MUSIC_DISC_13,
            Material.MUSIC_DISC_CAT,
            Material.MUSIC_DISC_BLOCKS,
            Material.MUSIC_DISC_CHIRP,
            Material.MUSIC_DISC_FAR,
            Material.MUSIC_DISC_MALL,
            Material.MUSIC_DISC_MELLOHI,
            Material.MUSIC_DISC_STAL,
            Material.MUSIC_DISC_STRAD,
            Material.MUSIC_DISC_WARD,
            Material.MUSIC_DISC_11,
            Material.MUSIC_DISC_WAIT,
            Material.MUSIC_DISC_PIGSTEP,
            Material.MUSIC_DISC_OTHERSIDE,
            Material.MUSIC_DISC_CREATOR,
            Material.MUSIC_DISC_CREATOR_MUSIC_BOX,
            Material.MUSIC_DISC_5,
            Material.MUSIC_DISC_RELIC,
            Material.MUSIC_DISC_PRECIPICE,
            Material.MUSIC_DISC_TEARS,
            Material.MUSIC_DISC_LAVA_CHICKEN
    );
    private final Random random = new Random();

    // Per-player setting to track if notifications are hidden
    private final Set<UUID> hiddenNotifications = new HashSet<>();

    // Global state for simplicity, tracking the currently playing disc
    private Material currentDisc = musicDiscs.get(0); // Start with the first disc (13)

    // Scheduled task ID for continuous random playback
    private int playbackTaskId = -1;

    // Constructor to receive the main plugin instance for scheduling tasks
    public radio(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        // Only players can interact with the radio/music
        if (!(sender instanceof Player)) {
            sender.sendMessage(miniMessage.deserialize("<red>Only players can use the radio commands.</red>"));
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelpMessage(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "play":
                handlePlayCommand(player, args);
                break;
            case "hide":
                handleHideCommand(player);
                break;
            case "show":
                handleShowCommand(player);
                break;
            default:
                sendHelpMessage(player);
                break;
        }
        return true;
    }

    // --- Sub-Command Handlers ---

    private void handlePlayCommand(@NotNull Player player, String[] args) {
        // Stop any existing random playback task
        if (playbackTaskId != -1) {
            Bukkit.getScheduler().cancelTask(playbackTaskId);
            playbackTaskId = -1;
        }

        Material discToPlay;

        if (args.length > 1) {
            // /radio play {disc name}
            String discName = args[1].toUpperCase().replace('-', '_');

            // Allow matching based on common names like "MUSIC_DISC_CREATOR_MUSIC_BOX"
            // but also handle abbreviations like "CREATOR"
            try {
                // Find a matching disc (e.g., /radio play pigstep)
                discToPlay = musicDiscs.stream()
                        .filter(disc -> disc.name().contains(discName))
                        .findFirst()
                        .orElse(null);

                if (discToPlay == null) {
                    player.sendMessage(miniMessage.deserialize("<red>Could not find a music disc matching '<yellow>" + args[1] + "</yellow>'.</red>"));
                    return;
                }

                // Play the specific disc once
                playDisc(player, discToPlay);
                player.sendMessage(miniMessage.deserialize("<green>Playing specific disc: <gold>" + discToPlay.name().replace("MUSIC_DISC_", "") + "</gold>!</green>"));

            } catch (Exception e) {
                player.sendMessage(miniMessage.deserialize("<red>Invalid disc name provided.</red>"));
            }
        } else {
            // /radio play (random, continuous)
            player.sendMessage(miniMessage.deserialize("<green>Starting random radio playback!</green> <gray>(Use /radio play to restart)</gray>"));

            // Start the continuous random playback task
            startRandomPlayback(player);
        }
    }

    private void handleHideCommand(@NotNull Player player) {
        if (hiddenNotifications.add(player.getUniqueId())) {
            player.sendMessage(miniMessage.deserialize("<gray>Radio notifications are now <red>hidden</red>.</gray>"));
        } else {
            player.sendMessage(miniMessage.deserialize("<gray>Radio notifications were already <red>hidden</red>.</gray>"));
        }
    }

    private void handleShowCommand(@NotNull Player player) {
        if (hiddenNotifications.remove(player.getUniqueId())) {
            player.sendMessage(miniMessage.deserialize("<gray>Radio notifications are now <green>enabled</green>.</gray>"));
        } else {
            player.sendMessage(miniMessage.deserialize("<gray>Radio notifications were already <green>enabled</green>.</gray>"));
        }

        // Show current song when notifications are re-enabled
        sendNowPlayingNotification(player, currentDisc);
    }

    // --- Playback and Scheduling Logic ---

    private void startRandomPlayback(@NotNull Player player) {
        // Task runs every 1200 ticks (60 seconds) or the duration of a typical song
        // Note: The actual length of discs varies, so this is just an average.
        playbackTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            Material nextDisc = musicDiscs.get(random.nextInt(musicDiscs.size()));
            playDisc(player, nextDisc);
            sendNowPlayingNotification(player, nextDisc); // Notify on new song
        }, 0L, 1200L); // Start immediately, repeat every 60 seconds

        if (playbackTaskId == -1) {
            player.sendMessage(miniMessage.deserialize("<red>Failed to start radio playback scheduler!</red>"));
        }
    }

    private void playDisc(@NotNull Player player, @NotNull Material disc) {
        currentDisc = disc;

        // Stops all previous music for the player in the RECORDS category (all music discs)
        player.stopSound(SoundCategory.RECORDS);

        // Construct the namespaced key (e.g., "music_disc_pigstep") by converting the Material name to lowercase.
        String soundKeyName = disc.name().toLowerCase();

        // FIX: Bukkit.getRegistry expects the class type (Sound.class) for generic resolution, not the enum constant.
        Sound sound = Bukkit.getRegistry(Sound.class).get(Key.key(soundKeyName));

        if (sound == null) {
            // No fallback: If the sound cannot be found, just notify the player and stop.
            player.sendMessage(miniMessage.deserialize("<red>Error: Could not find sound for disc <gold>" + disc.name().replace("MUSIC_DISC_", "") + "</gold>.</red>"));
            return;
        }

        // Play the sound at a comfortable volume
        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
    }

    /**
     * Sends the actionbar notification with dancing notes and a 2-second fade-out.
     * @param player The player to notify.
     * @param disc The disc that is currently playing.
     */
    private void sendNowPlayingNotification(@NotNull Player player, @NotNull Material disc) {
        if (hiddenNotifications.contains(player.getUniqueId())) {
            return; // Notification is hidden for this player
        }

        String discName = disc.name().replace("MUSIC_DISC_", "");

        // The message with dancing notes (using MiniMessage tag for the dancing effect)
        String messageTemplate =
                "<gradient:#F70E4D:#FFA200>♪</gradient> <yellow>Now Playing:</yellow> <gold><disc_name></gold> <gradient:#FFA200:#F70E4D>♫</gradient>";

        Component playingMessage = miniMessage.deserialize(
                messageTemplate,
                Placeholder.unparsed("disc_name", discName)
        );

        // 1. Send the message immediately
        player.sendActionBar(playingMessage);

        // 2. Schedule a blank actionbar to clear it after 2 seconds (40 ticks)
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.sendActionBar(Component.empty());
        }, 40L); // 2 seconds * 20 ticks/second = 40 ticks
    }


    // --- Help and Info Messages ---

    private void sendHelpMessage(@NotNull CommandSender sender) {
        String helpMessage =
                "<br><br><shadow:black>              <dark_gray>---</dark_gray> <gray>/</gray><gradient:#F70E4D:#F12760>Radio</gradient> help <dark_gray>---</dark_gray> </shadow><br>" +
                        "<shadow:black>                <gray>/</gray><gradient:#F70E4D:#F12760>Radio</gradient> play <gray>{disc name}</shadow><br>" +
                        "         Plays a specific disc once.<br>" +
                        "<shadow:black>                <gray>/</gray><gradient:#F70E4D:#F12760>Radio</gradient> play </shadow><br>" +
                        "         Starts continuous random playback.<br>" +
                        "<shadow:black>                <gray>/</gray><gradient:#F70E4D:#F12760>Radio</gradient> hide </shadow><br>" +
                        "         Hides the song notification in the action bar.<br>" +
                        "<shadow:black>                <gray>/</gray><gradient:#F70E4D:#F12760>Radio</gradient> show </shadow><br>" +
                        "         Shows the song notification (and displays current song).<br>" +
                        "  <br>           <shadow:black> <dark_gray>---------------------------</dark_gray> </shadow><br><br>";

        sender.sendMessage(miniMessage.deserialize(helpMessage));
    }
}
