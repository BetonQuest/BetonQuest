package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ConditionID;

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
