package org.betonquest.betonquest.quest.action.variable;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;
import org.betonquest.betonquest.quest.objective.variable.VariableObjective;

/**
 * Action that changes values that are stored in the variable objectives.
 */
public class VariableAction implements PlayerAction {

    /**
     * The objective manager.
     */
    private final ObjectiveManager objectiveManager;

    /**
     * The variable objective id to change the variable in.
     */
    private final Argument<ObjectiveIdentifier> objectiveID;

    /**
     * The key of the variable to store.
     */
    private final Argument<String> key;

    /**
     * The value of the variable to store.
     */
    private final Argument<String> value;

    /**
     * Create a new variable action.
     *
     * @param objectiveManager the objective manager
     * @param objectiveID      the objective id of the variable objective
     * @param key              the key of the variable to store
     * @param value            the value of the variable to store
     */
    public VariableAction(final ObjectiveManager objectiveManager, final Argument<ObjectiveIdentifier> objectiveID, final Argument<String> key, final Argument<String> value) {
        this.objectiveManager = objectiveManager;
        this.objectiveID = objectiveID;
        this.key = key;
        this.value = value;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final ObjectiveIdentifier resolved = this.objectiveID.getValue(profile);
        final Objective obj = objectiveManager.getObjective(resolved);
        if (!(obj instanceof final VariableObjective objective)) {
            throw new QuestException("Objective '%s' is not a variable objective".formatted(resolved));
        }
        final String keyReplaced = key.getValue(profile);
        final String valueReplaced = value.getValue(profile);
        if (!objective.store(profile, keyReplaced, valueReplaced)) {
            throw new QuestException("Player '%s' does not have '%s' objective, cannot store a variable".formatted(
                    profile.getProfileName(), resolved));
        }
    }
}
