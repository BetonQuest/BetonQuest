package pl.betoncraft.betonquest.events;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.id.ObjectiveID;
import pl.betoncraft.betonquest.objectives.VariableObjective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

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
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Objective obj = BetonQuest.getInstance().getObjective(objectiveID);
        if (!(obj instanceof VariableObjective)) {
            throw new QuestRuntimeException(objectiveID.getFullID() + " is not a variable objective");
        }
        final VariableObjective objective = (VariableObjective) obj;
        String keyReplaced = key;
        for (final String v : keyVariables) {
            keyReplaced = keyReplaced.replace(v, BetonQuest.getInstance().getVariableValue(
                    instruction.getPackage().getName(), v, playerID));
        }
        String valueReplaced = value;
        for (final String v : valueVariables) {
            valueReplaced = valueReplaced.replace(v, BetonQuest.getInstance().getVariableValue(
                    instruction.getPackage().getName(), v, playerID));
        }
        if (!objective.store(playerID, keyReplaced.replace('_', ' '), valueReplaced.replace('_', ' '))) {
            throw new QuestRuntimeException("Player " + PlayerConverter.getName(playerID) + " does not have '" +
                    objectiveID.getFullID() + "' objective, cannot store a variable.");
        }
        return null;
    }

}
