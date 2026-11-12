package org.betonquest.betonquest.schedule;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageCache;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test that the LastExecutionCache is properly loading and saving to package cache file.
 */
@ExtendWith(MockitoExtension.class)
class LastExecutionCacheTest {

    /**
     * The current time used in the tests.
     */
    private final Instant now = Instant.now();

    @Mock
    private BetonQuestLogger logger;

    /**
     * The cache to test.
     */
    private LastExecutionCache lastExecutionCache;

    @Mock
    private QuestPackage questPackage;

    @Mock
    private QuestPackageCache packageCache;

    /**
     * ID of the schedule to load and save from.
     */
    @Mock
    private ScheduleIdentifier scheduleID;

    @BeforeEach
    void setUp() {
        lastExecutionCache = new LastExecutionCache(logger);
        lenient().when(scheduleID.getPackage()).thenReturn(questPackage);
        lenient().when(questPackage.getCache()).thenReturn(packageCache);
        lenient().when(questPackage.getQuestPath()).thenReturn("test-package");
    }

    @Test
    void save_io_exception() throws IOException {
        when(scheduleID.get()).thenReturn("testCacheIOException");
        when(packageCache.save()).thenThrow(new IOException("ioexception"));
        lastExecutionCache.cacheExecutionTime(Instant.parse("1970-01-01T00:00:00Z"), scheduleID);
        verify(logger, times(1)).error(eq("Could not save schedule cache for package 'test-package': ioexception"), any(IOException.class));
    }

    @Test
    void raw_execution_time() {
        final String expected = "2022-06-17T08:45:49.000000000Z";
        when(scheduleID.get()).thenReturn("testRawExecutionTime");
        when(packageCache.getString("schedules.testRawExecutionTime")).thenReturn(expected);
        assertEquals(Optional.of(expected), lastExecutionCache.getRawLastExecutionTime(scheduleID), "Cache should return cached time");
    }

    @Test
    void execution_time() {
        final String expected = "1997-02-02T02:02:02.020202020Z";
        when(scheduleID.get()).thenReturn("testExecutionTime");
        when(packageCache.getString("schedules.testExecutionTime")).thenReturn(expected);
        assertEquals(Optional.of(Instant.parse(expected)), lastExecutionCache.getLastExecutionTime(scheduleID), "Cache should return cached time");
    }

    @Test
    void raw_not_cached() {
        when(scheduleID.get()).thenReturn("testRawNotCached");
        when(packageCache.getString("schedules.testRawNotCached")).thenReturn(null);
        assertEquals(Optional.empty(), lastExecutionCache.getRawLastExecutionTime(scheduleID), "Cache should return empty optional");
    }

    @Test
    void not_cached() {
        when(scheduleID.get()).thenReturn("testNotCached");
        when(packageCache.getString("schedules.testNotCached")).thenReturn(null);
        assertEquals(Optional.empty(), lastExecutionCache.getLastExecutionTime(scheduleID), "Cache should return empty optional");
    }

    @Test
    void is_contained() {
        final String expected = "2000-01-01T00:00:00Z";
        when(scheduleID.get()).thenReturn("testIsContained");
        when(packageCache.getString("schedules.testIsContained")).thenReturn(expected);
        assertTrue(lastExecutionCache.isCached(scheduleID), "isCached() should return true");
    }

    @Test
    void is_not_contained() {
        when(scheduleID.get()).thenReturn("testIsNotContained");
        when(packageCache.getString("schedules.testIsNotContained")).thenReturn(null);
        assertFalse(lastExecutionCache.isCached(scheduleID), "isCached() should return false");
    }

    @Test
    void cache_raw() throws IOException {
        final String expected = "2014-10-16T14:28:00Z";
        when(scheduleID.get()).thenReturn("testCacheRaw");
        lastExecutionCache.cacheRawExecutionTime(scheduleID, expected);
        verify(packageCache).set("schedules.testCacheRaw", expected);
        verify(packageCache).save();
    }

    @Test
    void cache_instant() throws IOException {
        final Instant toCache = Instant.parse("1970-01-01T00:00:00Z");
        when(scheduleID.get()).thenReturn("testCacheInstant");
        lastExecutionCache.cacheExecutionTime(toCache, scheduleID);
        verify(packageCache).set("schedules.testCacheInstant", toCache.toString());
        verify(packageCache).save();
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void cache_startup() throws IOException {
        final Instant now = this.now;
        final ScheduleIdentifier newSchedule = mock(ScheduleIdentifier.class);
        final ScheduleIdentifier cachedSchedule = mock(ScheduleIdentifier.class);
        final String expected = "2000-01-01T00:00:00Z";
        when(newSchedule.getPackage()).thenReturn(questPackage);
        when(newSchedule.get()).thenReturn("testCacheStartup-newSchedule");
        when(cachedSchedule.getPackage()).thenReturn(questPackage);
        when(cachedSchedule.get()).thenReturn("testCacheStartup-cachedSchedule");
        when(packageCache.getString("schedules.testCacheStartup-newSchedule")).thenReturn(null);
        when(packageCache.getString("schedules.testCacheStartup-cachedSchedule")).thenReturn(expected);
        lastExecutionCache.cacheStartupTime(now, List.of(newSchedule, cachedSchedule));
        final Instant end = this.now;
        final ArgumentMatcher<String> isCurrentTime = value -> {
            final Instant cachedTime = Instant.parse(value);
            return !cachedTime.isBefore(now) && !cachedTime.isAfter(end);
        };
        verify(packageCache).set(eq("schedules.testCacheStartup-newSchedule"), argThat(isCurrentTime));
        verify(packageCache, never()).set(eq("schedules.testCacheStartup-cachedSchedule"), anyString());
        verify(packageCache).save();
    }
}
