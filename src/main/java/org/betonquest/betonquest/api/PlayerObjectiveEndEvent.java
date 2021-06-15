package org.betonquest.betonquest.api;

import org.betonquest.betonquest.id.ObjectiveID;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * When player end event.
 */
@SuppressWarnings("PMD.CommentRequired")
public class PlayerObjectiveEndEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Objective objective;
    private final boolean complete;

    /**
     * Constructor of PlayerObjectiveEndEvent.
     *
     * @param who player who end the event
     * @param objective the objective which just ended
     * @param complete is the objective end because it is complete
     */
    public PlayerObjectiveEndEvent(final Player who, final Objective objective, final boolean complete) {
        super(who);
        this.objective = objective;
        this.complete = complete;
    }

    /**
     * Get this objective.
     *
     * @return the objective
     */
    public Objective getObjective() {
        return objective;
    }

    /**
     * Get the id of this objective.
     *
     * @return the id of this objective
     */
    public ObjectiveID getObjectiveID() {
        return (ObjectiveID) objective.instruction.getID();
    }

    /**
     * Is or not this objective end because it is completed.
     *
     * @return a boolean
     */
    public boolean isObjectiveComplete() {
        return complete;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
