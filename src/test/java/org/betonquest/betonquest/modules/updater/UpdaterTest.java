package org.betonquest.betonquest.modules.updater;

import org.apache.commons.io.FileUtils;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.betonquest.betonquest.modules.updater.source.UpdateSourceDevelopmentHandler;
import org.betonquest.betonquest.modules.updater.source.UpdateSourceReleaseHandler;
import org.betonquest.betonquest.modules.versioning.Version;
import org.betonquest.betonquest.util.scheduler.BukkitSchedulerMock;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.time.InstantSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(BetonQuestLoggerService.class)
class UpdaterTest {
    private static final String UPDATE_FILE = "src/test/resources/updater/BetonQuest.jar";

    @Test
    void testDevelopmentUpdateAvailable(@TempDir final File tempDir, final LogValidator validator) throws IOException {
        final ConfigurationFile config = UpdaterConfigTest.getMockedConfig(new UpdaterConfigTest.Input(null, true, true, "PATCH_DEV", false));
        final String file = "BetonQuest.jar";
        final Version version = new Version("2.0.0-DEV-3");
        final List<UpdateSourceReleaseHandler> releaseHandlerList = new ArrayList<>();
        final List<UpdateSourceDevelopmentHandler> developmentHandlerList = new ArrayList<>();
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        final UpdateSourceDevelopmentHandler developmentHandler = mock(UpdateSourceDevelopmentHandler.class);
        final Map<Version, String> versions = new HashMap<>();
        versions.put(new Version("2.0.0-DEV-201"), "https://betonquest.org");
        when(developmentHandler.getDevelopmentVersions()).thenReturn(versions);
        developmentHandlerList.add(developmentHandler);

        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(config, file, tempDir, version, releaseHandlerList, developmentHandlerList, plugin, scheduler, instantSource);
            scheduler.waitAsyncTasksFinished();
            scheduler.assertNoExceptions();
            assertTrue(updater.isUpdateAvailable());
            assertEquals("2.0.0-DEV-201", updater.getUpdateVersion());
        }
        validator.assertLogEntry(Level.INFO, "Found newer version '2.0.0-DEV-201', it will be installed, if you execute '/q update'!");
        validator.assertEmpty();
    }

    @Test
    void testNoDevelopmentUpdateAvailable(@TempDir final File tempDir, final LogValidator validator) throws IOException {
        final ConfigurationFile config = UpdaterConfigTest.getMockedConfig(new UpdaterConfigTest.Input(null, true, true, "PATCH_DEV", false));
        final String file = "BetonQuest.jar";
        final Version version = new Version("2.0.0-DEV-3");
        final List<UpdateSourceReleaseHandler> releaseHandlerList = new ArrayList<>();
        final List<UpdateSourceDevelopmentHandler> developmentHandlerList = new ArrayList<>();
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        final UpdateSourceDevelopmentHandler developmentHandler = mock(UpdateSourceDevelopmentHandler.class);
        final Map<Version, String> versions = new HashMap<>();
        versions.put(new Version("2.0.0-DEV-3"), "https://betonquest.org");
        when(developmentHandler.getDevelopmentVersions()).thenReturn(versions);
        developmentHandlerList.add(developmentHandler);

        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(config, file, tempDir, version, releaseHandlerList, developmentHandlerList, plugin, scheduler, instantSource);
            scheduler.waitAsyncTasksFinished();
            scheduler.assertNoExceptions();
            assertFalse(updater.isUpdateAvailable());
            assertNull(updater.getUpdateVersion());
        }
        validator.assertEmpty();
    }

    @Test
    void testReleaseUpdateAvailable(@TempDir final File tempDir, final LogValidator validator) throws IOException {
        final ConfigurationFile config = UpdaterConfigTest.getMockedConfig(new UpdaterConfigTest.Input(null, true, true, "PATCH_DEV", false));
        final String file = "BetonQuest.jar";
        final Version version = new Version("2.0.0-DEV-3");
        final List<UpdateSourceReleaseHandler> releaseHandlerList = new ArrayList<>();
        final List<UpdateSourceDevelopmentHandler> developmentHandlerList = new ArrayList<>();
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        final UpdateSourceReleaseHandler releaseHandler = mock(UpdateSourceReleaseHandler.class);
        final Map<Version, String> versions = new HashMap<>();
        versions.put(new Version("2.0.0"), "https://betonquest.org");
        when(releaseHandler.getReleaseVersions()).thenReturn(versions);
        releaseHandlerList.add(releaseHandler);

        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(config, file, tempDir, version, releaseHandlerList, developmentHandlerList, plugin, scheduler, instantSource);
            scheduler.waitAsyncTasksFinished();
            scheduler.assertNoExceptions();
            assertTrue(updater.isUpdateAvailable());
            assertEquals("2.0.0", updater.getUpdateVersion());
        }
        validator.assertLogEntry(Level.INFO, "Found newer version '2.0.0', it will be installed, if you execute '/q update'!");
        validator.assertEmpty();
    }

    @Test
    void testNoReleaseUpdateAvailable(@TempDir final File tempDir, final LogValidator validator) throws IOException {
        final ConfigurationFile config = UpdaterConfigTest.getMockedConfig(new UpdaterConfigTest.Input(null, true, true, "PATCH_DEV", false));
        final String file = "BetonQuest.jar";
        final Version version = new Version("2.0.0");
        final List<UpdateSourceReleaseHandler> releaseHandlerList = new ArrayList<>();
        final List<UpdateSourceDevelopmentHandler> developmentHandlerList = new ArrayList<>();
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        final UpdateSourceReleaseHandler releaseHandler = mock(UpdateSourceReleaseHandler.class);
        final Map<Version, String> versions = new HashMap<>();
        versions.put(new Version("2.0.0"), "https://betonquest.org");
        when(releaseHandler.getReleaseVersions()).thenReturn(versions);
        releaseHandlerList.add(releaseHandler);

        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(config, file, tempDir, version, releaseHandlerList, developmentHandlerList, plugin, scheduler, instantSource);
            scheduler.waitAsyncTasksFinished();
            scheduler.assertNoExceptions();
            assertFalse(updater.isUpdateAvailable());
            assertNull(updater.getUpdateVersion());
        }
        validator.assertEmpty();
    }

    @Test
    void testReleaseAndDevelopmentUpdateAvailable1(@TempDir final File tempDir, final LogValidator validator) throws IOException {
        final ConfigurationFile config = UpdaterConfigTest.getMockedConfig(new UpdaterConfigTest.Input(null, true, true, "PATCH", false));
        final String file = "BetonQuest.jar";
        final Version version = new Version("2.0.0-DEV-3");
        final List<UpdateSourceReleaseHandler> releaseHandlerList = new ArrayList<>();
        final List<UpdateSourceDevelopmentHandler> developmentHandlerList = new ArrayList<>();
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        final UpdateSourceReleaseHandler releaseHandler = mock(UpdateSourceReleaseHandler.class);
        final UpdateSourceDevelopmentHandler developmentHandler = mock(UpdateSourceDevelopmentHandler.class);
        final Map<Version, String> releaseVersions = new HashMap<>();
        final Map<Version, String> developmentVersions = new HashMap<>();
        releaseVersions.put(new Version("2.0.0"), "https://betonquest.org");
        developmentVersions.put(new Version("2.0.1-DEV-23"), "https://betonquest.org");
        when(releaseHandler.getReleaseVersions()).thenReturn(releaseVersions);
        when(developmentHandler.getDevelopmentVersions()).thenReturn(developmentVersions);

        releaseHandlerList.add(releaseHandler);
        developmentHandlerList.add(developmentHandler);

        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(config, file, tempDir, version, releaseHandlerList, developmentHandlerList, plugin, scheduler, instantSource);
            scheduler.waitAsyncTasksFinished();
            scheduler.assertNoExceptions();
            assertTrue(updater.isUpdateAvailable());
            assertEquals("2.0.0", updater.getUpdateVersion());
        }
        validator.assertLogEntry(Level.INFO, "Found newer version '2.0.0', it will be installed, if you execute '/q update'!");
        validator.assertEmpty();
    }

    @Test
    void testReleaseAndDevelopmentUpdateAvailable2(@TempDir final File tempDir, final LogValidator validator) throws IOException {
        final ConfigurationFile config = UpdaterConfigTest.getMockedConfig(new UpdaterConfigTest.Input(null, true, true, "PATCH_DEV", false));
        final String file = "BetonQuest.jar";
        final Version version = new Version("2.0.0-DEV-3");
        final List<UpdateSourceReleaseHandler> releaseHandlerList = new ArrayList<>();
        final List<UpdateSourceDevelopmentHandler> developmentHandlerList = new ArrayList<>();
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        final UpdateSourceReleaseHandler releaseHandler = mock(UpdateSourceReleaseHandler.class);
        final UpdateSourceDevelopmentHandler developmentHandler = mock(UpdateSourceDevelopmentHandler.class);
        final Map<Version, String> releaseVersions = new HashMap<>();
        final Map<Version, String> developmentVersions = new HashMap<>();
        releaseVersions.put(new Version("2.0.0"), "https://betonquest.org");
        developmentVersions.put(new Version("2.0.1-DEV-23"), "https://betonquest.org");
        when(releaseHandler.getReleaseVersions()).thenReturn(releaseVersions);
        when(developmentHandler.getDevelopmentVersions()).thenReturn(developmentVersions);

        releaseHandlerList.add(releaseHandler);
        developmentHandlerList.add(developmentHandler);

        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(config, file, tempDir, version, releaseHandlerList, developmentHandlerList, plugin, scheduler, instantSource);
            scheduler.waitAsyncTasksFinished();
            scheduler.assertNoExceptions();
            assertTrue(updater.isUpdateAvailable());
            assertEquals("2.0.1-DEV-23", updater.getUpdateVersion());
        }
        validator.assertLogEntry(Level.INFO, "Found newer version '2.0.1-DEV-23', it will be installed, if you execute '/q update'!");
        validator.assertEmpty();
    }

    @Test
    void testReleaseAndDevelopmentNoUpdateAvailable(@TempDir final File tempDir, final LogValidator validator) throws IOException {
        final ConfigurationFile config = UpdaterConfigTest.getMockedConfig(new UpdaterConfigTest.Input(null, true, true, "PATCH_DEV", false));
        final String file = "BetonQuest.jar";
        final Version version = new Version("2.0.0-DEV-3");
        final List<UpdateSourceReleaseHandler> releaseHandlerList = new ArrayList<>();
        final List<UpdateSourceDevelopmentHandler> developmentHandlerList = new ArrayList<>();
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        final UpdateSourceReleaseHandler releaseHandler = mock(UpdateSourceReleaseHandler.class);
        final UpdateSourceDevelopmentHandler developmentHandler = mock(UpdateSourceDevelopmentHandler.class);
        final Map<Version, String> releaseVersions = new HashMap<>();
        final Map<Version, String> developmentVersions = new HashMap<>();

        releaseHandlerList.add(releaseHandler);
        developmentHandlerList.add(developmentHandler);

        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(config, file, tempDir, version, releaseHandlerList, developmentHandlerList, plugin, scheduler, instantSource);
            scheduler.waitAsyncTasksFinished();
            scheduler.assertNoExceptions();
            assertFalse(updater.isUpdateAvailable());
            assertNull(updater.getUpdateVersion());
        }
        validator.assertEmpty();
    }

    @Disabled
    @Test
    void testUpdate(@TempDir final File tempDir, final LogValidator validator) throws IOException {
        final ConfigurationFile config = UpdaterConfigTest.getMockedConfig(new UpdaterConfigTest.Input(null, true, true, "PATCH", true));
        final String file = "BetonQuest.jar";
        final Version version = new Version("2.0.0-DEV-3");
        final List<UpdateSourceReleaseHandler> releaseHandlerList = new ArrayList<>();
        final List<UpdateSourceDevelopmentHandler> developmentHandlerList = new ArrayList<>();
        final BetonQuest plugin = mock(BetonQuest.class);
        final InstantSource instantSource = InstantSource.system();

        final UpdateSourceReleaseHandler releaseHandler = mock(UpdateSourceReleaseHandler.class);
        final UpdateSourceDevelopmentHandler developmentHandler = mock(UpdateSourceDevelopmentHandler.class);
        final Map<Version, String> releaseVersions = new HashMap<>();
        releaseVersions.put(new Version("2.0.0"), "https://betonquest.org");
        when(releaseHandler.getReleaseVersions()).thenReturn(releaseVersions);
        releaseHandlerList.add(releaseHandler);

        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final Updater updater = new Updater(config, file, tempDir, version, releaseHandlerList, developmentHandlerList, plugin, scheduler, instantSource);
            scheduler.waitAsyncTasksFinished();
            scheduler.assertNoExceptions();
            assertTrue(updater.isUpdateAvailable());
            assertEquals("2.0.0", updater.getUpdateVersion());
        }

        final File updateFile = new File(tempDir, file);
        assertTrue(updateFile.exists());
        assertTrue(FileUtils.contentEquals(updateFile, new File(UPDATE_FILE)),
                "The received file is not equal to the expected one!");

        validator.assertLogEntry(Level.INFO, "Found newer version '2.0.0', it will be installed, if you execute '/q update'!");
        validator.assertEmpty();
    }
}
