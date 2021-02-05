package org.betonquest.betonquest.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Should be fired when the quest data updates
 */
@SuppressWarnings({"PMD.DataClass", "PMD.CommentRequired"})
public class QuestDataUpdateEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String playerID;
    private final String objID;
    private final String data;

    public QuestDataUpdateEvent(final String playerID, final String objID, final String data) {
        super();
        this.playerID = playerID;
        this.objID = objID;
        this.data = data;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public String getPlayerID() {
        return playerID;
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
