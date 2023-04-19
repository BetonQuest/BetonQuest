package org.betonquest.betonquest.compatibility.holograms.lines;

import org.betonquest.betonquest.compatibility.holograms.BetonHologram;

/**
 * Displays a simple text line with optional color codes
 */
public class TextLine extends AbstractLine {
    /**
     * Text to be displayed.
     */
    private final String text;

    /**
     * Creates a new instance of TextLine.
     *
     * @param text Text to be displayed
     */
    public TextLine(final String text) {
        super(true, 1);
        this.text = text;
    }

    @Override
    public void setLine(final BetonHologram hologram, final int index) {
        hologram.setLine(index, text);
    }
}
