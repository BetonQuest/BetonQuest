package org.betonquest.betonquest.api;

import org.betonquest.betonquest.api.bukkit.event.ProfileEvent;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.objective.EntityInteractObjective.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Event for interaction with BetonQuest {@link Npc}s.
 */
@SuppressWarnings("PMD.DataClass")
public class NpcInteractEvent extends ProfileEvent implements Cancellable {
    /**
     * Static HandlerList to register listeners on tih event.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * Player instance who interacted.
     */
    private final Player player;

    /**
     * Instruction string of the Npc.
     */
    private final String npcIdentifier;

    /**
     * Interaction done with the Npc.
     */
    private final Interaction interaction;

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
     * @param profile       the profile of the player who interacted
     * @param player        the player who interacted
     * @param npc           the interacted npc
     * @param npcIdentifier the identifier as used inside the Npc section
     * @param interaction   the type of interaction with the Npc, left or right
     * @param isAsync       if the trigger and so this is async
     * @throws IllegalArgumentException if {@code interaction == Interaction.ANY}
     */
    public NpcInteractEvent(final Profile profile, final Player player, final Npc<?> npc, final String npcIdentifier,
                            final Interaction interaction, final boolean isAsync) {
        super(profile, isAsync);
        if (interaction == Interaction.ANY) {
            throw new IllegalArgumentException("interaction cannot be 'any'");
        }
        this.player = player;
        this.npcIdentifier = npcIdentifier;
        this.interaction = interaction;
        this.npc = npc;
    }

    /**
     * The static getter as required by the Event specification.
     *
     * @return the handler list to register new listener
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Gets the player instance which interacted.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the interacted Npc.
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

    /**
     * Gets the action done on the Npc.
     *
     * @return the interaction type
     */
    public Interaction getInteraction() {
        return interaction;
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
