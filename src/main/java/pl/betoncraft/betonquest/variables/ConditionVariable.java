package pl.betoncraft.betonquest.variables;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.id.ConditionID;

@SuppressWarnings("PMD.CommentRequired")
public class ConditionVariable extends Variable {

    private final ConditionID conditionId;
    private final boolean papiMode;

    public ConditionVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        conditionId = instruction.getCondition();
        papiMode = instruction.hasArgument("papiMode");
    }

    @Override
    public String getValue(final String playerID) {
        final String lang = BetonQuest.getInstance().getPlayerData(playerID).getLanguage();

        if (BetonQuest.condition(playerID, conditionId)) {
            return papiMode ? Config.getMessage(lang, "condition_variable_met") : "true";
        }
        return papiMode ? Config.getMessage(lang, "condition_variable_not_met") : "false";
    }
}
