package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("PMD.CommentRequired")
public class MMOCoreClassCondition extends Condition {
    private final String targetClassName;

    private final boolean mustBeEqual;

    @Nullable
    private final VariableNumber targetClassLevel;

    public MMOCoreClassCondition(final Instruction instruction) throws QuestException {
        super(instruction, true);
        targetClassName = instruction.next();
        targetClassLevel = instruction.hasNext() ? instruction.get(VariableNumber::new) : null;
        mustBeEqual = instruction.hasArgument("equal");
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestException {
        final PlayerData data = PlayerData.get(profile.getPlayerUUID());

        final String actualClassName = data.getProfess().getId();
        final int actualClassLevel = data.getLevel();

        if (actualClassName.equalsIgnoreCase(targetClassName) || "*".equals(targetClassName) && !"HUMAN".equalsIgnoreCase(actualClassName)) {
            if (targetClassLevel == null) {
                return true;
            }
            final int level = targetClassLevel.getInt(profile);
            return mustBeEqual ? actualClassLevel == level : actualClassLevel >= level;
        }
        return false;
    }
}
