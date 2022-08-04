package org.betonquest.betonquest.compatibility.holograms;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Interface class to wrap a hologram from another supported plugin
 * When implementing this interface, you MUST declare a constructor with a String and Location parameter
 */
public interface BetonHologram {

    void appendLine(ItemStack item);

    void appendLine(String text);

    void setLine(int index, ItemStack item);

    void setLine(int index, String text);

    void insertLine(int index, ItemStack item);

    void insertLine(int index, String text);

    void removeLine(int index);

    void show(Player player);

    void hide(Player player);

    /**
     * Moves this hologram to specified location
     *
     * @param location
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
     * Sets the range at which a hologram becomes visible.
     *
     * @param range The range or 0 to show the hologram at all times
     */
    void setDisplayRange(int range);

    void setUpdateRange(int range);

    void delete();
}
