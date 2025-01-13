package org.betonquest.betonquest.quest.condition.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.id.ObjectiveID;

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
    private final ObjectiveID objectiveId;

    /**
     * Creates a new ObjectiveCondition.
     *
     * @param questTypeAPI the Quest Type API
     * @param objectiveId  the objective ID
     */
    public ObjectiveCondition(final QuestTypeAPI questTypeAPI, final ObjectiveID objectiveId) {
        this.questTypeAPI = questTypeAPI;
        this.objectiveId = objectiveId;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final Objective objective = questTypeAPI.getObjective(objectiveId);
        if (objective == null) {
            throw new QuestException("Objective " + objectiveId + " not found. Check for errors on /bq reload!");
        }
        return objective.containsPlayer(profile);
    }
}
