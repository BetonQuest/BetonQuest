package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the player to have specified amount of health (or more)
 */
@SuppressWarnings("PMD.CommentRequired")
public class HealthCondition extends Condition {

    private final VariableNumber health;

    public HealthCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        health = instruction.getVarNum();
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        return PlayerConverter.getPlayer(playerID).getHealth() >= health.getDouble(playerID);
    }

}
