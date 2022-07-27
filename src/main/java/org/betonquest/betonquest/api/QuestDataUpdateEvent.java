package org.betonquest.betonquest.api;

import org.betonquest.betonquest.api.profiles.Profile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Should be fired when the quest data updates
 */
@SuppressWarnings({"PMD.DataClass", "PMD.CommentRequired"})
public class QuestDataUpdateEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Profile profile;
    private final String objID;
    private final String data;

    public QuestDataUpdateEvent(final Profile profile, final String objID, final String data) {
        super();
        this.profile = profile;
        this.objID = objID;
        this.data = data;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Profile getProfile() {
        return profile;
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
