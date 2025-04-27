package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.api.common.function.QuestConsumer;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.menu.MenuID;
import org.betonquest.betonquest.menu.RPGMenu;
import org.jetbrains.annotations.Nullable;

/**
 * Event to open or close menus.
 */
public class MenuEvent implements OnlineEvent {
    /**
     * The stuff to do with the profile.
     */
    private final QuestConsumer<OnlineProfile> doStuff;

    /**
     * Creates a new MenuQuestEvent.
     *
     * @param rpgMenu the rpg menu instance to open and close menus
     * @param menuID  the menu id to open or null if open menus should be closed
     */
    public MenuEvent(final RPGMenu rpgMenu, @Nullable final Variable<MenuID> menuID) {
        if (menuID != null) {
            doStuff = profile -> rpgMenu.openMenu(profile, menuID.getValue(profile));
        } else {
            doStuff = RPGMenu::closeMenu;
        }
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        doStuff.accept(profile);
    }
}
