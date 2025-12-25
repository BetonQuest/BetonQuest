package org.betonquest.betonquest.quest.condition.random;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.function.Supplier;

/**
 * The condition that is met randomly.
 */
public class RandomCondition implements NullableCondition {

    /**
     * The range of the random number to be true.
     */
    private final Argument<RandomChance> randomChance;

    /**
     * The random number generator supplier.
     */
    private final Supplier<Random> random;

    /**
     * The constructor of the random condition.
     *
     * @param randomSupplier the random number generator supplier
     * @param chance         the {@link RandomChance} to check the random condition
     */
    public RandomCondition(final Supplier<Random> randomSupplier, final Argument<RandomChance> chance) {
        this.randomChance = chance;
        this.random = randomSupplier;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        return randomChance.getValue(profile).pickRandom(random.get());
    }
}
