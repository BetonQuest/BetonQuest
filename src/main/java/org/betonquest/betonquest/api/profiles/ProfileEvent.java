package org.betonquest.betonquest.api.profiles;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

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
    public ProfileEvent(@NotNull final Profile who) {
        super();
        this.profile = who;
    }

    /**
     * Creates a new profile event with async setting.
     *
     * @param who     the profile
     * @param isAsync whether the event is async
     */
    public ProfileEvent(@NotNull final Profile who, final boolean isAsync) {
        super(isAsync);
        this.profile = who;
    }

    /**
     * @return the profile
     */
    @NotNull
    public final Profile getProfile() {
        return profile;
    }
}
