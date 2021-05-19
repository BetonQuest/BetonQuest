package org.betonquest.betonquest.menu.events;

import org.betonquest.betonquest.menu.MenuID;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

/**
 * Every event where a menu was involved
 * <p>
 * Created on 16.03.2018.
 *
 * @author Jonas Blocher
 */
public abstract class MenuEvent extends PlayerEvent {

    private final MenuID menu;

    protected MenuEvent(final Player who, final MenuID menu) {
        super(who);
        this.menu = menu;
    }

    /**
     * @return the id of the menu that was involved in this event
     */
    public MenuID getMenu() {
        return menu;
    }
}
