package org.betonquest.betonquest.quest.condition.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ObjectiveID;

/**
 * Checks if the player has specified objective active.
 */
public class ObjectiveCondition implements PlayerCondition {

    /**
     * The objective ID.
     */
    private final ObjectiveID objective;

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Creates a new ObjectiveCondition.
     *
     * @param objective  the objective ID
     * @param betonQuest the BetonQuest instance
     */
    public ObjectiveCondition(final ObjectiveID objective, final BetonQuest betonQuest) {
        this.objective = objective;
        this.betonQuest = betonQuest;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final Objective objective = betonQuest.getObjective(this.objective);
        if (objective == null) {
            throw new QuestException("Objective " + this.objective + " not found. Check for errors on /bq reload!");
        }
        return objective.containsPlayer(profile);
    }
}
