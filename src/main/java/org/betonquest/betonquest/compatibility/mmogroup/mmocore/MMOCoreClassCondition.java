package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class MMOCoreClassCondition extends Condition {

    private final String targetClassName;
    private int targetClassLevel = -1;
    private boolean mustBeEqual;

    public MMOCoreClassCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        targetClassName = instruction.next();

        final List<Integer> potentialLevel = instruction.getAllNumbers();
        if (!potentialLevel.isEmpty()) {
            targetClassLevel = potentialLevel.get(0);
        }

        if (instruction.hasArgument("equal")) {
            mustBeEqual = true;
        }
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        final PlayerData data = PlayerData.get(player);

        final String actualClassName = data.getProfess().getName();
        final int actualClassLevel = data.getLevel();

        if (actualClassName.equalsIgnoreCase(targetClassName)) {
            if (targetClassLevel == -1) {
                return true;
            }
            return mustBeEqual ? actualClassLevel == targetClassLevel : actualClassLevel >= targetClassLevel;
        }
        return false;
    }
}
