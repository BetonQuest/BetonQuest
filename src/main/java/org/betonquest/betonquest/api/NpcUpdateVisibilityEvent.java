package org.betonquest.betonquest.api;

import org.betonquest.betonquest.api.quest.npc.Npc;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

/**
 * Event to call when a Npc attempts to update visibility.
 */
@SuppressWarnings("PMD.DataClass")
public class NpcUpdateVisibilityEvent extends Event implements Cancellable {
    /**
     * Static HandlerList to register listeners on the event.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * The player it updated, null if unspecific/for all.
     */
    @Nullable
    private final Player player;

    /**
     * Instruction string of the Npc.
     */
    private final String npcIdentifier;

    /**
     * Interacted Npc.
     */
    private final Npc<?> npc;

    /**
     * If the event should be cancelled.
     */
    private boolean cancelled;

    /**
     * Create a new Npc Interact Event with a player.
     *
     * @param player        the player who got the update
     * @param npc           the npc which attempts to update the visibility
     * @param npcIdentifier the identifier as used inside the Npc section
     * @param isAsync       if the trigger and so this is async
     */
    public NpcUpdateVisibilityEvent(@Nullable final Player player, final String npcIdentifier, final Npc<?> npc, final boolean isAsync) {
        super(isAsync);
        this.player = player;
        this.npcIdentifier = npcIdentifier;
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
     * Gets the player which got the update.
     *
     * @return the player
     */
    @Nullable
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the Npc.
     *
     * @return the npc
     */
    public Npc<?> getNpc() {
        return npc;
    }

    /**
     * Gets the identifier which would be used to get this Npc through an instruction.
     *
     * @return the instruction string
     */
    public String getNpcIdentifier() {
        return npcIdentifier;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }
}
