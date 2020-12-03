package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

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
