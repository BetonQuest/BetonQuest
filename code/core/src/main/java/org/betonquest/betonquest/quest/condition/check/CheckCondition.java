package org.betonquest.betonquest.quest.condition.check;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.NullableCondition;
import org.betonquest.betonquest.kernel.processor.adapter.ConditionAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Allows for checking multiple conditions with one instruction string.
 */
public class CheckCondition implements NullableCondition {

    /**
     * Conditions that will be checked by this condition. All must be true for this condition to be true as well.
     */
    private final List<ConditionAdapter> conditions;

    /**
     * Create a check condition for the given instruction.
     *
     * @param conditions conditions that will be checked by this condition
     */
    public CheckCondition(final List<ConditionAdapter> conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        for (final ConditionAdapter condition : conditions) {
            if (!condition.check(profile)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return conditions.stream().anyMatch(ConditionAdapter::isPrimaryThreadEnforced);
    }
}
