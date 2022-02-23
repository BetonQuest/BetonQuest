package org.betonquest.betonquest.api;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerTagRemoveEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    private final String tag;

    public PlayerTagRemoveEvent(final Player who, final String tag) {
        super(who);
        this.tag = tag;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
