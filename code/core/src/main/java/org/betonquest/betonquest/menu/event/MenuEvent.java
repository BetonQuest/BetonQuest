package org.betonquest.betonquest.menu.event;

import org.betonquest.betonquest.api.bukkit.event.ProfileEvent;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.menu.MenuID;

/**
 * Every event where a menu is involved.
 */
public abstract class MenuEvent extends ProfileEvent {

    /**
     * Involved Menu's id.
     */
    private final MenuID menu;

    /**
     * Create a new Menu Event.
     *
     * @param who  the profile which interacted with the menu
     * @param menu the id of the menu which was interacted with
     */
    protected MenuEvent(final Profile who, final MenuID menu) {
        super(who);
        this.menu = menu;
    }

    /**
     * Get the involved menu's id.
     *
     * @return the id of the menu that was involved in this event
     */
    public MenuID getMenu() {
        return menu;
    }
}
