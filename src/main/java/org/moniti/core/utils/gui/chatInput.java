package org.moniti.core.utils.gui;

import org.moniti.core.Core;
import org.moniti.core.utils.io;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.concurrent.CompletableFuture;

/**
 * Listens for the next chat message from a specific player to capture text input.
 * This listener automatically unregisters itself after receiving the input.
 */
public class chatInput implements Listener {
    private final Player player;
    private final CompletableFuture<String> futureResult;
    private final String promptMessage;

    /**
     * Initializes the chat input listener.
     * @param player The player to listen to.
     * @param prompt The message to send to the player asking for input.
     */
    public chatInput(Player player, String prompt) {
        this.player = player;
        this.futureResult = new CompletableFuture<>();
        this.promptMessage = prompt;
    }

    /**
     * Sends the prompt message to the player and registers the listener.
     * @return A CompletableFuture that completes with the user's input string, or null if cancelled.
     */
    public CompletableFuture<String> open() {
        // Send the prompt message using the IO utility's MiniMessage capabilities
        io.mm(player, promptMessage);
        io.mm(player, "<gray>Type your message in chat. Type '<red>cancel</red>' to exit.</gray>");

        // Register the listener to capture the next chat message
        Bukkit.getPluginManager().registerEvents(this, Core.getInstance());
        return this.futureResult;
    }

    /**
     * Cleans up the listener and completes the future.
     * @param result The input result, or null if cancelled.
     */
    private void cleanup(String result) {
        HandlerList.unregisterAll(this);
        if (!futureResult.isDone()) {
            futureResult.complete(result);
        }
    }

    @EventHandler
    // Using the modern AsyncChatEvent as requested
    public void onPlayerChat(AsyncChatEvent event) {
        // Check if the event is from the player we are listening for
        if (!event.getPlayer().equals(this.player)) return;

        // We capture the event and prevent it from being broadcasted
        // Cancelling AsyncChatEvent prevents it from being broadcasted in chat
        event.setCancelled(true);

        // Use PlainTextComponentSerializer to get the raw String input from the Component
        String input = PlainTextComponentSerializer.plainText().serialize(event.message()).trim();

        // Check for cancellation
        if (input.equalsIgnoreCase("cancel")) {
            io.mm(player, "Input <red>cancelled</red>.");
            cleanup(null);
            return;
        }

        io.mm(player, "<green>Received</green>.");

        // Clean up and complete the future with the captured input
        cleanup(input);
    }
}
