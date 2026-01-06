package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestConsumer;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.online.OnlineAction;

/**
 * Event to open, close or update menus.
 */
public class MenuEvent implements OnlineAction {

    /**
     * The action to do with the profile.
     */
    private final QuestConsumer<OnlineProfile> action;

    /**
     * Creates a new MenuQuestEvent.
     *
     * @param action the action to do with the profile
     */
    public MenuEvent(final QuestConsumer<OnlineProfile> action) {
        this.action = action;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        action.accept(profile);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
