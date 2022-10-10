package org.betonquest.betonquest.compatibility.holographicdisplays.lines;

import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.betonquest.betonquest.compatibility.holographicdisplays.HologramLoop;

/**
 * Superclass for all hologram content types.
 * <p>
 * HologramLoop parses and validates the hologram instructions into instances of AbstractLine
 * so the content can be updated without dealing with parsing errors again.
 */
public abstract class AbstractLine {

    /**
     * Creates a new instance of the content line. It may validate data, however, the main validation is handled by
     * {@link HologramLoop#HologramLoop()}.
     */
    public AbstractLine() {
    }

    /**
     * Uses the stored data to update the content of the associated line(s).
     *
     * @param hologram target hologram
     */
    public abstract void addLine(Hologram hologram);
}
