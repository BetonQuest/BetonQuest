package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.util.UUID;

@SuppressWarnings("PMD.CommentRequired")
public class MMOCoreAttributeCondition extends Condition {

    private final String attribute;
    private final VariableNumber targetLevelVar;
    private boolean mustBeEqual;

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
