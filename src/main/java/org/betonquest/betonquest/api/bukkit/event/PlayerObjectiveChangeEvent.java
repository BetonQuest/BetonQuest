package org.betonquest.betonquest.api.bukkit.event;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.id.ID;
import org.bukkit.event.HandlerList;

/**
 * Fires when a profile's objectives change.
 */
public class PlayerObjectiveChangeEvent extends ProfileEvent {

    /**
     * HandlerList of this event.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Objective which will change of this event.
     */
    private final Objective objective;

    /**
     * ID of the changed objective.
     */
    private final ID objectiveID;

    /**
     * Objective future state.
     */
    private final Objective.ObjectiveState state;

    /**
     * Objective previous state.
     */
    private final Objective.ObjectiveState previousState;

    /**
     * Constructor of PlayerObjectiveChangeEvent.
     *
     * @param who           {@link Profile} who change this objective
     * @param objective     objective which will be changed
     * @param objectiveID   the objective id
     * @param state         future state of this objective
     * @param previousState previous state of this objective
     */
    public PlayerObjectiveChangeEvent(final Profile who, final Objective objective, final ID objectiveID,
                                      final Objective.ObjectiveState state, final Objective.ObjectiveState previousState) {
        super(who);
        this.objective = objective;
        this.objectiveID = objectiveID;
        this.state = state;
        this.previousState = previousState;
    }

    /**
     * Get the HandlerList of this event.
     *
     * @return the HandlerList
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    /**
     * Get the objective which will be changed.
     *
     * @return the objective
     */
    public Objective getObjective() {
        return objective;
    }

    /**
     * Get the ID of the objective which will be changed.
     *
     * @return the objective ID
     */
    public ID getObjectiveID() {
        return objectiveID;
    }

    /**
     * Get the future state of the objective.
     *
     * @return the future state
     */
    public Objective.ObjectiveState getState() {
        return state;
    }

    /**
     * Get the previous state of the objective.
     *
     * @return the previous state
     */
    public Objective.ObjectiveState getPreviousState() {
        return previousState;
    }

    /**
     * Get the HandlerList of this event.
     *
     * @return the HandlerList
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
