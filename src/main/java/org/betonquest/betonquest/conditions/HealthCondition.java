package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.entity.Player;

/**
 * Requires the player to have specified amount of health (or more)
 */
@SuppressWarnings("PMD.CommentRequired")
public class HealthCondition extends Condition {

    private final VariableNumber health;

    public HealthCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        health = instruction.getVarNum();
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        final double expectedHealth = health.getDouble(profile);
        return profile.getOnlineProfile()
                .map(OnlineProfile::getPlayer)
                .map(Player::getHealth)
                .map(playerHealth -> playerHealth >= expectedHealth)
                .orElse(false);
    }

}
