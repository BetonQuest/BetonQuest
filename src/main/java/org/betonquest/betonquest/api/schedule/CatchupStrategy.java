package org.betonquest.betonquest.api.schedule;

/**
 * Defines how the scheduler should behave if an execution of a schedule was missed
 * (e.g. due to a shutdown of the server).
 */
public enum CatchupStrategy {

    /**
     * Do not catch up any missed schedules.
     */
    NONE,

    /**
     * Catch up only once, even if schedule was missed multiple times.
     */
    ONE,

    /**
     * Catch up all missed schedules.
     */
    ALL
}
