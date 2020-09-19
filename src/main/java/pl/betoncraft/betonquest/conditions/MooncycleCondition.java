package pl.betoncraft.betonquest.conditions;

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * This condition checks the players moon cycle (1 is full moon, 8 is Waxing Gibbous) and returns if the player is
 * under that moon.
 */
public class MooncycleCondition extends Condition {

    private final VariableNumber thisCycle;

    public MooncycleCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        this.thisCycle = instruction.getVarNum();
    }


    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        final int days = (int) player.getWorld().getFullTime() / 24000;
        int phaseInt = days % 8;
        phaseInt += 1;
        return phaseInt == thisCycle.getInt(playerID);
    }

}



