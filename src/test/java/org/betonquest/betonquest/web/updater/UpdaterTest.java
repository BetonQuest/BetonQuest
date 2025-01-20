package org.betonquest.betonquest.web.updater;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.util.scheduler.BukkitSchedulerMock;
import org.betonquest.betonquest.versioning.Version;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.InstantSource;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class tests the {@link Updater}.
 */
@ExtendWith(MockitoExtension.class)
class UpdaterTest {
    @Mock
    private BetonQuestLogger logger;

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testUpdateAvailable() {
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        when(handler.searchUpdate(any(), any(), any())).thenReturn(Pair.of(new Version("2.0.0-DEV-5"), "https://betonquest.org"));

        final UpdaterConfigTest.Input patchDev = new UpdaterConfigTest.Input(null, true, false, "PATCH_DEV", false);
        final UpdaterConfig updaterConfig = UpdaterConfigTest.getMockedConfig(logger, patchDev, version);
        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(logger, updaterConfig, version, handler, null, plugin, scheduler, instantSource);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertTrue(updater.isUpdateAvailable(), "Expected update available");
            assertEquals("2.0.0-DEV-5", updater.getUpdateVersion(), "Expected versions do not match");
        }

        verify(logger, times(1)).info("Found newer version '2.0.0-DEV-5', it will be installed, if you execute '/q update'!");
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testNoUpdateAvailable() {
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        when(handler.searchUpdate(any(), any(), any())).thenReturn(Pair.of(new Version("2.0.0-DEV-3"), null));

        final UpdaterConfigTest.Input patchDev = new UpdaterConfigTest.Input(null, true, false, "PATCH_DEV", false);
        final UpdaterConfig updaterConfig = UpdaterConfigTest.getMockedConfig(logger, patchDev, version);
        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(logger, updaterConfig, version, handler, null, plugin, scheduler, instantSource);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertFalse(updater.isUpdateAvailable(), "Expected no available update");
        }

        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testUpdate() {
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final UpdateDownloader downloader = mock(UpdateDownloader.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        when(plugin.getPluginTag()).thenReturn("");
        final Version newVersion = new Version("2.0.0-DEV-5");
        when(handler.searchUpdate(any(), eq(version), any())).thenReturn(Pair.of(newVersion, "https://betonquest.org"));
        when(handler.searchUpdate(any(), eq(newVersion), any())).thenReturn(Pair.of(newVersion, null));

        final UpdaterConfigTest.Input patchDev = new UpdaterConfigTest.Input(null, true, true, "PATCH_DEV", false);
        final UpdaterConfig updaterConfig = UpdaterConfigTest.getMockedConfig(logger, patchDev, version);
        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(logger, updaterConfig, version, handler, downloader, plugin, scheduler, instantSource);
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

        verify(logger, times(1)).info("Found newer version '2.0.0-DEV-5', it will be installed, if you execute '/q update'!");
        verify(logger, times(1)).info("Started update to version '2.0.0-DEV-5'...");
        verify(logger, times(1)).info("...download finished. Restart the server to update the plugin.");
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testUpdateAutomatic() {
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final UpdateDownloader downloader = mock(UpdateDownloader.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        when(plugin.getPluginTag()).thenReturn("");
        final Version newVersion = new Version("2.0.0-DEV-5");
        when(handler.searchUpdate(any(), eq(version), any())).thenReturn(Pair.of(newVersion, "https://betonquest.org"));
        when(handler.searchUpdate(any(), eq(newVersion), any())).thenReturn(Pair.of(newVersion, null));

        final UpdaterConfigTest.Input patchDev = new UpdaterConfigTest.Input(null, true, true, "PATCH_DEV", true);
        final UpdaterConfig updaterConfig = UpdaterConfigTest.getMockedConfig(logger, patchDev, version);
        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(logger, updaterConfig, version, handler, downloader, plugin, scheduler, instantSource);
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

        verify(logger, times(1)).info("Found newer version '2.0.0-DEV-5', it will be downloaded and automatically installed on the next restart!");
        verify(logger, times(1)).info("Started update to version '2.0.0-DEV-5'...");
        verify(logger, times(1)).info("...download finished. Restart the server to update the plugin.");
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testUpdateAvailableSearchAgain() {
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        when(handler.searchUpdate(any(), any(), any())).thenReturn(Pair.of(new Version("2.0.0-DEV-5"), "https://betonquest.org"));

        final UpdaterConfigTest.Input patchDev = new UpdaterConfigTest.Input(null, true, false, "PATCH_DEV", false);
        final UpdaterConfig updaterConfig = UpdaterConfigTest.getMockedConfig(logger, patchDev, version);
        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(logger, updaterConfig, version, handler, null, plugin, scheduler, instantSource);
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

        verify(logger, times(1)).info("Found newer version '2.0.0-DEV-5', it will be installed, if you execute '/q update'!");
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testUpdateWithInvalidUrl() {
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        final Version newVersion = new Version("2.0.0-DEV-5");
        when(handler.searchUpdate(any(), eq(version), any())).thenReturn(Pair.of(newVersion, "betonquest"));
        when(handler.searchUpdate(any(), eq(newVersion), any())).thenReturn(Pair.of(newVersion, null));

        final UpdaterConfigTest.Input patchDev = new UpdaterConfigTest.Input(null, true, false, "PATCH_DEV", true);
        final UpdaterConfig updaterConfig = UpdaterConfigTest.getMockedConfig(logger, patchDev, version);
        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(logger, updaterConfig, version, handler, null, plugin, scheduler, instantSource);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertTrue(updater.isUpdateAvailable(), "Expected update available");
            assertEquals("2.0.0-DEV-5", updater.getUpdateVersion(), "Expected versions do not match");
        }

        verify(logger, times(1)).info("Found newer version '2.0.0-DEV-5', it will be downloaded and automatically installed on the next restart!");
        verify(logger, times(1)).info("Started update to version '2.0.0-DEV-5'...");
        verify(logger, times(1)).info("There was an error resolving the url 'betonquest'! Reason: no protocol: betonquest");
        verify(logger, times(1)).debug(eq("Error while performing update!"), any(QuestException.class));
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testUpdateSearchAgain() {
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final UpdateDownloader downloader = mock(UpdateDownloader.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        final Version newVersion = new Version("2.0.0-DEV-5");
        when(handler.searchUpdate(any(), eq(version), any())).thenReturn(Pair.of(newVersion, "https://betonquest.org/5"));
        when(handler.searchUpdate(any(), eq(newVersion), any())).thenReturn(Pair.of(new Version("2.0.0-DEV-6"), "https://betonquest.org/6"));

        final UpdaterConfigTest.Input patchDev = new UpdaterConfigTest.Input(null, true, true, "PATCH_DEV", false);
        final UpdaterConfig updaterConfig = UpdaterConfigTest.getMockedConfig(logger, patchDev, version);
        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(logger, updaterConfig, version, handler, downloader, plugin, scheduler, instantSource);
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

        verify(logger, times(1)).info("Found newer version '2.0.0-DEV-5', it will be installed, if you execute '/q update'!");
        verify(logger, times(1)).info("Update aborted! A newer version was found. New version '2.0.0-DEV-6'! You can execute '/q update' again to update.");
        verify(logger, times(1)).debug(eq("Error while performing update!"), any(QuestException.class));
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testUpdateWithoutAvailable() {
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final UpdateDownloader downloader = mock(UpdateDownloader.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        when(handler.searchUpdate(any(), any(), any())).thenReturn(Pair.of(new Version("2.0.0-DEV-3"), null));

        final UpdaterConfigTest.Input patchDev = new UpdaterConfigTest.Input(null, true, false, "PATCH_DEV", false);
        final UpdaterConfig updaterConfig = UpdaterConfigTest.getMockedConfig(logger, patchDev, version);
        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(logger, updaterConfig, version, handler, downloader, plugin, scheduler, instantSource);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertFalse(updater.isUpdateAvailable(), "Expected no available update");

            updater.update(null);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertFalse(updater.isUpdateAvailable(), "Expected no available update");
            assertNull(updater.getUpdateVersion(), "Expected no update version");
        }

        verify(logger, times(1)).info("The updater did not find an update! This can depend on your update.strategy, check config entry 'update.strategy'.");
        verify(logger, times(1)).debug(eq("Error while performing update!"), any(QuestException.class));
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testUpdateAlreadyDownloaded() {
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final UpdateDownloader downloader = mock(UpdateDownloader.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        when(handler.searchUpdate(any(), any(), any())).thenReturn(Pair.of(new Version("2.0.0-DEV-3"), null));
        when(downloader.alreadyDownloaded()).thenReturn(true);

        final UpdaterConfigTest.Input patchDev = new UpdaterConfigTest.Input(null, true, false, "PATCH_DEV", false);
        final UpdaterConfig updaterConfig = UpdaterConfigTest.getMockedConfig(logger, patchDev, version);
        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(logger, updaterConfig, version, handler, downloader, plugin, scheduler, instantSource);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertFalse(updater.isUpdateAvailable(), "Expected no available update");

            updater.update(null);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertFalse(updater.isUpdateAvailable(), "Expected no available update");
            assertNull(updater.getUpdateVersion(), "Expected no update version");
        }

        verify(logger, times(1)).info("The update was already downloaded! Restart the server to update the plugin.");
        verify(logger, times(1)).debug(eq("Error while performing update!"), any(QuestException.class));
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testUpdateWithDisabled() {
        final Version version = new Version("2.0.0-DEV-3");
        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        final UpdaterConfigTest.Input patchDev = new UpdaterConfigTest.Input(null, false, false, "PATCH_DEV", false);
        final UpdaterConfig updaterConfig = UpdaterConfigTest.getMockedConfig(logger, patchDev, version);
        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(logger, updaterConfig, version, handler, null, plugin, scheduler, instantSource);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertFalse(updater.isUpdateAvailable(), "Expected no update available");

            updater.update(null);
            assertTrue(scheduler.waitAsyncTasksFinished(), "Expected async tasks to finish");
            scheduler.assertNoExceptions();
            assertFalse(updater.isUpdateAvailable(), "Expected no available update");
            assertNull(updater.getUpdateVersion(), "Expected no update version");
        }

        verify(logger, times(1)).info("The updater is disabled! Change config entry 'update.enabled' to 'true' to enable it.");
        verify(logger, times(1)).debug(eq("Error while performing update!"), any(QuestException.class));
        verifyNoMoreInteractions(logger);
    }
}
