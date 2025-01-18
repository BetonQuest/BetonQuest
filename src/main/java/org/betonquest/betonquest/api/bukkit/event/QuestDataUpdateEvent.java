package org.betonquest.betonquest.api.bukkit.event;

import org.betonquest.betonquest.api.profiles.Profile;
import org.bukkit.event.HandlerList;

/**
 * Fired when the quest data updates.
 */
@SuppressWarnings("PMD.DataClass")
public class QuestDataUpdateEvent extends ProfileEvent {
    /**
     * A list of all handlers for this event.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Objective id string.
     */
    private final String objID;

    /**
     * Objective data string.
     */
    private final String data;

    /**
     * Create a new Data Update event.
     *
     * @param profile the profile that changed the objective for
     * @param objID   the string representation of the objective id
     * @param data    the string representation of the new objective data
     */
    public QuestDataUpdateEvent(final Profile profile, final String objID, final String data) {
        super(profile);
        this.objID = objID;
        this.data = data;
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
     * Get the objective id.
     *
     * @return the id of the objective that changed
     */
    public String getObjID() {
        return objID;
    }

    /**
     * Get the updated data.
     *
     * @return the new data string
     */
    public String getData() {
        return data;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
