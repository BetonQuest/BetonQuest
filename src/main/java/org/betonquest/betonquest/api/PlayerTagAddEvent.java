package org.betonquest.betonquest.api;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * event fired when player's tag is added
 */
@SuppressWarnings({"PMD.DataClass"})
public class PlayerTagAddEvent extends PlayerEvent {
    /**
     * HandlerList of this event.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * added tag.
     */
    @Getter
    private final String tag;

    /**
     * Constructor of PlayerTagAddEvent.
     *
     * @param who the player whose tag has added
     * @param tag added tag
     */
    public PlayerTagAddEvent(final Player who, final String tag) {
        super(who);
        this.tag = tag;
    }

    /**
     * Get the HandlerList of this event.
     *
     * @return the HandlerList
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Get the HandlerList of this event.
     *
     * @return the HandlerList
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
