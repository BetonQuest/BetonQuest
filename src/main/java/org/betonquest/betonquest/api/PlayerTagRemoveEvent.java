package org.betonquest.betonquest.api;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.profiles.ProfileEvent;
import org.bukkit.event.HandlerList;

/**
 * Fired when a tag is removed from a profile.
 */
@SuppressWarnings({"PMD.DataClass"})
public class PlayerTagRemoveEvent extends ProfileEvent {

    /**
     * HandlerList of this event.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * The tag that was removed.
     */
    private final String tag;

    /**
     * Creates a new PlayerTagRemoveEvent.
     *
     * @param who the {@link Profile} whose tag was removed
     * @param tag removed tag
     */
    public PlayerTagRemoveEvent(final Profile who, final String tag) {
        super(who);
        this.tag = tag;
    }

    /**
     * Gets the HandlerList of this event.
     *
     * @return the HandlerList
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

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
        return HANDLERS;
    }
}
