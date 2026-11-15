package org.betonquest.betonquest.lib.config.quest;

import com.google.common.base.Supplier;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.LogSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link QuestPackageCacheImpl}.
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
class QuestPackageCacheImplTest {

    @Mock
    private FileConfigAccessor accessor;

    @Mock
    private BetonQuestLogger log;

    @Mock
    private LogSource logSource;

    private YamlConfiguration config;

    private QuestPackageCacheImpl cache;

    @BeforeEach
    void setUp() {
        config = new YamlConfiguration();
        lenient().when(accessor.getConfig()).thenReturn(config);
        lenient().when(accessor.isConfigurationSection(anyString()))
                .thenAnswer(invocation -> config.isConfigurationSection(invocation.getArgument(0)));
        lenient().when(accessor.getConfigurationSection(anyString()))
                .thenAnswer(invocation -> config.getConfigurationSection(invocation.getArgument(0)));
        lenient().when(accessor.createSection(anyString()))
                .thenAnswer(invocation -> config.createSection(invocation.getArgument(0)));
        cache = new QuestPackageCacheImpl(() -> accessor, log, logSource);
    }

    @Test
    void cache_success() throws IOException {
        final ConfigurationSection section = config.createSection("schedules");
        section.set("testSchedule", "2026-11-15T00:00:00Z");

        final AtomicBoolean consumerExecuted = new AtomicBoolean(false);
        cache.write("schedules", cacheSection -> {
            consumerExecuted.set(true);
            assertEquals("2026-11-15T00:00:00Z", cacheSection.getString("testSchedule"),
                    "Section should contain the configured values");
            cacheSection.set("testSchedule", "2026-11-15T12:00:00Z");
        });

        assertTrue(consumerExecuted.get(), "Consumer should have been executed");
        assertEquals("2026-11-15T12:00:00Z", config.getString("schedules.testSchedule"),
                "Values modified in consumer should be reflected in config");
        verify(accessor).save();
    }

    @Test
    void cache_handles_consumer_exception() throws IOException {
        final QuestException exception = new QuestException("Consumer error");
        cache.write("schedules", cacheSection -> {
            throw exception;
        });

        verify(log).warn(eq(logSource), argThat(msg -> msg != null && msg.contains("Consumer error")), eq(exception));
        verify(accessor).save();
    }

    @Test
    void cache_handles_save_IOException() throws IOException {
        final IOException ioException = new IOException("Disk full");
        when(accessor.save()).thenThrow(ioException);

        cache.write("schedules", cacheSection -> {
        });

        verify(log).warn(eq(logSource), argThat(msg -> msg != null && msg.contains("Disk full")), eq(ioException));
    }

    @Test
    @SuppressWarnings("unchecked")
    void lazy_supplier_initialization() throws IOException {
        final Supplier<FileConfigAccessor> supplier = mock(Supplier.class);
        when(supplier.get()).thenReturn(accessor);

        final QuestPackageCacheImpl lazyCache = new QuestPackageCacheImpl(supplier, log, logSource);
        verify(supplier, never()).get();

        lazyCache.write("schedules", cacheSection -> {
        });
        verify(supplier, times(1)).get();

        lazyCache.write("schedules", cacheSection -> {
        });
        verify(supplier, times(1)).get();
        verify(accessor, times(2)).save();
    }

    @Test
    void get_cache_success() {
        final ConfigurationSection section = config.createSection("schedules");
        section.set("testSchedule", "2026-11-15T00:00:00Z");

        final ConfigurationSection retrieved = cache.read("schedules").orElseThrow();
        assertNotNull(retrieved, "Retrieved section should not be null");
        assertEquals("2026-11-15T00:00:00Z", retrieved.getString("testSchedule"),
                "Section should contain the configured values");
        assertThrows(UnsupportedOperationException.class, () -> retrieved.set("testSchedule", "newVal"),
                "Retrieved section should be unmodifiable");
    }

    @Test
    void get_cache_returns_empty_if_absent() {
        final Optional<ConfigurationSection> retrieved = cache.read("absentKey");
        assertTrue(retrieved.isEmpty(), "getCache should return an empty optional if not previously existing");
    }

    @Test
    void null_accessor_throws_IllegalStateException() {
        final QuestPackageCacheImpl nullCache = new QuestPackageCacheImpl(() -> null, log, logSource);
        assertThrows(IllegalStateException.class, () ->
                nullCache.write("schedules", cacheSection -> {
                }), "Null accessor should throw IllegalStateException in cache()");
        assertThrows(IllegalStateException.class, () ->
                nullCache.read("schedules"), "Null accessor should throw IllegalStateException in getCache()");
    }
}
