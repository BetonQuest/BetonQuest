package org.betonquest.betonquest.modules.schedule;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Optional;

/**
 * Cache that holds the last execution time of schedules.
 * Only one instance may exist.
 */
public class LastExecutionCache {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Config accessor for the cache.
     */
    private final ConfigAccessor cache;

    /**
     * Create a new execution cache instance for a given schedule.
     *
     * @param log   the logger that will be used for logging
     * @param cache the config accessor for the cache
     */
    public LastExecutionCache(final BetonQuestLogger log, final ConfigAccessor cache) {
        this.log = log;
        this.cache = cache;
    }

    /**
     * reload execution cache in case it was modified externally.
     */
    public void reload() {
        try {
            cache.reload();
            log.debug("Successfully reloaded schedule cache.");
        } catch (final IOException e) {
            log.error("Could not reload schedule cache: " + e.getMessage(), e);
        }
    }

    /**
     * Save the last execution time of a schedule as raw string to the cache.
     *
     * @param schedule id of the schedule
     * @param rawTime  raw string to cache
     */
    public void cacheRawExecutionTime(final ScheduleID schedule, final String rawTime) {
        cache.getConfig().set(schedule.getFullID(), rawTime);
        try {
            cache.save();
        } catch (final IOException e) {
            log.error("Could not save schedule cache: " + e.getMessage(), e);
        }
    }

    /**
     * Save the last execution time of a schedule to the cache in ISO-8601 format
     * (see {@link DateTimeFormatter#ISO_INSTANT}).
     *
     * @param now      The Instance of now
     * @param schedule id of the schedule
     */
    public void cacheExecutionTime(final Instant now, final ScheduleID schedule) {
        cacheRawExecutionTime(schedule, now.toString());
    }

    /**
     * Get the cached last execution time of a schedule as raw string.
     *
     * @param schedule id of the schedule
     * @return optional containing the cached string, empty if nothing was cached
     */
    public Optional<String> getRawLastExecutionTime(final ScheduleID schedule) {
        return Optional.ofNullable(cache.getConfig().getString(schedule.getFullID()));
    }

    /**
     * Get the cached last execution time of a schedule as Instant.
     *
     * @param schedule id of the schedule
     * @return optional containing the last execution time, empty if no execution time is cached
     * @throws DateTimeParseException if time couldn't be parsed using {@link DateTimeFormatter#ISO_INSTANT}
     */
    public Optional<Instant> getLastExecutionTime(final ScheduleID schedule) {
        return getRawLastExecutionTime(schedule).map(Instant::parse);
    }

    /**
     * Check if the execution time of a schedule was cached.
     *
     * @param scheduleID id of the schedule
     * @return true if cache contains time for that schedule, false otherwise
     */
    public boolean isCached(final ScheduleID scheduleID) {
        return getRawLastExecutionTime(scheduleID).isPresent();
    }

    /**
     * For all schedules that are not in the cache, cache the current time as last execution time.
     * This allows to find missed schedules during shutdown.
     *
     * @param now       The Instant of now
     * @param schedules ids of the schedules to cache
     */
    public void cacheStartupTime(final Instant now, final Collection<ScheduleID> schedules) {
        for (final ScheduleID schedule : schedules) {
            if (!isCached(schedule)) {
                cacheExecutionTime(now, schedule);
            }
        }
    }
}
