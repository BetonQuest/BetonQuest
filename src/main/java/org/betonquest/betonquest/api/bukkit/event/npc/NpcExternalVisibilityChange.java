package org.betonquest.betonquest.api.bukkit.event.npc;

import org.betonquest.betonquest.api.quest.npc.Npc;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

/**
 * Event to call if a Npc is externally modified and its BetonQuest features needs recalibration.
 */
public class NpcExternalVisibilityChange extends Event {
    /**
     * Static HandlerList to register listeners on the event.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Npc to update visibility.
     */
    @Nullable
    private final Npc<?> npc;

    /**
     * Creates an update event.
     *
     * @param npc the npc to update, or null if every Npc should be updated.
     */
    public NpcExternalVisibilityChange(@Nullable final Npc<?> npc) {
        super();
        this.npc = npc;
    }

    /**
     * The static getter as required by the Event specification.
     *
     * @return the handler list to register new listener
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * Gets npc to update or null if every one should be updated.
     *
     * @return the npc to update or null
     */
    @Nullable
    public Npc<?> getNpc() {
        return npc;
    }
}
