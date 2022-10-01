package org.betonquest.betonquest.compatibility.holographicdisplays;

import lombok.Getter;

/**
 * Stores entry data of a {@link TopXObject}.
 */
@SuppressWarnings("PMD.CommentRequired")
@Getter
public final class TopXLine {

    /**
     * Name of player.
     */
    private final String playerName;

    /**
     * Value for point.
     */
    private final long count;

    /**
     * Creates a new instance of TopXLine
     *
     * @param playerName Name of player
     * @param count      Value of point
     */
    public TopXLine(final String playerName, final long count) {
        this.playerName = playerName;
        this.count = count;
    }
}
