package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;

/**
 * Requires the player to burn
 */
@SuppressWarnings("PMD.CommentRequired")
public class BurningCondition extends Condition {

    public BurningCondition(final Instruction instruction) {
        super(instruction, true);
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        return PlayerConverter.getPlayer(playerID).getFireTicks() > 0;
    }
}
