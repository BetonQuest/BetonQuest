package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;

/**
 * Checks if the player is gliding with elytra.
 */
@SuppressWarnings("PMD.CommentRequired")
public class FlyingCondition extends Condition {

    public FlyingCondition(final Instruction instruction) {
        super(instruction, true);
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        return PlayerConverter.getPlayer(playerID).isGliding();
    }

}
