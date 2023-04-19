package org.betonquest.betonquest.modules.schedule;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test that the LastExecutionCache is properly loading & saving to cache file.
 */
@ExtendWith(MockitoExtension.class)
@ExtendWith(BetonQuestLoggerService.class)
class LastExecutionCacheTest {

    /**
     * The cache to test.
     */
    private LastExecutionCache lastExecutionCache;

    /**
     * Config Accessor used by the cache to access the file.
     */
    @Mock
    private ConfigAccessor cacheAccessor;

    /**
     * Config provided by the {@link #cacheAccessor} to write & read from the cache.
     */
    @Mock
    private YamlConfiguration cacheContent;

    /**
     * ID of the schedule to load and save from.
     */
    @Mock
    private ScheduleID scheduleID;

    @BeforeEach
    void setUp() {
        try (MockedStatic<ConfigAccessor> configAccessor = mockStatic(ConfigAccessor.class);
             MockedStatic<Files> files = mockStatic(Files.class)) {
            lenient().when(cacheAccessor.getConfig()).thenReturn(cacheContent);
            configAccessor.when(() -> ConfigAccessor.create(any(File.class))).thenReturn(cacheAccessor);
            files.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            lastExecutionCache = new LastExecutionCache(new File("."));
        }
    }

    @Test
    void testLoadIOException(final LogValidator validator) {
        try (MockedStatic<ConfigAccessor> configAccessor = mockStatic(ConfigAccessor.class);
             MockedStatic<Files> files = mockStatic(Files.class)) {
            lenient().when(cacheAccessor.getConfig()).thenReturn(cacheContent);
            configAccessor.when(() -> ConfigAccessor.create(any(File.class))).thenThrow(new FileNotFoundException("FileNotFound"));
            files.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            lastExecutionCache = new LastExecutionCache(new File("."));
            validator.assertLogEntry(Level.SEVERE, "(Cache) Error while loading schedule cache: FileNotFound");
            final Optional<Instant> result = lastExecutionCache.getLastExecutionTime(scheduleID);
            assertEquals(Optional.empty(), result, "result should be empty");
            validator.assertLogEntry(Level.SEVERE, "(Cache) Schedule cache not present!");
        }
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void testSaveIOException(final LogValidator validator) throws IOException {
        when(scheduleID.getFullID()).thenReturn("test-package.testCacheIOException");
        when(cacheAccessor.save()).thenThrow(new IOException("ioexception"));
        lastExecutionCache.cacheExecutionTime(scheduleID, Instant.parse("1970-01-01T00:00:00Z"));
        validator.assertLogEntry(Level.SEVERE, "(Cache) Could not save schedule cache: ioexception");
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void testReloadIOException(final LogValidator validator) throws IOException {
        when(cacheAccessor.reload()).thenThrow(new IOException("ioexception"));
        lastExecutionCache.reload();
        validator.assertLogEntry(Level.SEVERE, "(Cache) Could not reload schedule cache: ioexception");
    }

    @Test
    void testRawExecutionTime() {
        final String expected = "2022-06-17T08:45:49.000000000Z";
        when(scheduleID.getFullID()).thenReturn("test-package.testRawExecutionTime");
        when(cacheContent.getString("test-package.testRawExecutionTime")).thenReturn(expected);
        assertEquals(Optional.of(expected), lastExecutionCache.getRawLastExecutionTime(scheduleID), "Cache should return cached time");
    }

    @Test
    void testExecutionTime() {
        final String expected = "1997-02-02T02:02:02.020202020Z";
        when(scheduleID.getFullID()).thenReturn("test-package.testExecutionTime");
        when(cacheContent.getString("test-package.testExecutionTime")).thenReturn(expected);
        assertEquals(Optional.of(Instant.parse(expected)), lastExecutionCache.getLastExecutionTime(scheduleID), "Cache should return cached time");
    }

    @Test
    void testRawNotCached() {
        when(scheduleID.getFullID()).thenReturn("test-package.testRawNotCached");
        when(cacheContent.getString("test-package.testRawNotCached")).thenReturn(null);
        assertEquals(Optional.empty(), lastExecutionCache.getRawLastExecutionTime(scheduleID), "Cache should return empty optional");
    }

    @Test
    void testNotCached() {
        when(scheduleID.getFullID()).thenReturn("test-package.testNotCached");
        when(cacheContent.getString("test-package.testNotCached")).thenReturn(null);
        assertEquals(Optional.empty(), lastExecutionCache.getLastExecutionTime(scheduleID), "Cache should return empty optional");
    }

    @Test
    void testIsContained() {
        final String expected = "2000-01-01T00:00:00Z";
        when(scheduleID.getFullID()).thenReturn("test-package.testIsContained");
        when(cacheContent.getString("test-package.testIsContained")).thenReturn(expected);
        assertTrue(lastExecutionCache.isCached(scheduleID), "isCached() should return true");
    }

    @Test
    void testIsNotContained() {
        when(scheduleID.getFullID()).thenReturn("test-package.testIsNotContained");
        when(cacheContent.getString("test-package.testIsNotContained")).thenReturn(null);
        assertFalse(lastExecutionCache.isCached(scheduleID), "isCached() should return false");
    }

    @Test
    void testCacheRaw() throws IOException {
        final String expected = "2014-10-16T14:28:00Z";
        when(scheduleID.getFullID()).thenReturn("test-package.testCacheRaw");
        lastExecutionCache.cacheRawExecutionTime(scheduleID, expected);
        verify(cacheContent).set("test-package.testCacheRaw", expected);
        verify(cacheAccessor).save();
    }

    @Test
    void testCacheInstant() throws IOException {
        final Instant toCache = Instant.parse("1970-01-01T00:00:00Z");
        when(scheduleID.getFullID()).thenReturn("test-package.testCacheInstant");
        lastExecutionCache.cacheExecutionTime(scheduleID, toCache);
        verify(cacheContent).set("test-package.testCacheInstant", toCache.toString());
        verify(cacheAccessor).save();
    }

    @Test
    void reload() throws IOException {
        lastExecutionCache.reload();
        verify(cacheAccessor).reload();
    }
}
