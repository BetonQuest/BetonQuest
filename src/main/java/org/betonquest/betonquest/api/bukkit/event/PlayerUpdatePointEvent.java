package org.betonquest.betonquest.api.bukkit.event;

import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.event.HandlerList;

/**
 * Fired when a player's points are updated.
 */
public class PlayerUpdatePointEvent extends ProfileEvent {
    /**
     * HandlerList of this event.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * The category whose points were updated.
     */
    private final String category;

    /**
     * The updated total points count of the involved category.
     */
    private final int newCount;

    /**
     * Creates a new PlayerAddPointEvent.
     *
     * @param who      the {@link Profile} whose points was added
     * @param category the category whose points were updated
     * @param newCount the updated total points count of the involved category
     */
    public PlayerUpdatePointEvent(final Profile who, final String category, final int newCount) {
        super(who);
        this.category = category;
        this.newCount = newCount;
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
     * Gets the category whose points were updated.
     *
     * @return the category whose points were updated
     */
    public String getCategory() {
        return category;
    }

    /**
     * Gets the total updated points count of the involved category.
     *
     * @return the total updated points count of the involved category
     */
    public int getNewCount() {
        return newCount;
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
