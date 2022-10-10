package org.betonquest.betonquest.compatibility.holographicdisplays.lines;

import lombok.Getter;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.inventory.ItemStack;

/**
 * Displays an item instead of text.
 */
@Getter
public class ItemLine extends AbstractLine {
    /**
     * The item to be displayed with all its metadata
     */
    private final ItemStack item;

    /**
     * Creates a new instance of ItemLine.
     *
     * @param item Item to be displayed
     */
    public ItemLine(final ItemStack item) {
        super();
        this.item = item;
    }

    @Override
    public void addLine(final Hologram hologram) {
        hologram.getLines().appendItem(this.item);
    }
}
