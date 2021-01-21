package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;

/**
 * Requires the player to have specified permission node
 */
@SuppressWarnings("PMD.CommentRequired")
public class PermissionCondition extends Condition {

    private final String permission;

    public PermissionCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        permission = instruction.next();
    }

    @Override
    protected Boolean execute(final String playerID) {
        return PlayerConverter.getPlayer(playerID).hasPermission(permission);
    }

}
