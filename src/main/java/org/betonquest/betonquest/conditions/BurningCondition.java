package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;

/**
 * Requires the player to burn
 */
public class BurningCondition extends Condition {

    /**
     * Constructor of the BurningCondition
     *
     * @param instruction the instruction
     */
    public BurningCondition(final Instruction instruction) {
        super(instruction, true);
    }

    @Override
    protected Boolean execute(final Profile profile) {
        return profile.getOnlineProfile().getOnlinePlayer().getFireTicks() > 0;
    }
}
