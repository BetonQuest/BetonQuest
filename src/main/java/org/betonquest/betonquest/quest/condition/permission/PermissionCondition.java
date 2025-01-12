package org.betonquest.betonquest.quest.condition.permission;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableString;

/**
 * A condition that checks if a player has a permission.
 */
public class PermissionCondition implements OnlineCondition {

    /**
     * The permission to check for.
     */
    private final VariableString permission;

    /**
     * Creates a new permission condition.
     *
     * @param permission The permission to check for.
     */
    public PermissionCondition(final VariableString permission) {
        this.permission = permission;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        return profile.getPlayer().hasPermission(permission.getValue(profile));
    }
}
