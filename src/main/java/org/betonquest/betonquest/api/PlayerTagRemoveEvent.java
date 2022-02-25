package org.betonquest.betonquest.api;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * This event is fired when a player's tag was removed.
 */
@SuppressWarnings({"PMD.DataClass"})
public class PlayerTagRemoveEvent extends PlayerEvent {

    /**
     * HandlerList of this event.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * The tag that was removed.
     */
    @Getter
    private final String tag;

    /**
     * Creates a new PlayerTagRemoveEvent.
     *
     * @param who the player whose tag was removed
     * @param tag removed tag
     */
    public PlayerTagRemoveEvent(final Player who, final String tag) {
        super(who);
        this.tag = tag;
    }

    /**
     * Gets the HandlerList of this event.
     *
     * @return the HandlerList
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Gets the HandlerList of this event.
     *
     * @return the HandlerList
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
