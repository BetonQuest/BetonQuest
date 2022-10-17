package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.objectives.VariableObjective;

import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class VariableEvent extends QuestEvent {

    private final ObjectiveID objectiveID;
    private final String key;
    private final List<String> keyVariables;
    private final String value;
    private final List<String> valueVariables;

    public VariableEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        objectiveID = instruction.getObjective();
        key = instruction.next();
        keyVariables = BetonQuest.resolveVariables(key);
        value = instruction.next();
        valueVariables = BetonQuest.resolveVariables(value);
    }

    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final Objective obj = BetonQuest.getInstance().getObjective(objectiveID);
        if (!(obj instanceof VariableObjective)) {
            throw new QuestRuntimeException(objectiveID.getFullID() + " is not a variable objective");
        }
        final VariableObjective objective = (VariableObjective) obj;
        String keyReplaced = key;
        for (final String v : keyVariables) {
            keyReplaced = keyReplaced.replace(v, BetonQuest.getInstance().getVariableValue(
                    instruction.getPackage().getQuestPath(), v, profile));
        }
        String valueReplaced = value;
        for (final String v : valueVariables) {
            valueReplaced = valueReplaced.replace(v, BetonQuest.getInstance().getVariableValue(
                    instruction.getPackage().getQuestPath(), v, profile));
        }
        if (!objective.store(profile, keyReplaced.replace('_', ' '), valueReplaced.replace('_', ' '))) {
            throw new QuestRuntimeException("Player " + profile.getProfileName() + " does not have '" +
                    objectiveID.getFullID() + "' objective, cannot store a variable.");
        }
        return null;
    }

}
