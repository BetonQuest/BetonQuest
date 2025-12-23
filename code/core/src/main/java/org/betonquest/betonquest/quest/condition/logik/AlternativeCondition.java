package org.betonquest.betonquest.quest.condition.logik;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * One of specified conditions has to be true.
 */
public class AlternativeCondition implements NullableCondition {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * List of condition IDs.
     */
    private final Variable<List<ConditionID>> conditionIDs;

    /**
     * Create a new alternative condition.
     *
     * @param questTypeApi the Quest Type API to check conditions
     * @param conditionIDs the condition IDs
     */
    public AlternativeCondition(final QuestTypeApi questTypeApi, final Variable<List<ConditionID>> conditionIDs) {
        this.questTypeApi = questTypeApi;
        this.conditionIDs = conditionIDs;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final List<ConditionID> conditionIDs = this.conditionIDs.getValue(profile);
        return questTypeApi.conditionsAny(profile, conditionIDs);
    }
}
