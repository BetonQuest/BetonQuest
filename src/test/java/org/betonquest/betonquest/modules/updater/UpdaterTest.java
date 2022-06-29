package org.betonquest.betonquest.modules.updater;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.betonquest.betonquest.modules.versioning.Version;
import org.betonquest.betonquest.util.scheduler.BukkitSchedulerMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.InstantSource;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class test the {@link Updater}.
 */
@ExtendWith(BetonQuestLoggerService.class)
class UpdaterTest {

    @Test
    void testUpdateAvailable(final LogValidator validator) {
        final ConfigurationFile config = UpdaterConfigTest.getMockedConfig(new UpdaterConfigTest.Input(null, true, true, "PATCH_DEV", false));
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        when(handler.searchUpdate(any(), any(), any())).thenReturn(Pair.of(new Version("2.0.0-DEV-5"), "https://betonquest.org"));

        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(config, version, handler, null, plugin, scheduler, instantSource);
            scheduler.waitAsyncTasksFinished();
            scheduler.assertNoExceptions();
            assertTrue(updater.isUpdateAvailable());
            assertEquals("2.0.0-DEV-5", updater.getUpdateVersion());
        }

        validator.assertLogEntry(Level.INFO, "Found newer version '2.0.0-DEV-5', it will be installed, if you execute '/q update'!");
        validator.assertEmpty();
    }

    @Test
    void testNoUpdateAvailable(final LogValidator validator) {
        final ConfigurationFile config = UpdaterConfigTest.getMockedConfig(new UpdaterConfigTest.Input(null, true, true, "PATCH_DEV", false));
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        when(handler.searchUpdate(any(), any(), any())).thenReturn(Pair.of(new Version("2.0.0-DEV-3"), null));

        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(config, version, handler, null, plugin, scheduler, instantSource);
            scheduler.waitAsyncTasksFinished();
            scheduler.assertNoExceptions();
            assertFalse(updater.isUpdateAvailable());
        }

        validator.assertEmpty();
    }

    @Test
    void testUpdate(final LogValidator validator) {
        final ConfigurationFile config = UpdaterConfigTest.getMockedConfig(new UpdaterConfigTest.Input(null, true, true, "PATCH_DEV", false));
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final UpdateDownloader downloader = mock(UpdateDownloader.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        final Version newVersion = new Version("2.0.0-DEV-5");
        when(handler.searchUpdate(any(), eq(version), any())).thenReturn(Pair.of(newVersion, "https://betonquest.org"));
        when(handler.searchUpdate(any(), eq(newVersion), any())).thenReturn(Pair.of(newVersion, null));

        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(config, version, handler, downloader, plugin, scheduler, instantSource);
            scheduler.waitAsyncTasksFinished();
            scheduler.assertNoExceptions();
            assertTrue(updater.isUpdateAvailable());
            assertEquals("2.0.0-DEV-5", updater.getUpdateVersion());

            updater.update(null);
            scheduler.waitAsyncTasksFinished();
            scheduler.assertNoExceptions();
            assertFalse(updater.isUpdateAvailable());
            assertNull(updater.getUpdateVersion());
        }

        validator.assertLogEntry(Level.INFO, "Found newer version '2.0.0-DEV-5', it will be installed, if you execute '/q update'!");
        validator.assertLogEntry(Level.INFO, "§2Started update to version '2.0.0-DEV-5'...");
        validator.assertLogEntry(Level.INFO, "§2...download finished. Restart the server to update the plugin.");
        validator.assertEmpty();
    }
}
