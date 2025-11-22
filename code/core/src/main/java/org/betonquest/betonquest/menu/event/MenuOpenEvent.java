package org.betonquest.betonquest.menu.event;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.menu.MenuID;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called whenever a menu is opened.
 */
public class MenuOpenEvent extends MenuEvent implements Cancellable {
    /**
     * A list of all handlers for this event.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Whether the event is cancelled.
     */
    private boolean cancelled;

    /**
     * Create a new Menu Event.
     *
     * @param who  the profile which interacted with the menu
     * @param menu the id of the menu which was interacted with
     */
    public MenuOpenEvent(final Profile who, final MenuID menu) {
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

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}
