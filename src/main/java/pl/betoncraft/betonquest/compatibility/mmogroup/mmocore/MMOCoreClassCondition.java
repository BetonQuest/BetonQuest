package pl.betoncraft.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.ArrayList;

public class MMOCoreClassCondition extends Condition {

    private final String targetClassName;
    private int targetClassLevel = -1;
    private boolean mustBeEqual;

    public MMOCoreClassCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        targetClassName = instruction.next();

        final ArrayList<Integer> potentialLevel = instruction.getAllNumbers();
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
