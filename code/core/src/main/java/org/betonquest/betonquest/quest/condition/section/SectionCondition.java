package org.betonquest.betonquest.quest.condition.section;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.NullableCondition;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Condition that checks if the amount as {@code true} evaluated conditions are within a specific range.
 *
 */
public class SectionCondition implements NullableCondition {

    /**
     * The condition manager.
     */
    private final ConditionManager conditionManager;

    /**
     * Identifiers of conditions to evaluate.
     */
    private final Argument<List<ConditionIdentifier>> identifiers;

    /**
     * Minimum amount of conditions that must evaluate to {@code true}.
     */
    private final Argument<Number> min;

    /**
     * Maximum amount of conditions that must evaluate to {@code true}.
     */
    private final Argument<Number> max;

    /**
     * Create a new condition that checks if the amount as {@code true} evaluated conditions are within the range.
     *
     * @param conditionManager the condition manager
     * @param identifiers      the identifiers of conditions to evaluate
     * @param min              the minimum amount of conditions that must evaluate to {@code true}
     * @param max              the maximum amount of conditions that must evaluate to {@code true}
     */
    public SectionCondition(final ConditionManager conditionManager, final Argument<List<ConditionIdentifier>> identifiers,
                            final Argument<Number> min, final Argument<Number> max) {
        this.conditionManager = conditionManager;
        this.identifiers = identifiers;
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final int minimum = min.getValue(profile).intValue();
        final int maximum = max.getValue(profile).intValue();
        return minimum <= maximum && conditionManager.test(profile, identifiers.getValue(profile), (positive, negative, remaining) -> {
            if (positive >= minimum && positive + remaining <= maximum) {
                return Optional.of(true);
            }
            if (positive > maximum || positive + remaining < minimum) {
                return Optional.of(false);
            }
            return Optional.empty();
        });
    }
}
