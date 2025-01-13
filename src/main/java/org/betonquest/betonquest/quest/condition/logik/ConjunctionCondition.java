package org.betonquest.betonquest.quest.condition.logik;

import org.betonquest.betonquest.api.BetonQuestAPI;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.exceptions.QuestException;
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
     * BetonQuest API.
     */
    private final BetonQuestAPI questAPI;

    /**
     * Constructor for the {@link ConjunctionCondition} class.
     *
     * @param conditions All of specified conditions have to be true.
     * @param questAPI   the BetonQuest API
     */
    public ConjunctionCondition(final List<ConditionID> conditions, final BetonQuestAPI questAPI) {
        this.conditions = conditions;
        this.questAPI = questAPI;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        return questAPI.conditions(profile, conditions);
    }
}
