package org.betonquest.betonquest.schedule;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestConsumer;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageCache;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test that the LastExecutionCache is properly loading and saving to package cache file.
 */
@ExtendWith(MockitoExtension.class)
class LastExecutionCacheTest {

    /**
     * The current time used in the tests.
     */
    private final Instant now = Instant.now();

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
        lastExecutionCache = new LastExecutionCache();
        lenient().when(scheduleID.getPackage()).thenReturn(questPackage);
        lenient().when(questPackage.getCache()).thenReturn(packageCache);
        lenient().when(questPackage.getQuestPath()).thenReturn("test-package");
    }

    @Test
    void raw_execution_time() {
        final String expected = "2022-06-17T08:45:49.000000000Z";
        final ConfigurationSection section = mock(ConfigurationSection.class);
        when(scheduleID.get()).thenReturn("testRawExecutionTime");
        when(packageCache.read("schedules")).thenReturn(Optional.of(section));
        when(section.getString("testRawExecutionTime")).thenReturn(expected);
        assertEquals(Optional.of(expected), lastExecutionCache.getRawLastExecutionTime(scheduleID), "Cache should return cached time");
    }

    @Test
    void execution_time() {
        final String expected = "1997-02-02T02:02:02.020202020Z";
        final ConfigurationSection section = mock(ConfigurationSection.class);
        when(scheduleID.get()).thenReturn("testExecutionTime");
        when(packageCache.read("schedules")).thenReturn(Optional.of(section));
        when(section.getString("testExecutionTime")).thenReturn(expected);
        assertEquals(Optional.of(Instant.parse(expected)), lastExecutionCache.getLastExecutionTime(scheduleID), "Cache should return cached time");
    }

    @Test
    void raw_not_cached() {
        when(packageCache.read("schedules")).thenReturn(Optional.empty());
        assertEquals(Optional.empty(), lastExecutionCache.getRawLastExecutionTime(scheduleID), "Cache should return empty optional");
    }

    @Test
    void not_cached() {
        when(packageCache.read("schedules")).thenReturn(Optional.empty());
        assertEquals(Optional.empty(), lastExecutionCache.getLastExecutionTime(scheduleID), "Cache should return empty optional");
    }

    @Test
    void is_contained() {
        final String expected = "2000-01-01T00:00:00Z";
        final ConfigurationSection section = mock(ConfigurationSection.class);
        when(scheduleID.get()).thenReturn("testIsContained");
        when(packageCache.read("schedules")).thenReturn(Optional.of(section));
        when(section.getString("testIsContained")).thenReturn(expected);
        assertTrue(lastExecutionCache.isCached(scheduleID), "isCached() should return true");
    }

    @Test
    void is_not_contained() {
        when(packageCache.read("schedules")).thenReturn(Optional.empty());
        assertFalse(lastExecutionCache.isCached(scheduleID), "isCached() should return false");
    }

    @Test
    @SuppressWarnings("unchecked")
    void cache_raw() throws QuestException {
        final String expected = "2014-10-16T14:28:00Z";
        when(scheduleID.get()).thenReturn("testCacheRaw");
        lastExecutionCache.cacheRawExecutionTime(scheduleID, expected);

        final ArgumentCaptor<QuestConsumer<ConfigurationSection>> captor = ArgumentCaptor.forClass(QuestConsumer.class);
        verify(packageCache).write(eq("schedules"), captor.capture());

        final ConfigurationSection section = mock(ConfigurationSection.class);
        captor.getValue().accept(section);
        verify(section).set("testCacheRaw", expected);
    }

    @Test
    @SuppressWarnings("unchecked")
    void cache_instant() throws QuestException {
        final Instant toCache = Instant.parse("1970-01-01T00:00:00Z");
        when(scheduleID.get()).thenReturn("testCacheInstant");
        lastExecutionCache.cacheExecutionTime(toCache, scheduleID);

        final ArgumentCaptor<QuestConsumer<ConfigurationSection>> captor = ArgumentCaptor.forClass(QuestConsumer.class);
        verify(packageCache).write(eq("schedules"), captor.capture());

        final ConfigurationSection section = mock(ConfigurationSection.class);
        captor.getValue().accept(section);
        verify(section).set("testCacheInstant", toCache.toString());
    }

    @SuppressWarnings({"PMD.UnitTestContainsTooManyAsserts", "unchecked"})
    @Test
    void cache_startup() throws QuestException {
        final Instant now = this.now;
        final ScheduleIdentifier newSchedule = mock(ScheduleIdentifier.class);
        final ScheduleIdentifier cachedSchedule = mock(ScheduleIdentifier.class);
        final ConfigurationSection section = mock(ConfigurationSection.class);
        final String expected = "2000-01-01T00:00:00Z";
        when(newSchedule.getPackage()).thenReturn(questPackage);
        when(newSchedule.get()).thenReturn("testCacheStartup-newSchedule");
        when(cachedSchedule.getPackage()).thenReturn(questPackage);
        when(cachedSchedule.get()).thenReturn("testCacheStartup-cachedSchedule");
        when(packageCache.read("schedules")).thenReturn(Optional.of(section));
        when(section.getString("testCacheStartup-newSchedule")).thenReturn(null);
        when(section.getString("testCacheStartup-cachedSchedule")).thenReturn(expected);

        lastExecutionCache.cacheStartupTime(now, List.of(newSchedule, cachedSchedule));

        final ArgumentCaptor<QuestConsumer<ConfigurationSection>> captor = ArgumentCaptor.forClass(QuestConsumer.class);
        verify(packageCache, times(1)).write(eq("schedules"), captor.capture());

        final ConfigurationSection newSection = mock(ConfigurationSection.class);
        captor.getValue().accept(newSection);

        final Instant end = this.now;
        final ArgumentMatcher<String> isCurrentTime = value -> {
            final Instant cachedTime = Instant.parse(value);
            return !cachedTime.isBefore(now) && !cachedTime.isAfter(end);
        };
        verify(newSection).set(eq("testCacheStartup-newSchedule"), argThat(isCurrentTime));
        verify(newSection, never()).set(eq("testCacheStartup-cachedSchedule"), anyString());
    }
}
