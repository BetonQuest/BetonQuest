package org.betonquest.betonquest.quest.condition.permission;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * A condition that checks if a player has a permission.
 */
public class PermissionCondition implements OnlineCondition {

    /**
     * The permission to check for.
     */
    private final String permission;

    /**
     * Creates a new permission condition.
     *
     * @param permission The permission to check for.
     */
    public PermissionCondition(final String permission) {
        this.permission = permission;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestRuntimeException {
        return profile.getPlayer().hasPermission(permission);
    }
}
