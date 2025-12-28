package org.betonquest.betonquest.quest.variable.objective;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;

/**
 * Resolves to a specified property of an objective.
 */
public class ObjectivePropertyVariable implements PlayerVariable {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

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
     * @param questTypeApi the Quest Type API
     * @param objectiveID  The objective ID.
     * @param propertyName The property name.
     */
    public ObjectivePropertyVariable(final QuestTypeApi questTypeApi, final ObjectiveID objectiveID, final String propertyName) {
        this.questTypeApi = questTypeApi;
        this.objectiveID = objectiveID;
        this.propertyName = propertyName;
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        final DefaultObjective objective = questTypeApi.getObjective(objectiveID);
        if (objective.containsPlayer(profile)) {
            return objective.getProperty(propertyName, profile);
        }
        return "";
    }
}
