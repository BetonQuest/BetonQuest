package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.menu.MenuID;
import org.betonquest.betonquest.menu.RPGMenu;
import org.jetbrains.annotations.Nullable;

/**
 * Checks if a player has opened a menu.
 */
public class MenuCondition implements OnlineCondition {

    /**
     * MenuID to check, null if any matches.
     */
    @Nullable
    private final Variable<MenuID> menuID;

    /**
     * Create a new menu condition.
     *
     * @param menuID the menu id to check for or null if matches any
     */
    public MenuCondition(@Nullable final Variable<MenuID> menuID) {
        this.menuID = menuID;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        return RPGMenu.hasOpenedMenu(profile, menuID == null ? null : menuID.getValue(profile));
    }
}
