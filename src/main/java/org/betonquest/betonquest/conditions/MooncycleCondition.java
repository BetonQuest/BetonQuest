package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.entity.Player;

/**
 * This condition checks the players moon cycle (1 is full moon, 8 is Waxing Gibbous) and returns if the player is
 * under that moon.
 */
@SuppressWarnings("PMD.CommentRequired")
public class MooncycleCondition extends Condition {

    private final VariableNumber thisCycle;

    public MooncycleCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        this.thisCycle = instruction.getVarNum();
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        final int days = (int) player.getWorld().getFullTime() / 24_000;
        int phaseInt = days % 8;
        phaseInt += 1;
        return phaseInt == thisCycle.getInt(profile);
    }

}



