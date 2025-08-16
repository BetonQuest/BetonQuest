package org.betonquest.betonquest.quest.condition.objective;

import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;

/**
 * Checks if the player has specified objective active.
 */
public class ObjectiveCondition implements PlayerCondition {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * The objective ID.
     */
    private final Variable<ObjectiveID> objectiveId;

    /**
     * Creates a new ObjectiveCondition.
     *
     * @param questTypeApi the Quest Type API
     * @param objectiveId  the objective ID
     */
    public ObjectiveCondition(final QuestTypeApi questTypeApi, final Variable<ObjectiveID> objectiveId) {
        this.questTypeApi = questTypeApi;
        this.objectiveId = objectiveId;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        return questTypeApi.getObjective(objectiveId.getValue(profile)).containsPlayer(profile);
    }
}
