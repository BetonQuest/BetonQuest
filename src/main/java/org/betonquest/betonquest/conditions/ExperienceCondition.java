package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.entity.Player;

/**
 * Requires the player to have specified level of experience (or more)
 */
@SuppressWarnings("PMD.CommentRequired")
public class ExperienceCondition extends Condition {

    private final VariableNumber amount;
    private final boolean checkForLevel;

    public ExperienceCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        this.amount = instruction.getVarNum();
        this.checkForLevel = instruction.hasArgument("level");
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        final int amount = this.amount.getInt(profile);
        if (checkForLevel) {
            return player.getLevel() >= amount;
        } else {
            return player.getTotalExperience() >= amount;
        }
    }

}
