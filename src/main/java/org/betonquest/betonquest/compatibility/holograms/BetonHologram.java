package org.betonquest.betonquest.compatibility.holograms;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Interface class to wrap a hologram from another supported plugin
 * When implementing this interface, you MUST declare a constructor with a String and Location parameter
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.TooManyMethods"})
public interface BetonHologram {

    /**
     * Add an item line to the bottom of this hologram
     */
    void appendLine(ItemStack item);

    /**
     * Add a text line to the bottom of this hologram
     */
    void appendLine(String text);

    /**
     * Replace a line at the index with a new item line
     */
    void setLine(int index, ItemStack item);

    /**
     * Replace a line at the index with a new text line
     */
    void setLine(int index, String text);

    /**
     * Insert an item line at an index. Lines at this index and below
     * will be shuffled down
     */
    void insertLine(int index, ItemStack item);

    /**
     * Insert a text line at an index. Lines at this index and below
     * will be shuffled down
     */
    void insertLine(int index, String text);

    /**
     * Remove the line at an index from this hologram
     *
     * @param index
     */
    void removeLine(int index);

    /**
     * Show this hologram to a player
     */
    void show(Player player);

    /**
     * Hides this hologram from a player
     */
    void hide(Player player);

    /**
     * Moves this hologram to specified location
     */
    void move(Location location);

    /**
     * Show this hologram to all players
     */
    void showAll();

    /**
     * Hides this hologram to all players
     */
    void hideAll();

    /**
     * Destroys and deletes this hologram
     */
    void delete();

    /**
     * Counts the amount of lines in this hologram when called
     *
     * @return the amount of lines
     */
    int size();

    /**
     * Clear all lines from this hologram
     */
    void clear();
}
