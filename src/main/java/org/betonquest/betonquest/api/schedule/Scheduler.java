package org.betonquest.betonquest.api.schedule;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.schedule.ScheduleID;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Superclass of all event schedulers.
 * While {@link Schedule} holds the data and settings of a schedule, children of this class should contain the logic for
 * scheduling of events.
 * </p>
 * <p>
 * When loading the configs,
 * new schedules are parsed and registered in the matching Scheduler by calling {@link #addSchedule(Schedule)}.
 * After everything is loaded {@link #start(Object)} is called. It should start the scheduler.
 * Once a time defined in the schedule is met,
 * the referenced events shall be executed using {@link #executeEvents(Schedule)}.
 * On shutdown or before reloading all data, {@link #stop()} is called to stop all schedules.
 * Also, this class should implement the {@link CatchupStrategy} required by the schedule.
 * </p>
 *
 * @param <S> Type of Schedule
 * @param <T> Type of time used by the scheduler
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class Scheduler<S extends Schedule, T> {
    /**
     * Map containing all schedules that belong to this scheduler.
     */
    protected final Map<ScheduleID, S> schedules;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Flag stating if this scheduler is currently running.
     */
    private boolean running;

    /**
     * Default constructor.
     *
     * @param log the logger that will be used for logging
     */
    public Scheduler(final BetonQuestLogger log) {
        this.log = log;
        schedules = new HashMap<>();
        running = false;
    }

    /**
     * Register a new schedule to the list of schedules managed by this scheduler.
     * The schedule shall remain inactive till method {@link #start(Object)} is called to activate all schedules.
     *
     * @param schedule schedule object to register
     */
    public void addSchedule(final S schedule) {
        schedules.put(schedule.getId(), schedule);
    }

    /**
     * Start all schedules that have been added to this scheduler, in the same way as {@link #start(Object)},
     * but using the current time provided by {@link #getNow()} as the time.
     */
    public void start() {
        start(getNow());
    }

    /**
     * <p>
     * Start all schedules that have been added to this scheduler.
     * This method is called on startup and reload of BetonQuest to activate/resume all schedules.
     * </p>
     * <p>
     * As well as handling the actual scheduling logic this method shall also handle catching up schedules that
     * were missed during reloading or shutdown of the server, based on their {@link CatchupStrategy}.
     * </p>
     * <p><b>
     * When overriding this method, make sure to call {@code super.start()} at some point to update the running flag.
     * </b></p>
     *
     * @param now the current time when the scheduler is started
     */
    public void start(final T now) {
        running = true;
    }

    /**
     * Method to get the current time of the type {@link T} used by the scheduler.
     *
     * @return the current time
     */
    protected abstract T getNow();

    /**
     * <p>
     * Stop the scheduler and unregister all schedules that belong to this scheduler.
     * Typically this method is called on reload and server shutdown.
     * </p>
     * <p><b>
     * When overriding this method, make sure to call {@code super.stop()} at some point to clear the map of schedules.
     * </b></p>
     */
    public void stop() {
        running = false;
        schedules.clear();
    }

    /**
     * This method shall be called whenever the execution time of a schedule is reached.
     * It executes all events that should be run by the schedule.
     *
     * @param schedule a schedule that reached execution time, providing a list of events to run
     */
    protected void executeEvents(final S schedule) {
        log.debug(schedule.getId().getPackage(), "Schedule '" + schedule.getId() + "' runs its events...");
        for (final EventID eventID : schedule.getEvents()) {
            BetonQuest.event(null, eventID);
        }
    }

    /**
     * Check if this scheduler is currently running.
     *
     * @return true if currently running, false if not (e.g. during startup or reloading)
     */
    public boolean isRunning() {
        return running;
    }
}
