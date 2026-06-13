package org.betonquest.betonquest.compatibility.thebrewingproject;

import dev.jsinco.brewery.api.brew.BrewQuality;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

/**
 * Utility class for dealing with brew data.
 */
public final class BrewUtil {

    /**
     * Persistent data key used by TheBrewingProject to identify brews.
     */
    public static final NamespacedKey TBP_TAG = new NamespacedKey("brewery", "tag");

    /**
     * Persistent data key used by TheBrewingProject to identify scores.
     */
    private static final NamespacedKey TBP_SCORE = new NamespacedKey("brewery", "score");

    /**
     * The minimal score for excellent brewing quality.
     */
    private static final double EXCELLENT_QUALITY = 0.8;

    /**
     * The minimal score for good brewing quality.
     */
    private static final double GOOD_QUALITY = 0.6;

    private BrewUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Get the name of the recipe this brew matched with when it was created.
     *
     * @param container The item persistent data container
     * @return The recipe name, if present
     */
    public static Optional<String> brewName(final PersistentDataContainer container) {

        return Optional.ofNullable(container.get(
                TBP_TAG, PersistentDataType.STRING
        ));
    }

    /**
     * Get the quality of this brew from when it was created.
     *
     * @param container A persistent data container linked to an item
     * @return The quality, if present
     */
    public static Optional<BrewQuality> quality(final PersistentDataContainer container) {
        return Optional.ofNullable(container.get(
                TBP_SCORE, PersistentDataType.DOUBLE
        )).flatMap(BrewUtil::quality);
    }

    /**
     * Determine a quality from the score.
     *
     * @param score The score of the brew
     * @return The brew quality if score is above 0, otherwise an empty value
     */
    public static Optional<BrewQuality> quality(final double score) {
        if (score >= EXCELLENT_QUALITY) {
            return Optional.of(BrewQuality.EXCELLENT);
        }
        if (score >= GOOD_QUALITY) {
            return Optional.of(BrewQuality.GOOD);
        }
        if (score > 0) {
            return Optional.of(BrewQuality.BAD);
        }
        return Optional.empty();
    }
}
