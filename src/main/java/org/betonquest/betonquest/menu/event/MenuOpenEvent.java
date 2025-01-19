package org.betonquest.betonquest.menu.event;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.menu.MenuID;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called whenever a menu is opened.
 */
@SuppressWarnings("PMD.CommentRequired")
public class MenuOpenEvent extends MenuEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private boolean cancelled;

    public MenuOpenEvent(final Profile who, final MenuID menu) {
        super(who, menu);
    }

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
