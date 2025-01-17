package org.betonquest.betonquest.menu.events;

import org.betonquest.betonquest.api.bukkit.event.ProfileEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.menu.MenuID;

/**
 * Every event where a menu is involved.
 */
@SuppressWarnings("PMD.CommentRequired")
public abstract class MenuEvent extends ProfileEvent {

    private final MenuID menu;

    protected MenuEvent(final Profile who, final MenuID menu) {
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
