package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

/**
 * Requires a specified amount of global points (or more) in specified
 * category
 */
@SuppressWarnings("PMD.CommentRequired")
public class GlobalPointCondition extends PointCondition {

    public GlobalPointCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction);
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        return check(playerID, BetonQuest.getInstance().getGlobalData().getPoints());
    }

}
