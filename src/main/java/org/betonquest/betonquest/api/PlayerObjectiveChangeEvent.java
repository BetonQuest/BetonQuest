package org.betonquest.betonquest.api;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.profiles.ProfileEvent;
import org.betonquest.betonquest.id.ObjectiveID;
import org.bukkit.event.HandlerList;

/**
 * Fires when a profile's objectives change.
 */
@SuppressWarnings("PMD.DataClass")
public class PlayerObjectiveChangeEvent extends ProfileEvent {

    /**
     * HandlerList of this event.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * Objective which will change of this event.
     */
    private final Objective objective;

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
     * @param state         future state of this objective
     * @param previousState previous state of this objective
     */
    public PlayerObjectiveChangeEvent(final Profile who, final Objective objective,
                                      final Objective.ObjectiveState state, final Objective.ObjectiveState previousState) {
        super(who);
        this.objective = objective;
        this.state = state;
        this.previousState = previousState;
    }

    /**
     * Get the HandlerList of this event.
     *
     * @return the HandlerList
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
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
    public ObjectiveID getObjectiveID() {
        return (ObjectiveID) objective.instruction.getID();
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
        return HANDLERS;
    }
}
