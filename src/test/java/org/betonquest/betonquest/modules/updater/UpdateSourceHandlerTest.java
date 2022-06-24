package org.betonquest.betonquest.modules.updater;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.betonquest.betonquest.modules.updater.source.UpdateSourceDevelopment;
import org.betonquest.betonquest.modules.updater.source.UpdateSourceRelease;
import org.betonquest.betonquest.modules.versioning.UpdateStrategy;
import org.betonquest.betonquest.modules.versioning.Version;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(BetonQuestLoggerService.class)
class UpdateSourceHandlerTest {

    @Test
    void testDevelopmentUpdateAvailable(@TempDir final File tempDir, final LogValidator validator) throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.PATCH, true, false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<UpdateSourceRelease> releaseHandlerList = new ArrayList<>();
        final List<UpdateSourceDevelopment> developmentHandlerList = getUpdateSourceDevelopment("2.0.0-DEV-201", "https://betonquest.org");

        final UpdateSourceHandler handler = new UpdateSourceHandler(releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0-DEV-201", latest.getKey().getVersion());
        assertNotNull(latest.getValue());
        assertEquals("https://betonquest.org", latest.getValue());

        validator.assertEmpty();
    }

    @Test
    void testNoDevelopmentUpdateAvailable(@TempDir final File tempDir, final LogValidator validator) throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.PATCH, true, false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<UpdateSourceRelease> releaseHandlerList = new ArrayList<>();
        final List<UpdateSourceDevelopment> developmentHandlerList = getUpdateSourceDevelopment("2.0.0-DEV-3", "https://betonquest.org");

        final UpdateSourceHandler handler = new UpdateSourceHandler(releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0-DEV-3", latest.getKey().getVersion());
        assertNull(latest.getValue());

        validator.assertEmpty();
    }

    @Test
    void testReleaseUpdateAvailable(@TempDir final File tempDir, final LogValidator validator) throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.PATCH, true, false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<UpdateSourceRelease> releaseHandlerList = getUpdateSourceRelease("2.0.0", "https://betonquest.org");
        final List<UpdateSourceDevelopment> developmentHandlerList = new ArrayList<>();

        final UpdateSourceHandler handler = new UpdateSourceHandler(releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0", latest.getKey().getVersion());
        assertNotNull(latest.getValue());
        assertEquals("https://betonquest.org", latest.getValue());

        validator.assertEmpty();
    }

    @Test
    void testNoReleaseUpdateAvailable(@TempDir final File tempDir, final LogValidator validator) throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.PATCH, true, false);
        final Version version = new Version("2.0.0");
        final List<UpdateSourceRelease> releaseHandlerList = getUpdateSourceRelease("2.0.0", "https://betonquest.org");
        final List<UpdateSourceDevelopment> developmentHandlerList = new ArrayList<>();

        final UpdateSourceHandler handler = new UpdateSourceHandler(releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0", latest.getKey().getVersion());
        assertNull(latest.getValue());

        validator.assertEmpty();
    }

    @Test
    void testReleaseAndDevelopmentUpdateAvailableForced(@TempDir final File tempDir, final LogValidator validator) throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.PATCH, true, true);
        final Version version = new Version("2.0.0-DEV-3");
        final List<UpdateSourceRelease> releaseHandlerList = getUpdateSourceRelease("2.0.0", "https://betonquest.org/release");
        final List<UpdateSourceDevelopment> developmentHandlerList = getUpdateSourceDevelopment("2.0.1-DEV-201", "https://betonquest.org/development");

        final UpdateSourceHandler handler = new UpdateSourceHandler(releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0", latest.getKey().getVersion());
        assertNotNull(latest.getValue());
        assertEquals("https://betonquest.org/release", latest.getValue());

        validator.assertEmpty();
    }

    @Test
    void testReleaseAndDevelopmentUpdateAvailableNotForced(@TempDir final File tempDir, final LogValidator validator) throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.PATCH, true, false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<UpdateSourceRelease> releaseHandlerList = getUpdateSourceRelease("2.0.0", "https://betonquest.org/release");
        final List<UpdateSourceDevelopment> developmentHandlerList = getUpdateSourceDevelopment("2.0.1-DEV-201", "https://betonquest.org/development");

        final UpdateSourceHandler handler = new UpdateSourceHandler(releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.1-DEV-201", latest.getKey().getVersion());
        assertNotNull(latest.getValue());
        assertEquals("https://betonquest.org/development", latest.getValue());

        validator.assertEmpty();
    }

    @Test
    void testReleaseAndDevelopmentNoUpdateAvailable(@TempDir final File tempDir, final LogValidator validator) throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.PATCH, true, false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<UpdateSourceRelease> releaseHandlerList = new ArrayList<>();
        final List<UpdateSourceDevelopment> developmentHandlerList = new ArrayList<>();

        final UpdateSourceHandler handler = new UpdateSourceHandler(releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0-DEV-3", latest.getKey().getVersion());
        assertNull(latest.getValue());

        validator.assertEmpty();
    }

    @Test
    void testReleaseThrowsUnknownHostException(@TempDir final File tempDir, final LogValidator validator) throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.PATCH, true, false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<UpdateSourceRelease> releaseHandlerList = new ArrayList<>();
        final List<UpdateSourceDevelopment> developmentHandlerList = new ArrayList<>();

        final UpdateSourceRelease releaseHandler = mock(UpdateSourceRelease.class);
        when(releaseHandler.getReleaseVersions()).thenThrow(new UnknownHostException("Unknown host Test"));
        releaseHandlerList.add(releaseHandler);

        final UpdateSourceHandler handler = new UpdateSourceHandler(releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0-DEV-3", latest.getKey().getVersion());
        assertNull(latest.getValue());

        validator.assertLogEntry(Level.WARNING, "The update server for release builds is currently not available!");
        validator.assertEmpty();
    }

    @Test
    void testReleaseThrowsIOException(@TempDir final File tempDir, final LogValidator validator) throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.PATCH, true, false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<UpdateSourceRelease> releaseHandlerList = new ArrayList<>();
        final List<UpdateSourceDevelopment> developmentHandlerList = new ArrayList<>();

        final UpdateSourceRelease releaseHandler = mock(UpdateSourceRelease.class);
        when(releaseHandler.getReleaseVersions()).thenThrow(new IOException("Unexpected problem"));
        releaseHandlerList.add(releaseHandler);

        final UpdateSourceHandler handler = new UpdateSourceHandler(releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0-DEV-3", latest.getKey().getVersion());
        assertNull(latest.getValue());

        validator.assertLogEntry(Level.WARNING, "Could not get the latest release build! Unexpected problem");
        validator.assertLogEntry(Level.FINE, "Additional stacktrace:", IOException.class, "Unexpected problem");
        validator.assertEmpty();
    }

    @Test
    void testDevelopmentThrowsUnknownHostException(@TempDir final File tempDir, final LogValidator validator) throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.PATCH, true, false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<UpdateSourceRelease> releaseHandlerList = new ArrayList<>();
        final List<UpdateSourceDevelopment> developmentHandlerList = new ArrayList<>();

        final UpdateSourceDevelopment developmentHandler = mock(UpdateSourceDevelopment.class);
        when(developmentHandler.getDevelopmentVersions()).thenThrow(new UnknownHostException("Unknown host Test"));
        developmentHandlerList.add(developmentHandler);

        final UpdateSourceHandler handler = new UpdateSourceHandler(releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0-DEV-3", latest.getKey().getVersion());
        assertNull(latest.getValue());

        validator.assertLogEntry(Level.WARNING, "The update server for dev builds is currently not available!");
        validator.assertEmpty();
    }

    @Test
    void testDevelopmentThrowsIOException(@TempDir final File tempDir, final LogValidator validator) throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.PATCH, true, false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<UpdateSourceRelease> releaseHandlerList = new ArrayList<>();
        final List<UpdateSourceDevelopment> developmentHandlerList = new ArrayList<>();

        final UpdateSourceDevelopment developmentHandler = mock(UpdateSourceDevelopment.class);
        when(developmentHandler.getDevelopmentVersions()).thenThrow(new IOException("Unexpected problem"));
        developmentHandlerList.add(developmentHandler);

        final UpdateSourceHandler handler = new UpdateSourceHandler(releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0-DEV-3", latest.getKey().getVersion());
        assertNull(latest.getValue());

        validator.assertLogEntry(Level.WARNING, "Could not get the latest dev build! Unexpected problem");
        validator.assertLogEntry(Level.FINE, "Additional stacktrace:", IOException.class, "Unexpected problem");
        validator.assertEmpty();
    }

    @NotNull
    private UpdaterConfig getUpdaterConfig(final UpdateStrategy strategy, final boolean devDownloadEnabled, final boolean forcedStrategy) {
        final UpdaterConfig config = mock(UpdaterConfig.class);
        when(config.getStrategy()).thenReturn(strategy);
        when(config.isDevDownloadEnabled()).thenReturn(devDownloadEnabled);
        when(config.isForcedStrategy()).thenReturn(forcedStrategy);
        return config;
    }

    @NotNull
    private List<UpdateSourceRelease> getUpdateSourceRelease(final String version, final String url) throws IOException {
        final List<UpdateSourceRelease> handlerList = new ArrayList<>();

        final UpdateSourceRelease handler = mock(UpdateSourceRelease.class);
        final Map<Version, String> versions = new HashMap<>();
        versions.put(new Version(version), url);
        when(handler.getReleaseVersions()).thenReturn(versions);
        handlerList.add(handler);

        return handlerList;
    }

    @NotNull
    private List<UpdateSourceDevelopment> getUpdateSourceDevelopment(final String version, final String url) throws IOException {
        final List<UpdateSourceDevelopment> handlerList = new ArrayList<>();

        final UpdateSourceDevelopment handler = mock(UpdateSourceDevelopment.class);
        final Map<Version, String> versions = new HashMap<>();
        versions.put(new Version(version), url);
        when(handler.getDevelopmentVersions()).thenReturn(versions);
        handlerList.add(handler);

        return handlerList;
    }
}
