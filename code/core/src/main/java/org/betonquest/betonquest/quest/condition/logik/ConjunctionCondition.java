package org.betonquest.betonquest.quest.condition.logik;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * All of specified conditions have to be true.
 */
public class ConjunctionCondition implements NullableCondition {

    /**
     * All of specified conditions have to be true.
     */
    private final Variable<List<ConditionID>> conditions;

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * Constructor for the {@link ConjunctionCondition} class.
     *
     * @param conditions   All of specified conditions have to be true.
     * @param questTypeAPI the Quest Type API
     */
    public ConjunctionCondition(final Variable<List<ConditionID>> conditions, final QuestTypeAPI questTypeAPI) {
        this.conditions = conditions;
        this.questTypeAPI = questTypeAPI;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        return questTypeAPI.conditions(profile, conditions.getValue(profile));
    }
}
