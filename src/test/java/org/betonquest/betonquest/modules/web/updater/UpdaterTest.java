package org.betonquest.betonquest.modules.web.updater;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.betonquest.betonquest.modules.versioning.Version;
import org.betonquest.betonquest.util.scheduler.BukkitSchedulerMock;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;

import java.time.InstantSource;
import java.util.UUID;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class tests the {@link Updater}.
 */
@ExtendWith(BetonQuestLoggerService.class)
class UpdaterTest {

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testUpdateAvailable(final LogValidator validator) {
        final ConfigurationFile config = UpdaterConfigTest.getMockedConfig(new UpdaterConfigTest.Input(null, true, false, "PATCH_DEV", false));
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        when(handler.searchUpdate(any(), any(), any())).thenReturn(Pair.of(new Version("2.0.0-DEV-5"), "https://betonquest.org"));

        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(config, version, handler, null, plugin, scheduler, instantSource);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertTrue(updater.isUpdateAvailable(), "Expected update available");
            assertEquals("2.0.0-DEV-5", updater.getUpdateVersion(), "Expected versions do not match");
        }

        validator.assertLogEntry(Level.INFO, "Found newer version '2.0.0-DEV-5', it will be installed, if you execute '/q update'!");
        validator.assertEmpty();
    }

    @Test
    void testNoUpdateAvailable(final LogValidator validator) {
        final ConfigurationFile config = UpdaterConfigTest.getMockedConfig(new UpdaterConfigTest.Input(null, true, false, "PATCH_DEV", false));
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        when(handler.searchUpdate(any(), any(), any())).thenReturn(Pair.of(new Version("2.0.0-DEV-3"), null));

        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(config, version, handler, null, plugin, scheduler, instantSource);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertFalse(updater.isUpdateAvailable(), "Expected no available update");
        }

        validator.assertEmpty();
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testUpdate(final LogValidator validator) {
        final ConfigurationFile config = UpdaterConfigTest.getMockedConfig(new UpdaterConfigTest.Input(null, true, true, "PATCH_DEV", false));
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final UpdateDownloader downloader = mock(UpdateDownloader.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        when(plugin.getPluginTag()).thenReturn("");
        final Version newVersion = new Version("2.0.0-DEV-5");
        when(handler.searchUpdate(any(), eq(version), any())).thenReturn(Pair.of(newVersion, "https://betonquest.org"));
        when(handler.searchUpdate(any(), eq(newVersion), any())).thenReturn(Pair.of(newVersion, null));

        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(config, version, handler, downloader, plugin, scheduler, instantSource);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertTrue(updater.isUpdateAvailable(), "Expected update available");
            assertEquals("2.0.0-DEV-5", updater.getUpdateVersion(), "Expected versions do not match");

            final Player player = mock(Player.class);
            when(player.getUniqueId()).thenReturn(UUID.randomUUID());
            updater.sendUpdateNotification(player);

            updater.update(player);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertFalse(updater.isUpdateAvailable(), "Expected no available update");
            assertNull(updater.getUpdateVersion(), "Expected no update version");

            final InOrder inOrder = inOrder(player);
            inOrder.verify(player, times(1)).sendMessage("§2Started update to version '2.0.0-DEV-5'...");
            inOrder.verify(player, times(1)).sendMessage("§2...download finished. Restart the server to update the plugin.");
        }

        validator.assertLogEntry(Level.INFO, "Found newer version '2.0.0-DEV-5', it will be installed, if you execute '/q update'!");
        validator.assertLogEntry(Level.INFO, "Started update to version '2.0.0-DEV-5'...");
        validator.assertLogEntry(Level.INFO, "...download finished. Restart the server to update the plugin.");
        validator.assertEmpty();
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testUpdateAutomatic(final LogValidator validator) {
        final ConfigurationFile config = UpdaterConfigTest.getMockedConfig(new UpdaterConfigTest.Input(null, true, true, "PATCH_DEV", true));
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final UpdateDownloader downloader = mock(UpdateDownloader.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        when(plugin.getPluginTag()).thenReturn("");
        final Version newVersion = new Version("2.0.0-DEV-5");
        when(handler.searchUpdate(any(), eq(version), any())).thenReturn(Pair.of(newVersion, "https://betonquest.org"));
        when(handler.searchUpdate(any(), eq(newVersion), any())).thenReturn(Pair.of(newVersion, null));

        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(config, version, handler, downloader, plugin, scheduler, instantSource);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertFalse(updater.isUpdateAvailable(), "Expected no available update");
            assertNull(updater.getUpdateVersion(), "Expected no update version");

            final Player player = mock(Player.class);
            when(player.getUniqueId()).thenReturn(UUID.randomUUID());
            updater.sendUpdateNotification(player);
            updater.sendUpdateNotification(player);

            final InOrder inOrder = inOrder(player);
            inOrder.verify(player, times(2)).getUniqueId();
            inOrder.verify(player, times(1)).sendMessage("§2Update was downloaded! Restart the server to update the plugin.");
            inOrder.verify(player, times(2)).getUniqueId();
        }

        validator.assertLogEntry(Level.INFO, "Found newer version '2.0.0-DEV-5', it will be downloaded and automatically installed on the next restart!");
        validator.assertLogEntry(Level.INFO, "Started update to version '2.0.0-DEV-5'...");
        validator.assertLogEntry(Level.INFO, "...download finished. Restart the server to update the plugin.");
        validator.assertEmpty();
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testUpdateAvailableSearchAgain(final LogValidator validator) {
        final ConfigurationFile config = UpdaterConfigTest.getMockedConfig(new UpdaterConfigTest.Input(null, true, false, "PATCH_DEV", false));
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        when(handler.searchUpdate(any(), any(), any())).thenReturn(Pair.of(new Version("2.0.0-DEV-5"), "https://betonquest.org"));

        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(config, version, handler, null, plugin, scheduler, instantSource);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertTrue(updater.isUpdateAvailable(), "Expected update available");
            assertEquals("2.0.0-DEV-5", updater.getUpdateVersion(), "Expected versions do not match");

            updater.search();
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertTrue(updater.isUpdateAvailable(), "Expected update available");
            assertEquals("2.0.0-DEV-5", updater.getUpdateVersion(), "Expected versions do not match");

            verify(handler, times(1)).searchUpdate(any(), any(), any());
        }

        validator.assertLogEntry(Level.INFO, "Found newer version '2.0.0-DEV-5', it will be installed, if you execute '/q update'!");
        validator.assertEmpty();
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testUpdateWithInvalidUrl(final LogValidator validator) {
        final ConfigurationFile config = UpdaterConfigTest.getMockedConfig(new UpdaterConfigTest.Input(null, true, false, "PATCH_DEV", true));
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        final Version newVersion = new Version("2.0.0-DEV-5");
        when(handler.searchUpdate(any(), eq(version), any())).thenReturn(Pair.of(newVersion, "betonquest"));
        when(handler.searchUpdate(any(), eq(newVersion), any())).thenReturn(Pair.of(newVersion, null));

        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(config, version, handler, null, plugin, scheduler, instantSource);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertTrue(updater.isUpdateAvailable(), "Expected update available");
            assertEquals("2.0.0-DEV-5", updater.getUpdateVersion(), "Expected versions do not match");
        }

        validator.assertLogEntry(Level.INFO, "Found newer version '2.0.0-DEV-5', it will be downloaded and automatically installed on the next restart!");
        validator.assertLogEntry(Level.INFO, "Started update to version '2.0.0-DEV-5'...");
        validator.assertLogEntry(Level.INFO, "There was an error resolving the url 'betonquest'! Reason: no protocol: betonquest");
        validator.assertLogEntry(Level.FINE, "Error while performing update!", QuestRuntimeException.class);
        validator.assertEmpty();
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testUpdateSearchAgain(final LogValidator validator) {
        final ConfigurationFile config = UpdaterConfigTest.getMockedConfig(new UpdaterConfigTest.Input(null, true, true, "PATCH_DEV", false));
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final UpdateDownloader downloader = mock(UpdateDownloader.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        when(plugin.getPluginTag()).thenReturn("");
        final Version newVersion = new Version("2.0.0-DEV-5");
        when(handler.searchUpdate(any(), eq(version), any())).thenReturn(Pair.of(newVersion, "https://betonquest.org/5"));
        when(handler.searchUpdate(any(), eq(newVersion), any())).thenReturn(Pair.of(new Version("2.0.0-DEV-6"), "https://betonquest.org/6"));

        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(config, version, handler, downloader, plugin, scheduler, instantSource);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertTrue(updater.isUpdateAvailable(), "Expected update available");
            assertEquals("2.0.0-DEV-5", updater.getUpdateVersion(), "Expected versions do not match");

            updater.update(null);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertTrue(updater.isUpdateAvailable(), "Expected update available");
            assertEquals("2.0.0-DEV-6", updater.getUpdateVersion(), "Expected versions do not match");
        }

        validator.assertLogEntry(Level.INFO, "Found newer version '2.0.0-DEV-5', it will be installed, if you execute '/q update'!");
        validator.assertLogEntry(Level.INFO, "Update aborted! A newer version was found. New version '2.0.0-DEV-6'! You can execute '/q update' again to update.");
        validator.assertLogEntry(Level.FINE, "Error while performing update!");
        validator.assertEmpty();
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testUpdateWithoutAvailable(final LogValidator validator) {
        final ConfigurationFile config = UpdaterConfigTest.getMockedConfig(new UpdaterConfigTest.Input(null, true, false, "PATCH_DEV", false));
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final UpdateDownloader downloader = mock(UpdateDownloader.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        when(handler.searchUpdate(any(), any(), any())).thenReturn(Pair.of(new Version("2.0.0-DEV-3"), null));

        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(config, version, handler, downloader, plugin, scheduler, instantSource);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertFalse(updater.isUpdateAvailable(), "Expected no available update");

            updater.update(null);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertFalse(updater.isUpdateAvailable(), "Expected no available update");
            assertNull(updater.getUpdateVersion(), "Expected no update version");
        }

        validator.assertLogEntry(Level.INFO, "The updater did not find an update! This can depend on your update.strategy, check config entry 'update.strategy'.");
        validator.assertLogEntry(Level.FINE, "Error while performing update!");
        validator.assertEmpty();
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testUpdateAlreadyDownloaded(final LogValidator validator) {
        final ConfigurationFile config = UpdaterConfigTest.getMockedConfig(new UpdaterConfigTest.Input(null, true, false, "PATCH_DEV", false));
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final UpdateDownloader downloader = mock(UpdateDownloader.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        when(handler.searchUpdate(any(), any(), any())).thenReturn(Pair.of(new Version("2.0.0-DEV-3"), null));
        when(downloader.alreadyDownloaded()).thenReturn(true);

        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(config, version, handler, downloader, plugin, scheduler, instantSource);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertFalse(updater.isUpdateAvailable(), "Expected no available update");

            updater.update(null);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertFalse(updater.isUpdateAvailable(), "Expected no available update");
            assertNull(updater.getUpdateVersion(), "Expected no update version");
        }

        validator.assertLogEntry(Level.INFO, "The update was already downloaded! Restart the server to update the plugin.");
        validator.assertLogEntry(Level.FINE, "Error while performing update!");
        validator.assertEmpty();
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testUpdateWithDisabled(final LogValidator validator) {
        final ConfigurationFile config = UpdaterConfigTest.getMockedConfig(new UpdaterConfigTest.Input(null, false, false, "PATCH_DEV", false));
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        when(handler.searchUpdate(any(), any(), any())).thenReturn(Pair.of(new Version("2.0.0-DEV-3"), null));

        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(config, version, handler, null, plugin, scheduler, instantSource);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertFalse(updater.isUpdateAvailable(), "Expected no update available");

            updater.update(null);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertFalse(updater.isUpdateAvailable(), "Expected no available update");
            assertNull(updater.getUpdateVersion(), "Expected no update version");
        }

        validator.assertLogEntry(Level.INFO, "The updater is disabled! Change config entry 'update.enabled' to 'true' to enable it.");
        validator.assertLogEntry(Level.FINE, "Error while performing update!");
        validator.assertEmpty();
    }
}
