package org.betonquest.betonquest.api;

import lombok.Getter;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.profiles.ProfileEvent;
import org.bukkit.event.HandlerList;

/**
 * Fired when a tag is added to a profile.
 */
@SuppressWarnings({"PMD.DataClass"})
public class PlayerTagAddEvent extends ProfileEvent {

    /**
     * HandlerList of this event.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * The tag that was added.
     */
    @Getter
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
        return HANDLERS;
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
