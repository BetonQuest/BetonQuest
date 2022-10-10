package org.betonquest.betonquest.compatibility.holographicdisplays.lines;

import lombok.Getter;
import me.filoghost.holographicdisplays.api.hologram.Hologram;

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
    public void addLine(final Hologram hologram) {
        hologram.getLines().appendText(this.text);
    }
}
