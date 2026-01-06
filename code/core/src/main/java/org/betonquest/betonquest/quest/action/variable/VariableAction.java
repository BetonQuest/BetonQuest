package org.betonquest.betonquest.quest.action.variable;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.quest.objective.variable.VariableObjective;

/**
 * Event that changes values that are stored in the variable objectives.
 */
public class VariableAction implements PlayerAction {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * The variable objective id to change the variable in.
     */
    private final Argument<ObjectiveID> objectiveID;

    /**
     * The key of the variable to store.
     */
    private final Argument<String> key;

    /**
     * The value of the variable to store.
     */
    private final Argument<String> value;

    /**
     * Create a new variable event.
     *
     * @param questTypeApi the Quest Type API
     * @param objectiveID  the objective id of the variable objective
     * @param key          the key of the variable to store
     * @param value        the value of the variable to store
     */
    public VariableAction(final QuestTypeApi questTypeApi, final Argument<ObjectiveID> objectiveID, final Argument<String> key, final Argument<String> value) {
        this.questTypeApi = questTypeApi;
        this.objectiveID = objectiveID;
        this.key = key;
        this.value = value;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final ObjectiveID resolved = this.objectiveID.getValue(profile);
        final DefaultObjective obj = questTypeApi.getObjective(resolved);
        if (!(obj instanceof final VariableObjective objective)) {
            throw new QuestException(resolved + " is not a variable objective");
        }
        final String keyReplaced = key.getValue(profile);
        final String valueReplaced = value.getValue(profile);
        if (!objective.store(profile, keyReplaced, valueReplaced)) {
            throw new QuestException("Player " + profile.getProfileName() + " does not have '"
                    + resolved + "' objective, cannot store a variable.");
        }
    }
}
