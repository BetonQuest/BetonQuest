package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Requires the player to have specified level of experience or more.
 */
@SuppressWarnings("PMD.CommentRequired")
public class ExperienceCondition extends Condition {

    /**
     * The experience level the player needs to get.
     * The decimal part of the number is a percentage of the next level.
     */
    private final VariableNumber amount;

    public ExperienceCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        this.amount = instruction.getVarNum();
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        final double amount = this.amount.getDouble(profile);
        return profile.getOnlineProfile()
                .map(OnlineProfile::getPlayer)
                .map(player -> player.getLevel() + player.getExp() >= amount)
                .orElse(false);
    }

}
