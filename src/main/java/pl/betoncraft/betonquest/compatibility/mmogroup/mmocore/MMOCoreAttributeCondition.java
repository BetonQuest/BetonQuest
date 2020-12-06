package pl.betoncraft.betonquest.compatibility.mmogroup.mmocore;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

import java.util.UUID;

@SuppressWarnings("PMD.CommentRequired")
public class MMOCoreAttributeCondition extends Condition {

    private final String attribute;
    private final VariableNumber targetLevelVar;
    private boolean mustBeEqual = false;

    public MMOCoreAttributeCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        attribute = instruction.next();
        MMOCoreUtils.isMMOConfigValidForAttribute(attribute);

        targetLevelVar = instruction.getVarNum();
        if (instruction.hasArgument("equal")) {
            mustBeEqual = true;
        }
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final int targetLevel = targetLevelVar.getInt(playerID);
        final int actualLevel = MMOCoreUtils.getMMOCoreAttribute(UUID.fromString(playerID), attribute);

        return mustBeEqual ? actualLevel == targetLevel : actualLevel >= targetLevel;
    }
}
