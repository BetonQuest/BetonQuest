package org.betonquest.betonquest.quest.condition.logik;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.id.ConditionID;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * All of specified conditions have to be true.
 */
public class ConjunctionCondition implements NullableCondition {

    /**
     * All of specified conditions have to be true.
     */
    private final List<ConditionID> conditions;

    /**
     * Constructor for the {@link ConjunctionCondition} class.
     *
     * @param conditions All of specified conditions have to be true.
     */
    public ConjunctionCondition(final List<ConditionID> conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        return BetonQuest.conditions(profile, conditions);
    }
}
