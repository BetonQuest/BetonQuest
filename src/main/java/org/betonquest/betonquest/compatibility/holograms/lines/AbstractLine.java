package org.betonquest.betonquest.compatibility.holograms.lines;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.betonquest.betonquest.compatibility.holograms.HologramLoop;

/**
 * Superclass for all hologram content types.
 * <p>
 * HologramLoop parses and validates the hologram instructions into instances of AbstractLine
 * so the content can be updated without dealing with parsing errors again.
 */
public abstract class AbstractLine {
    /**
     * False if this line needs to be updated
     */
    protected final boolean staticText;

    /**
     * The amount of lines added by this line
     */
    protected final int linesAdded;

    /**
     * Creates a new instance of the content line. It may validate data, however, the main validation is handled by
     * {@link HologramLoop#HologramLoop(BetonQuestLoggerFactory, BetonQuestLogger)}.
     *
     * @param staticText false if this line needs to be updated
     * @param linesAdded the amount of lines added by this line when {@link #setLine(BetonHologram, int)} is called
     */
    public AbstractLine(final boolean staticText, final int linesAdded) {
        this.staticText = staticText;
        this.linesAdded = linesAdded;
    }

    /**
     * Uses the stored data to update the content of the associated line(s).
     *
     * @param hologram target hologram
     * @param index    the starting index
     */
    public abstract void setLine(BetonHologram hologram, int index);

    /**
     * Gets the amount of lines added when {@link #setLine(BetonHologram, int)} is called.
     *
     * @return the amount of lines
     */
    public int getLinesAdded() {
        return linesAdded;
    }

    /**
     * @return True if this line's content does not need to be updated, false if it does.
     */
    public boolean isNotStaticText() {
        return !staticText;
    }
}
