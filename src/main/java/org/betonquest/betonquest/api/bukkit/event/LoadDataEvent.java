package org.betonquest.betonquest.api.bukkit.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fires after BetonQuest finished loading or reloading all events, conditions, objectives, conversations etc.
 * <p>
 * Useful if you like to have an addon which is reloading whenever BetonQuest is reloading.
 */
@SuppressWarnings("PMD.CommentRequired")
public class LoadDataEvent extends Event {
    /**
     * A list of all handlers for this event.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Create a new Load Data Event.
     */
    public LoadDataEvent() {
        super();
    }

    /**
     * Get the HandlerList of this event.
     *
     * @return the HandlerList.
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
