package org.betonquest.betonquest.api;


import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fires after BetonQuest finished loading or reloading all events, conditions, objectives, conversations etc.
 * <p>
 * Usefull if you like to have a addon which is reloading whenever BetonQuest is reloading.
 */
@SuppressWarnings("PMD.CommentRequired")
public class LoadDataEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public LoadDataEvent() {
        super();
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
