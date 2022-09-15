package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

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
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        return profile.getOnlineProfile().getOnlinePlayer().hasPermission(permission);
    }

}
