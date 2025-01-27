package org.betonquest.betonquest.api.profile;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ActiveProfileChangedEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final Profile oldProfile;
    
    private final OnlineProfile newProfile;

    public ActiveProfileChangedEvent(final Profile oldProfile, final OnlineProfile newProfile) {
        this.oldProfile = oldProfile;
        this.newProfile = newProfile;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public OnlineProfile getNewProfile() {
        return newProfile;
    }

    public Profile getOldProfile() {
        return oldProfile;
    }
}
