package org.betonquest.betonquest.compatibility.holograms.lines;

import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.bukkit.inventory.ItemStack;

/**
 * Displays an item instead of text.
 */
public class ItemLine extends AbstractLine {

    /**
     * The item to be displayed with all its metadata.
     */
    private final ItemStack item;

    /**
     * Creates a new instance of ItemLine.
     *
     * @param item Item to be displayed
     */
    public ItemLine(final ItemStack item) {
        super(true, 1);
        this.item = item;
    }

    @Override
    public void setLine(final BetonHologram hologram, final int index) {
        hologram.setLine(index, this.item);
    }
}
