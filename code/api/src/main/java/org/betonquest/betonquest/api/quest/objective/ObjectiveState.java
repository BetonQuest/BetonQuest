package org.betonquest.betonquest.api.quest.objective;

/**
 * Represents the states of an objective.
 *
 * @since 3.0.0
 */
public enum ObjectiveState {

    /**
     * The objective is new and has not existed before.
     *
     * @since 3.0.0
     */
    NEW,

    /**
     * The objective is active.
     *
     * @since 3.0.0
     */
    ACTIVE,

    /**
     * The objective is complete.
     *
     * @since 3.0.0
     */
    COMPLETED,

    /**
     * The objective is paused.
     *
     * @since 3.0.0
     */
    PAUSED,

    /**
     * The objective is canceled.
     *
     * @since 3.0.0
     */
    CANCELED,
}
