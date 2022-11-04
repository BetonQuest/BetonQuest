package org.betonquest.betonquest.api;

import lombok.Getter;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.id.EventID;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a BetonQuest event is executed without a player.
 * Use {@link EventExecutedOnProfileEvent} for events executed on a {@link Profile}.
 */
public class EventExecutedEvent extends Event {

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
     * @param eventID the {@link EventID} of the event that was executed
     */
    public EventExecutedEvent(final EventID eventID) {
        super(true);
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
