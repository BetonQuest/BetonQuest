package org.betonquest.betonquest.api;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.profiles.ProfileEvent;
import org.bukkit.event.HandlerList;

/**
 * Fired when the quest data updates.
 */
@SuppressWarnings({"PMD.DataClass", "PMD.CommentRequired"})
public class QuestDataUpdateEvent extends ProfileEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String objID;
    private final String data;

    public QuestDataUpdateEvent(final Profile profile, final String objID, final String data) {
        super(profile);
        this.objID = objID;
        this.data = data;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public String getObjID() {
        return objID;
    }

    public String getData() {
        return data;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
