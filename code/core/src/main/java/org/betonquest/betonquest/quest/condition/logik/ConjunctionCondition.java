package org.betonquest.betonquest.quest.condition.logik;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * All specified conditions have to be true.
 */
public class ConjunctionCondition implements NullableCondition {

    /**
     * All specified conditions have to be true.
     */
    private final Argument<List<ConditionIdentifier>> conditions;

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Constructor for the {@link ConjunctionCondition} class.
     *
     * @param conditions   All specified conditions have to be true.
     * @param questTypeApi the Quest Type API
     */
    public ConjunctionCondition(final Argument<List<ConditionIdentifier>> conditions, final QuestTypeApi questTypeApi) {
        this.conditions = conditions;
        this.questTypeApi = questTypeApi;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        return questTypeApi.conditions(profile, conditions.getValue(profile));
    }
}
