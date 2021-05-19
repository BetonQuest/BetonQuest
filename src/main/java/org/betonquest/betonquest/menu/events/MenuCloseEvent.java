package org.betonquest.betonquest.menu.events;

import org.betonquest.betonquest.menu.MenuID;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called whenever a menu is closed
 * <p>
 * Created on 16.03.2018.
 *
 * @author Jonas Blocher
 */
public class MenuCloseEvent extends MenuEvent {

    private static final HandlerList handlers = new HandlerList();

    public MenuCloseEvent(final Player who, final MenuID menu) {
        super(who, menu);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
