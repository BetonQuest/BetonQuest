package org.betonquest.betonquest.compatibility.holograms;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Interface class to wrap a hologram from another supported plugin.
 * Instances of this class should only be created by {@link HologramIntegrator#createHologram(Location)}.
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface BetonHologram {

    /**
     * Add an item line to the bottom of this hologram.
     *
     * @param item The item to display and append.
     */
    void appendLine(ItemStack item);

    /**
     * Add a text line to the bottom of this hologram.
     *
     * @param text The text to append.
     */
    void appendLine(String text);

    /**
     * Replace a line at the index with a new item line. Will throw an exception if index is out of bounds.
     *
     * @param index The index at which the line is set.
     * @param item  The item to place at the line.
     */
    void setLine(int index, ItemStack item);

    /**
     * Replace a line at the index with a new text line. Will throw an exception if index is out of bounds.
     *
     * @param index The index at which the line is set.
     * @param text  The text to place at the line.
     */
    void setLine(int index, String text);

    /**
     * Creates multiple lines from the starting index and adding a specified amount of lines.
     * If a line at an index already exists, then a line will not be created.
     *
     * @param startingIndex  the starting index
     * @param linesToBeAdded the amount of lines to add
     */
    void createLines(int startingIndex, int linesToBeAdded);

    /**
     * Remove the line at an index from this hologram. May throw an exception if index is out of bounds.
     *
     * @param index The index of the line to remove
     */
    void removeLine(int index);

    /**
     * Show this hologram to a player.
     *
     * @param player The player to show the hologram to.
     */
    void show(Player player);

    /**
     * Hides this hologram from a player.
     *
     * @param player The player to hide the hologram from.
     */
    void hide(Player player);

    /**
     * Moves this hologram to specified location.
     *
     * @param location The location to move this hologram to.
     */
    void move(Location location);

    /**
     * Show this hologram to all players.
     */
    void showAll();

    /**
     * Hides this hologram to all players.
     */
    void hideAll();

    /**
     * Destroys and deletes this hologram.
     */
    void delete();

    /**
     * Whether the hologram is disabled or not.
     *
     * @return true if disabled, false otherwise.
     */
    boolean isDisabled();

    /**
     * Disables the hologram without deleting it.
     */
    void disable();

    /**
     * Enables the hologram after it has been disabled.
     */
    void enable();

    /**
     * Counts the amount of lines in this hologram when called.
     *
     * @return the amount of lines.
     */
    int size();

    /**
     * Retrieves the location of the hologram.
     *
     * @return the location of the hologram.
     */
    Location getLocation();

    /**
     * Clear all lines from this hologram.
     */
    void clear();
}
