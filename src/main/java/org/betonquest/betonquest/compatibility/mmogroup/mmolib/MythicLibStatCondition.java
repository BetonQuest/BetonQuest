package org.betonquest.betonquest.compatibility.mmogroup.mmolib;

import io.lumine.mythic.lib.api.player.MMOPlayerData;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

@SuppressWarnings("PMD.CommentRequired")
public class MythicLibStatCondition extends Condition {

    private final String statName;
    private final double targetLevel;
    private final boolean mustBeEqual;

    public MythicLibStatCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        statName = instruction.next();
        targetLevel = instruction.getDouble();
        mustBeEqual = instruction.hasArgument("equal");
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        final MMOPlayerData data = MMOPlayerData.get(player);
        if (data == null) {
            return false;
        }
        final double actualLevel = data.getStatMap().getStat(statName);
        return mustBeEqual ? actualLevel == targetLevel : actualLevel >= targetLevel;
    }

}
