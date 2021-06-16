package org.betonquest.betonquest.api;

import org.betonquest.betonquest.id.ObjectiveID;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * When player start event.
 */
public class PlayerObjectiveStartEvent extends PlayerEvent {

    /**
     * HandlerList of this event.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * Objective which is just started of this event.
     */
    private final Objective objective;

    /**
     * Reason of why objective start.
     */
    private final StartCause startCause;

    /**
     * Constructor of PlayerObjectiveStartEvent
     *
     * @param who player who start the event
     * @param objective the objective which just stated
     * @param startCause why is the objective start
     */
    public PlayerObjectiveStartEvent(final Player who, final Objective objective, final StartCause startCause) {
        super(who);
        this.objective = objective;
        this.startCause = startCause;
    }

    /**
     * Reason enum of why objective start.
     */
    public enum StartCause {

        /**
         * Objective start for player (This objective is new for this player).
         */
        START,

        /**
         * Objective resume for player (This objective paused before).
         */
        RESUME
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
     * Get the StartCause of this event. It indicates why this objective started.
     *
     * @return the StartCause
     */
    public StartCause getStartCause() {
        return startCause;
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
