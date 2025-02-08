package org.betonquest.betonquest.compatibility.holograms.lines;

/**
 * Stores entry data of a {@link TopXObject}.
 *
 * @param playerName Name of player.
 * @param count      Value for point.
 */
public record TopXLine(String playerName, long count) {

    /**
     * Creates a new instance of TopXLine.
     *
     * @param playerName Name of player.
     * @param count      Value of point.
     */
    public TopXLine {
    }
}
