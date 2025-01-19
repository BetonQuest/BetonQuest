package org.betonquest.betonquest.api.bukkit.event;

import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Fired when the compass calls the setCompassTarget method.
 */
@SuppressWarnings("PMD.DataClass")
public class QuestCompassTargetChangeEvent extends ProfileEvent implements Cancellable {
    /**
     * A list of all handlers for this event.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * The target location.
     */
    private final Location location;

    /**
     * Whether the event is cancelled.
     */
    private boolean cancelled;

    /**
     * Create a new event.
     *
     * @param profile  the profile to change the target for
     * @param location the location to target with the compass
     */
    public QuestCompassTargetChangeEvent(final Profile profile, final Location location) {
        super(profile);
        this.location = location;
    }

    /**
     * Get the HandlerList of this event.
     *
     * @return the HandlerList.
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    /**
     * Get the location to set the compass to.
     *
     * @return the target location
     */
    public Location getLocation() {
        return location;
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
    public void setCancelled(final boolean cancel) {
        cancelled = cancel;
    }
}
