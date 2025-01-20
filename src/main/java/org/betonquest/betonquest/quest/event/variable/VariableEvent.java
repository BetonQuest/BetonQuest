package org.betonquest.betonquest.quest.event.variable;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.objective.VariableObjective;

/**
 * Event that changes variables that is stored in variable objective.
 */
public class VariableEvent implements Event {

    /**
     * The variable objective id to change the variable in.
     */
    private final ObjectiveID objectiveID;

    /**
     * The key of the variable to store.
     */
    private final VariableString key;

    /**
     * The value of the variable to store.
     */
    private final VariableString value;

    /**
     * BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Create a new variable event.
     *
     * @param objectiveID the objective id of the variable objective
     * @param key         the key of the variable to store
     * @param value       the value of the variable to store
     * @param betonQuest  the BetonQuest instance
     */
    public VariableEvent(final ObjectiveID objectiveID, final VariableString key, final VariableString value, final BetonQuest betonQuest) {
        this.objectiveID = objectiveID;
        this.key = key;
        this.value = value;
        this.betonQuest = betonQuest;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final Objective obj = betonQuest.getObjective(objectiveID);
        if (!(obj instanceof final VariableObjective objective)) {
            throw new QuestException(objectiveID.getFullID() + " is not a variable objective");
        }
        final String keyReplaced = key.getValue(profile);
        final String valueReplaced = value.getValue(profile);
        if (!objective.store(profile, keyReplaced, valueReplaced)) {
            throw new QuestException("Player " + profile.getProfileName() + " does not have '"
                    + objectiveID + "' objective, cannot store a variable.");
        }
    }
}
