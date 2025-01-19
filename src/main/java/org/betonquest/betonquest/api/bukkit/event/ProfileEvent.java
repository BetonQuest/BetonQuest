package org.betonquest.betonquest.api.bukkit.event;

import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.event.Event;

/**
 * Represents a profile related event.
 */
public abstract class ProfileEvent extends Event {

    /**
     * The profile this event is for.
     */
    protected Profile profile;

    /**
     * Creates a new profile event.
     * This constructor assumes the event is synchronous
     *
     * @param who the profile
     */
    public ProfileEvent(final Profile who) {
        super();
        this.profile = who;
    }

    /**
     * Creates a new profile event with async setting.
     *
     * @param who     the profile
     * @param isAsync whether the event is async
     */
    public ProfileEvent(final Profile who, final boolean isAsync) {
        super(isAsync);
        this.profile = who;
    }

    /**
     * @return the profile
     */
    public final Profile getProfile() {
        return profile;
    }
}
