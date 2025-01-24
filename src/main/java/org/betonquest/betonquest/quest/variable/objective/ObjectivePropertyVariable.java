package org.betonquest.betonquest.quest.variable.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.id.ObjectiveID;

/**
 * Resolves to a specified property of an objective.
 */
public class ObjectivePropertyVariable implements PlayerVariable {

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

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
     * @param betonQuest   The BetonQuest instance.
     * @param objectiveID  The objective ID.
     * @param propertyName The property name.
     */
    public ObjectivePropertyVariable(final BetonQuest betonQuest, final ObjectiveID objectiveID, final String propertyName) {
        this.betonQuest = betonQuest;
        this.objectiveID = objectiveID;
        this.propertyName = propertyName;
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        final Objective objective = betonQuest.getObjective(objectiveID);
        if (objective == null) {
            throw new QuestException("Objective not found: " + objectiveID);
        }
        if (objective.containsPlayer(profile)) {
            return objective.getProperty(propertyName, profile);
        }
        throw new QuestException("Player doesn't have objective active: " + objectiveID);
    }
}
