package org.betonquest.betonquest.api.bukkit.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fires after BetonQuest finished loading or reloading all events, conditions, objectives, conversations etc.
 * <p>
 * Useful if you like to have an addon which is reloading whenever BetonQuest is reloading.
 */
public class LoadDataEvent extends Event {

    /**
     * A list of all handlers for this event.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * The state of this event.
     */
    private final State state;

    /**
     * Create a new Load Data Event.
     *
     * @param state the state of this event.
     */
    public LoadDataEvent(final State state) {
        super();
        this.state = state;
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
     * Get the state of this event.
     *
     * @return the state of this event.
     */
    public State getState() {
        return state;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * The state of the LoadDataEvent.
     */
    public enum State {
        /**
         * Event is fired before BetonQuest loads the data.
         */
        PRE_LOAD,
        /**
         * Event is fired after BetonQuest loaded the data.
         */
        POST_LOAD
    }
}
