package pl.betoncraft.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.api.player.PlayerData;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class MMOCoreProfessionLevelCondition extends Condition {

    PlayerData data;

    String professionName;
    int targetLevel;
    boolean mustBeEqual = false;

    public MMOCoreProfessionLevelCondition(Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        professionName = instruction.next();
        targetLevel = instruction.getInt();
        if (instruction.hasArgument("equal")) {
            mustBeEqual = true;
        }
    }

    @Override
    protected Boolean execute(String playerID) throws QuestRuntimeException {
        data = PlayerData.get(PlayerConverter.getPlayer(playerID));
        int actualLevel = data.getCollectionSkills().getLevel(professionName);

        return mustBeEqual ? actualLevel == targetLevel : actualLevel >= targetLevel;
    }
}
