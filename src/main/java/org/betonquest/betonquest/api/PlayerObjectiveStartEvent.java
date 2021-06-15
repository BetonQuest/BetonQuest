package org.betonquest.betonquest.api;

import org.betonquest.betonquest.id.ObjectiveID;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * When player start event.
 */
@SuppressWarnings("PMD.CommentRequired")
public class PlayerObjectiveStartEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Objective objective;

    /**
     * Constructor of PlayerObjectiveStartEvent
     *
     * @param who player who start the event
     * @param objective the objective which just stated
     */
    public PlayerObjectiveStartEvent(final Player who, final Objective objective) {
        super(who);
        this.objective = objective;
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

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
