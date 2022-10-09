package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;

/**
 * Checks if the player is gliding with elytra.
 */
@SuppressWarnings("PMD.CommentRequired")
public class FlyingCondition extends Condition {

    public FlyingCondition(final Instruction instruction) {
        super(instruction, true);
    }

    @Override
    protected Boolean execute(final Profile profile) {
        return profile.getOnlineProfile().getOnlinePlayer().isGliding();
    }

}
