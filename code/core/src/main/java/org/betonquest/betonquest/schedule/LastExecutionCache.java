package org.betonquest.betonquest.schedule;

import org.betonquest.betonquest.api.config.quest.QuestPackageCache;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;
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
     * Path prefix in the package cache configuration where schedules are stored.
     */
    private static final String SCHEDULES_PREFIX = "schedules.";

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Create a new execution cache instance.
     *
     * @param log the logger that will be used for logging
     */
    public LastExecutionCache(final BetonQuestLogger log) {
        this.log = log;
    }

    /**
     * Save the last execution time of a schedule as raw string to the cache.
     *
     * @param schedule id of the schedule
     * @param rawTime  raw string to cache
     */
    public void cacheRawExecutionTime(final ScheduleIdentifier schedule, final String rawTime) {
        final QuestPackageCache cache = schedule.getPackage().getCache();
        cache.set(SCHEDULES_PREFIX + schedule.get(), rawTime);
        try {
            cache.save();
        } catch (final IOException e) {
            log.error("Could not save schedule cache for package '" + schedule.getPackage().getQuestPath() + "': " + e.getMessage(), e);
        }
    }

    /**
     * Save the last execution time of a schedule to the cache in ISO-8601 format
     * (see {@link DateTimeFormatter#ISO_INSTANT}).
     *
     * @param now      The Instance of now
     * @param schedule id of the schedule
     */
    public void cacheExecutionTime(final Instant now, final ScheduleIdentifier schedule) {
        cacheRawExecutionTime(schedule, now.toString());
    }

    /**
     * Get the cached last execution time of a schedule as raw string.
     *
     * @param schedule id of the schedule
     * @return optional containing the cached string, empty if nothing was cached
     */
    public Optional<String> getRawLastExecutionTime(final ScheduleIdentifier schedule) {
        return Optional.ofNullable(schedule.getPackage().getCache().getString(SCHEDULES_PREFIX + schedule.get()));
    }

    /**
     * Get the cached last execution time of a schedule as Instant.
     *
     * @param schedule id of the schedule
     * @return optional containing the last execution time, empty if no execution time is cached
     * @throws DateTimeParseException if time couldn't be parsed using {@link DateTimeFormatter#ISO_INSTANT}
     */
    public Optional<Instant> getLastExecutionTime(final ScheduleIdentifier schedule) {
        return getRawLastExecutionTime(schedule).map(Instant::parse);
    }

    /**
     * Check if the execution time of a schedule was cached.
     *
     * @param scheduleID id of the schedule
     * @return true if cache contains time for that schedule, false otherwise
     */
    public boolean isCached(final ScheduleIdentifier scheduleID) {
        return getRawLastExecutionTime(scheduleID).isPresent();
    }

    /**
     * For all schedules that are not in the cache, cache the current time as last execution time.
     * This allows to find missed schedules during shutdown.
     *
     * @param now       The Instant of now
     * @param schedules ids of the schedules to cache
     */
    public void cacheStartupTime(final Instant now, final Collection<ScheduleIdentifier> schedules) {
        for (final ScheduleIdentifier schedule : schedules) {
            if (!isCached(schedule)) {
                cacheExecutionTime(now, schedule);
            }
        }
    }
}
