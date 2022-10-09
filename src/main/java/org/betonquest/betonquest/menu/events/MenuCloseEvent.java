package org.betonquest.betonquest.menu.events;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.menu.MenuID;
import org.bukkit.event.HandlerList;

/**
 * Called whenever a menu is closed.
 */
@SuppressWarnings("PMD.CommentRequired")
public class MenuCloseEvent extends MenuEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public MenuCloseEvent(final Profile who, final MenuID menu) {
        super(who, menu);
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
