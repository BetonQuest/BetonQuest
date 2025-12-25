package org.betonquest.betonquest.quest.condition.permission;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;

/**
 * A condition that checks if a player has a permission.
 */
public class PermissionCondition implements OnlineCondition {

    /**
     * The permission to check for.
     */
    private final Argument<String> permission;

    /**
     * Creates a new permission condition.
     *
     * @param permission The permission to check for.
     */
    public PermissionCondition(final Argument<String> permission) {
        this.permission = permission;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        return profile.getPlayer().hasPermission(permission.getValue(profile));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
