package org.betonquest.betonquest.api;

import lombok.Getter;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.profiles.ProfileEvent;
import org.betonquest.betonquest.id.EventID;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a BetonQuest event is executed on a {@link Profile}.
 * Use {@link EventExecutedEvent} for events executed without a {@link Profile}.
 */
public class EventExecutedOnProfileEvent extends ProfileEvent {

    /**
     * All registered handlers.
     */
    private static final HandlerList HANDLERS = new HandlerList();
    /**
     * The event that was executed.
     */
    @Getter
    private final EventID eventID;

    /**
     * Constructs a new EventExecutedEvent.
     *
     * @param who     the {@link Profile} for which this event has been executed
     * @param eventID the {@link EventID} of the event that was executed
     */
    public EventExecutedOnProfileEvent(@NotNull final Profile who, final EventID eventID) {
        super(who, true);
        this.eventID = eventID;
    }

    /**
     * Returns all registered handlers for this event.
     *
     * @return all registered handlers for this event
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Returns all registered handlers for this event.
     *
     * @return all registered handlers for this event
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
