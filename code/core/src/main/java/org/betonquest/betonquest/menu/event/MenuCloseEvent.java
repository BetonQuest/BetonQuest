package org.betonquest.betonquest.menu.event;

import org.betonquest.betonquest.api.identifier.MenuIdentifier;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.event.HandlerList;

/**
 * Called whenever a menu is closed.
 */
public class MenuCloseEvent extends MenuEvent {

    /**
     * A list of all handlers for this event.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Create a new Menu Event.
     *
     * @param who  the profile which interacted with the menu
     * @param menu the id of the menu which was interacted with
     */
    public MenuCloseEvent(final Profile who, final MenuIdentifier menu) {
        super(who, menu);
    }

    /**
     * Get the HandlerList of this event.
     *
     * @return the HandlerList.
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
