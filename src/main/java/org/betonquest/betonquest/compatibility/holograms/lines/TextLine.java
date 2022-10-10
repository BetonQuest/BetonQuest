package org.betonquest.betonquest.compatibility.holograms.lines;

import lombok.Getter;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;

/**
 * Displays a simple text line with optional color codes
 */
@Getter
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
        super();
        this.text = text;
    }

    @Override
    public void addLine(final BetonHologram hologram) {
        hologram.appendLine(this.text);
    }
}
