package org.betonquest.betonquest.api;

import lombok.Getter;
import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.Pointer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerTagAddEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    private final String tag;

    public PlayerTagAddEvent(final Player who, final String tag) {
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
