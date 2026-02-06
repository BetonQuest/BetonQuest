package org.betonquest.betonquest.compatibility.brewery;

import org.betonquest.betonquest.api.QuestException;

/**
 * Utility class for Brewery.
 */
public final class BreweryUtils {

    private BreweryUtils() {
    }

    /**
     * Validate a quality and throw a {@link QuestException} if it is not between 1 and 10.
     *
     * @param quality the quality to validate.
     * @throws QuestException if the quality is not between 1 and 10.
     */
    public static void validateQualityOrThrow(final int quality) throws QuestException {
        if (quality <= 0 || quality > 10) {
            throw new QuestException("Drunk quality can only be between 1 and 10!");
        }
    }
}
