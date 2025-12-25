package org.betonquest.betonquest.compatibility.holograms.lines;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;

/**
 * Displays a simple text line with optional color codes.
 */
public class TextLine extends AbstractLine {

    /**
     * Text to be displayed.
     */
    private final Component text;

    /**
     * Creates a new instance of TextLine.
     *
     * @param text Text to be displayed
     */
    public TextLine(final Component text) {
        super(true, 1);
        this.text = text;
    }

    @Override
    public void setLine(final BetonHologram hologram, final int index) {
        hologram.setLine(index, text);
    }
}
