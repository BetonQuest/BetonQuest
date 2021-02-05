package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;

@SuppressWarnings("PMD.CommentRequired")
public class MMOCoreProfessionLevelCondition extends Condition {


    private final String professionName;
    private final VariableNumber targetLevelVar;
    private boolean mustBeEqual;

    public MMOCoreProfessionLevelCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        professionName = instruction.next();
        targetLevelVar = instruction.getVarNum();
        if (instruction.hasArgument("equal")) {
            mustBeEqual = true;
        }
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final PlayerData data = PlayerData.get(PlayerConverter.getPlayer(playerID));
        final int actualLevel = data.getCollectionSkills().getLevel(professionName);
        final int targetLevel = targetLevelVar.getInt(playerID);

        return mustBeEqual ? actualLevel == targetLevel : actualLevel >= targetLevel;
    }
}
