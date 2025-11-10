package org.moniti.core.utils.gui;

import org.moniti.core.Core;
import org.moniti.core.utils.io;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.Listener;

import net.kyori.adventure.text.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Creates a fake Anvil GUI for a player to input text.
 * Since Paper/Bukkit does not have a native API for this, we use the AnvilInventory and monitor clicks.
 */
public class anvilInput implements Listener {
    private final Player player;
    private final CompletableFuture<String> futureResult;
    private final Inventory inventory;

    /**
     * Creates and opens the Anvil GUI for text input.
     * Returns a CompletableFuture that completes with the user's input string.
     * @param player The player to open the GUI for.
     * @param prompt The text displayed in the input field (Anvil's repair slot).
     */
    public anvilInput(Player player, String prompt) {
        this.player = player;
        this.futureResult = new CompletableFuture<>();

        Component titleComponent = io.MM_SERIALIZER.deserialize("<dark_gray>Input</dark_gray>");
        this.inventory = Bukkit.createInventory(null, InventoryType.ANVIL, titleComponent);

        ItemStack inputItem = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = inputItem.getItemMeta();

        meta.displayName(io.MM_SERIALIZER.deserialize(prompt));
        inputItem.setItemMeta(meta);

        this.inventory.setItem(0, inputItem);

        Bukkit.getPluginManager().registerEvents(this, Core.getInstance());
    }

    /**
     * Opens the anvil inventory to the player and returns the future result.
     * @return A CompletableFuture for the resulting string.
     */
    public CompletableFuture<String> open() {
        player.openInventory(this.inventory);
        return this.futureResult;
    }

    /**
     * Cleans up the listener and forces the future to complete with a given result.
     * * @param result The input result, or null if cancelled.
     */
    private void cleanup(String result) {
        HandlerList.unregisterAll(this);
        // Safely complete the future
        if (result != null) {
            futureResult.complete(result);
        } else if (!futureResult.isDone()) {
            futureResult.complete(null);
        }
    }

    /**
     * Handles clicks within the Anvil GUI.
     * We only care about the result slot (slot 2)
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(this.inventory)) return;

        // If the inventory is an Anvil, the result slot is 2
        if (event.getRawSlot() == 2) {
            event.setCancelled(true); // Prevent item removal

            ItemStack resultItem = event.getCurrentItem();
            if (resultItem == null || !resultItem.hasItemMeta()) return;

            // Get the name of the repaired/renamed item (which is the user's input)
            // Use the utility method in io.java to extract the raw text from the component.
            Component displayName = resultItem.getItemMeta().displayName();
            String input = (displayName != null) ? io.toPlainText(displayName) : "";

            player.closeInventory();
            cleanup(input);
        }
    }

    /**
     * Handles the player closing the inventory without accepting the result.
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getInventory().equals(this.inventory)) return;

        if (!futureResult.isDone()) {
            cleanup(null);
        }
    }
}
