package pl.betoncraft.betonquest.compatibility.mmogroup.mmocore;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

import java.util.UUID;

public class MMOCoreAttributeCondition extends Condition {

    String attribute;
    int targetLevel;
    boolean mustBeEqual = false;

    public MMOCoreAttributeCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        attribute = instruction.next();
        MMOCoreUtils.isMMOConfigValidForAttribute(attribute);

        targetLevel = instruction.getInt();
        if (instruction.hasArgument("equal")) {
            mustBeEqual = true;
        }
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final int actualLevel = MMOCoreUtils.getMMOCoreAttribute(UUID.fromString(playerID), attribute);

        return mustBeEqual ? actualLevel == targetLevel : actualLevel >= targetLevel;
    }
}
