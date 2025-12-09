package org.betonquest.betonquest.quest.condition.random;

import java.util.Random;

/**
 * Represents the chance of a random pick between 1 and a rangeMax value.
 *
 * @param pick     the value to pick at least to result in a true state
 * @param rangeMax the maximum for the range to pick randomly from
 */
public record RandomChance(Number pick, Number rangeMax) {

    /**
     * Picks a random number between 1 and the rangeMax and checks if it's smaller or equal to the pick value.
     *
     * @param rng the random number generator to use
     * @return true if the random number up to rangeMax is smaller or equal to the pick value, false otherwise
     */
    public boolean pickRandom(final Random rng) {
        final int random = rng.nextInt(rangeMax.intValue()) + 1;
        return random <= pick.intValue();
    }
}
