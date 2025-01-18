package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

@SuppressWarnings("PMD.CommentRequired")
public class MMOCoreAttributeCondition extends Condition {
    private final String attribute;

    private final VariableNumber targetLevelVar;

    private final boolean mustBeEqual;

    public MMOCoreAttributeCondition(final Instruction instruction) throws QuestException {
        super(instruction, true);

        attribute = instruction.next();
        MMOCoreUtils.isMMOConfigValidForAttribute(attribute);

        targetLevelVar = instruction.get(VariableNumber::new);
        mustBeEqual = instruction.hasArgument("equal");
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestException {
        final int targetLevel = targetLevelVar.getInt(profile);
        final int actualLevel = MMOCoreUtils.getMMOCoreAttribute(profile.getPlayerUUID(), attribute);

        return mustBeEqual ? actualLevel == targetLevel : actualLevel >= targetLevel;
    }
}
