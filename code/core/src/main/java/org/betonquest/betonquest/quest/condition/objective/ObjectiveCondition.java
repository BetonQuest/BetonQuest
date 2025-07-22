package org.betonquest.betonquest.quest.condition.objective;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Checks if the player has specified objective active.
 */
public class ObjectiveCondition implements PlayerCondition {

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * The objective ID.
     */
    private final Variable<ObjectiveID> objectiveId;

    /**
     * Creates a new ObjectiveCondition.
     *
     * @param questTypeAPI the Quest Type API
     * @param objectiveId  the objective ID
     */
    public ObjectiveCondition(final QuestTypeAPI questTypeAPI, final Variable<ObjectiveID> objectiveId) {
        this.questTypeAPI = questTypeAPI;
        this.objectiveId = objectiveId;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        return questTypeAPI.getObjective(objectiveId.getValue(profile)).containsPlayer(profile);
    }
}
