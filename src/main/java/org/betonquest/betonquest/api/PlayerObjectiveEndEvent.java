package org.betonquest.betonquest.api;

import org.betonquest.betonquest.id.ObjectiveID;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * When player end event.
 */
public class PlayerObjectiveEndEvent extends PlayerEvent {

    /**
     * HandlerList of this event.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * Objective which is just ended of this event.
     */
    private final Objective objective;

    /**
     * Reason of why objective end.
     */
    private final EndCause endCause;

    /**
     * Constructor of PlayerObjectiveEndEvent.
     *
     * @param who player who end the event
     * @param objective the objective which just ended
     * @param endCause why is the objective end
     */
    public PlayerObjectiveEndEvent(final Player who, final Objective objective, final EndCause endCause) {
        super(who);
        this.objective = objective;
        this.endCause = endCause;
    }

    /**
     * Reason enum of why objective end.
     */
    public enum EndCause {

        /**
         * Objective end because it is completed.
         */
        COMPLETE,

        /**
         * Objective end because it is canceled.
         */
        CANCEL,

        /**
         * Objective end because it is paused.
         */
        PAUSE
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
     * Get the EndCause of this event. It indicates why this objective ended.
     *
     * @return the EndCause
     */
    public EndCause getEndCause() {
        return endCause;
    }

    /**
     * Get the HandlerList of this event.
     *
     * @return the HandlerList.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Get the HandlerList of this event.
     *
     * @return the HandlerList.
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
