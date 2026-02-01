package org.betonquest.betonquest.api.quest.objective;

/**
 * Represents the states of an objective.
 */
public enum ObjectiveState {

    /**
     * The objective is new and has not existed before.
     */
    NEW,

    /**
     * The objective is active.
     */
    ACTIVE,

    /**
     * The objective is complete.
     */
    COMPLETED,

    /**
     * The objective is paused.
     */
    PAUSED,

    /**
     * The objective is canceled.
     */
    CANCELED,
}
