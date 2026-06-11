package org.betonquest.betonquest.compatibility.holograms.lines;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;

/**
 * Displays an item instead of text.
 */
public class ItemLine extends AbstractLine {

    /**
     * The item to be displayed with all its metadata.
     */
    private final ItemWrapper item;

    /**
     * Creates a new instance of ItemLine.
     *
     * @param item Item to be displayed
     */
    public ItemLine(final ItemWrapper item) {
        super(true, 1);
        this.item = item;
    }

    @Override
    public void setLine(final BetonHologram hologram, final int index) throws QuestException {
        hologram.setLine(index, this.item.generate(null));
    }
}
