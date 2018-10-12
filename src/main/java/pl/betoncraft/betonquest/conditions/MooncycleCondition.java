package pl.betoncraft.betonquest.conditions;

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

    /**
     * This condition checks the players moon cycle (1 is full moon, 8 is Waxing Gibbous) and returns if the player is
     * under that moon.
     *
     * @author Caleb Britannia (James Thacker)
     */
    public class MooncycleCondition extends Condition {

        private VariableNumber thisCycle;

        public MooncycleCondition(Instruction instruction) throws InstructionParseException {
            super(instruction);
            this.thisCycle = instruction.getVarNum();
        }


        @Override
        public boolean check(String playerID) throws QuestRuntimeException {
            Player player = PlayerConverter.getPlayer(playerID);
            int days = (int) player.getWorld().getFullTime() / 24000;
            int phaseInt = days % 8;
            phaseInt += 1;
            return (phaseInt == thisCycle.getInt(playerID));
        }

    }



