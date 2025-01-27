package org.betonquest.betonquest.api.profile;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event that is fired when the active profile changes.
 */
public class ActiveProfileChangedEvent extends Event {
    /**
     * The handler list for the event.
     */
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    /**
     * The old profile.
     */
    private final Profile oldProfile;

    /**
     * The new profile.
     */
    private final OnlineProfile newProfile;

    /**
     * Creates a new ActiveProfileChangedEvent.
     *
     * @param oldProfile the old profile
     * @param newProfile the new profile
     */
    public ActiveProfileChangedEvent(final Profile oldProfile, final OnlineProfile newProfile) {
        super(false);
        this.oldProfile = oldProfile;
        this.newProfile = newProfile;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    /**
     * Gets the handler list for the event.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public OnlineProfile getNewProfile() {
        return newProfile;
    }

    public Profile getOldProfile() {
        return oldProfile;
    }
}
