package org.betonquest.betonquest.quest.variable.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.id.ObjectiveID;

/**
 * Resolves to a specified property of an objective.
 */
public class ObjectivePropertyVariable implements PlayerVariable {

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * The objective ID.
     */
    private final ObjectiveID objectiveID;

    /**
     * The property name.
     */
    private final String propertyName;

    /**
     * Create a new objective property variable.
     *
     * @param questTypeAPI the Quest Type API
     * @param objectiveID  The objective ID.
     * @param propertyName The property name.
     */
    public ObjectivePropertyVariable(final QuestTypeAPI questTypeAPI, final ObjectiveID objectiveID, final String propertyName) {
        this.questTypeAPI = questTypeAPI;
        this.objectiveID = objectiveID;
        this.propertyName = propertyName;
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        final Objective objective = questTypeAPI.getObjective(objectiveID);
        if (objective.containsPlayer(profile)) {
            return objective.getProperty(propertyName, profile);
        }
        return "";
    }
}
