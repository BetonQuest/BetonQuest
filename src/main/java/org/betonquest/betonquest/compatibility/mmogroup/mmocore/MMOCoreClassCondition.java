package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class MMOCoreClassCondition extends Condition {

    private final String targetClassName;
    private final boolean mustBeEqual;
    private int targetClassLevel = -1;

    public MMOCoreClassCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        targetClassName = instruction.next();

        final List<Integer> potentialLevel = instruction.getAllNumbers();
        if (!potentialLevel.isEmpty()) {
            targetClassLevel = potentialLevel.get(0);
        }

        mustBeEqual = instruction.hasArgument("equal");
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        final PlayerData data = PlayerData.get(profile.getOfflinePlayer().getUniqueId());

        final String actualClassName = data.getProfess().getId();
        final int actualClassLevel = data.getLevel();

        if (actualClassName.equalsIgnoreCase(targetClassName) || "*".equals(targetClassName) && !"HUMAN".equalsIgnoreCase(actualClassName)) {
            if (targetClassLevel == -1) {
                return true;
            }
            return mustBeEqual ? actualClassLevel == targetClassLevel : actualClassLevel >= targetClassLevel;
        }
        return false;
    }
}
