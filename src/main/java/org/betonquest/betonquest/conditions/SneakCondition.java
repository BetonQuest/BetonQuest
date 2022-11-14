package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;

/**
 * Returns true if the player is sneaking
 */
@SuppressWarnings("PMD.CommentRequired")
public class SneakCondition extends Condition {

    public SneakCondition(final Instruction instruction) {
        super(instruction, true);
    }

    @Override
    protected Boolean execute(final Profile profile) {
        return profile.getOnlineProfile().get().getPlayer().isSneaking();
    }

}
