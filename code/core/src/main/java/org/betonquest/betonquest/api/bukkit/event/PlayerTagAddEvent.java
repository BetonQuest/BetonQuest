package org.betonquest.betonquest.api.bukkit.event;

import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.event.HandlerList;

/**
 * Fired when a tag is added to a profile.
 */
public class PlayerTagAddEvent extends ProfileEvent {

    /**
     * HandlerList of this event.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * The tag that was added.
     */
    private final String tag;

    /**
     * Creates a new PlayerTagAddEvent.
     *
     * @param who the {@link Profile} whose tag has added
     * @param tag added tag
     */
    public PlayerTagAddEvent(final Profile who, final String tag) {
        super(who);
        this.tag = tag;
    }

    /**
     * Gets the HandlerList of this event.
     *
     * @return the HandlerList
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    /**
     * Get the added tag.
     *
     * @return the tag which was added
     */
    public String getTag() {
        return tag;
    }

    /**
     * Gets the HandlerList of this event.
     *
     * @return the HandlerList
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
