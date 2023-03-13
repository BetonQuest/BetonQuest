package org.betonquest.betonquest.modules.web.updater;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.betonquest.betonquest.modules.versioning.UpdateStrategy;
import org.betonquest.betonquest.modules.versioning.Version;
import org.betonquest.betonquest.modules.web.updater.source.DevelopmentUpdateSource;
import org.betonquest.betonquest.modules.web.updater.source.ReleaseUpdateSource;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class tests the {@link UpdateSourceHandler}.
 */
@ExtendWith(BetonQuestLoggerService.class)
class UpdateSourceHandlerTest {

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testDevelopmentUpdateAvailable(final LogValidator validator) throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.PATCH, true, false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<ReleaseUpdateSource> releaseHandlerList = new ArrayList<>();
        final List<DevelopmentUpdateSource> developmentHandlerList = getUpdateSourceDevelopment("2.0.0-DEV-201");

        final UpdateSourceHandler handler = new UpdateSourceHandler(releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0-DEV-201", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNotNull(latest.getValue(), "Expected an URL");
        assertEquals("https://betonquest.org/development", latest.getValue(), "Actual URL does not match expected");

        validator.assertEmpty();
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testNoDevelopmentUpdateAvailable(final LogValidator validator) throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.MINOR, true, false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<ReleaseUpdateSource> releaseHandlerList = new ArrayList<>();
        final List<DevelopmentUpdateSource> developmentHandlerList = getUpdateSourceDevelopment("2.0.0-DEV-3");

        final UpdateSourceHandler handler = new UpdateSourceHandler(releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0-DEV-3", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNull(latest.getValue(), "Expected no update URL");

        validator.assertEmpty();
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testReleaseUpdateAvailable(final LogValidator validator) throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.MAJOR, false, false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<ReleaseUpdateSource> releaseHandlerList = getUpdateSourceRelease();
        final List<DevelopmentUpdateSource> developmentHandlerList = new ArrayList<>();

        final UpdateSourceHandler handler = new UpdateSourceHandler(releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNotNull(latest.getValue(), "Expected an URL");
        assertEquals("https://betonquest.org/release", latest.getValue(), "Actual URL does not match expected");

        validator.assertEmpty();
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testNoReleaseUpdateAvailable(final LogValidator validator) throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.PATCH, true, false);
        final Version version = new Version("2.0.0");
        final List<ReleaseUpdateSource> releaseHandlerList = getUpdateSourceRelease();
        final List<DevelopmentUpdateSource> developmentHandlerList = new ArrayList<>();

        final UpdateSourceHandler handler = new UpdateSourceHandler(releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNull(latest.getValue(), "Expected no update URL");

        validator.assertEmpty();
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testReleaseAndDevelopmentUpdateAvailableForced(final LogValidator validator) throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.MINOR, true, true);
        final Version version = new Version("2.0.0-DEV-3");
        final List<ReleaseUpdateSource> releaseHandlerList = getUpdateSourceRelease();
        final List<DevelopmentUpdateSource> developmentHandlerList = getUpdateSourceDevelopment("2.0.1-DEV-201");

        final UpdateSourceHandler handler = new UpdateSourceHandler(releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNotNull(latest.getValue(), "Expected an URL");
        assertEquals("https://betonquest.org/release", latest.getValue(), "Actual URL does not match expected");

        validator.assertEmpty();
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testReleaseAndDevelopmentUpdateAvailableNotForced(final LogValidator validator) throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.MAJOR, true, false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<ReleaseUpdateSource> releaseHandlerList = getUpdateSourceRelease();
        final List<DevelopmentUpdateSource> developmentHandlerList = getUpdateSourceDevelopment("2.0.1-DEV-201");

        final UpdateSourceHandler handler = new UpdateSourceHandler(releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.1-DEV-201", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNotNull(latest.getValue(), "Expected an URL");
        assertEquals("https://betonquest.org/development", latest.getValue(), "Actual URL does not match expected");

        validator.assertEmpty();
    }

    @Test
    void testReleaseAndDevelopmentNoUpdateAvailable(final LogValidator validator) {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.PATCH, true, false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<ReleaseUpdateSource> releaseHandlerList = new ArrayList<>();
        final List<DevelopmentUpdateSource> developmentHandlerList = new ArrayList<>();

        final UpdateSourceHandler handler = new UpdateSourceHandler(releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0-DEV-3", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNull(latest.getValue(), "Expected no update URL");

        validator.assertEmpty();
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testReleaseThrowsUnknownHostException(final LogValidator validator) throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.MINOR, false, false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<ReleaseUpdateSource> releaseHandlerList = new ArrayList<>();
        final List<DevelopmentUpdateSource> developmentHandlerList = new ArrayList<>();

        final ReleaseUpdateSource releaseHandler = mock(ReleaseUpdateSource.class);
        when(releaseHandler.getReleaseVersions()).thenThrow(new UnknownHostException("Unknown host Test"));
        releaseHandlerList.add(releaseHandler);

        final UpdateSourceHandler handler = new UpdateSourceHandler(releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0-DEV-3", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNull(latest.getValue(), "Expected no update URL");

        validator.assertLogEntry(Level.WARNING, "The update server for release builds is currently not available!");
        validator.assertEmpty();
    }

    @Test
    void testReleaseThrowsIOException(final LogValidator validator) throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.MAJOR, false, false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<ReleaseUpdateSource> releaseHandlerList = new ArrayList<>();
        final List<DevelopmentUpdateSource> developmentHandlerList = new ArrayList<>();

        final ReleaseUpdateSource releaseHandler = mock(ReleaseUpdateSource.class);
        when(releaseHandler.getReleaseVersions()).thenThrow(new IOException("Unexpected problem"));
        releaseHandlerList.add(releaseHandler);

        final UpdateSourceHandler handler = new UpdateSourceHandler(releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0-DEV-3", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNull(latest.getValue(), "Expected no update URL");

        validator.assertLogEntry(Level.WARNING, "Could not get the latest release build! Unexpected problem");
        validator.assertLogEntry(Level.FINE, "Additional stacktrace:", IOException.class, "Unexpected problem");
        validator.assertEmpty();
    }

    @Test
    void testDevelopmentThrowsUnknownHostException(final LogValidator validator) throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.PATCH, true, false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<ReleaseUpdateSource> releaseHandlerList = new ArrayList<>();
        final List<DevelopmentUpdateSource> developmentHandlerList = new ArrayList<>();

        final DevelopmentUpdateSource developmentHandler = mock(DevelopmentUpdateSource.class);
        when(developmentHandler.getDevelopmentVersions()).thenThrow(new UnknownHostException("Unknown host Test"));
        developmentHandlerList.add(developmentHandler);

        final UpdateSourceHandler handler = new UpdateSourceHandler(releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0-DEV-3", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNull(latest.getValue(), "Expected no update URL");

        validator.assertLogEntry(Level.WARNING, "The update server for dev builds is currently not available!");
        validator.assertEmpty();
    }

    @Test
    void testDevelopmentThrowsIOException(final LogValidator validator) throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.MINOR, true, false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<ReleaseUpdateSource> releaseHandlerList = new ArrayList<>();
        final List<DevelopmentUpdateSource> developmentHandlerList = new ArrayList<>();

        final DevelopmentUpdateSource developmentHandler = mock(DevelopmentUpdateSource.class);
        when(developmentHandler.getDevelopmentVersions()).thenThrow(new IOException("Unexpected problem"));
        developmentHandlerList.add(developmentHandler);

        final UpdateSourceHandler handler = new UpdateSourceHandler(releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0-DEV-3", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNull(latest.getValue(), "Expected no update URL");

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
    private List<ReleaseUpdateSource> getUpdateSourceRelease() throws IOException {
        final List<ReleaseUpdateSource> handlerList = new ArrayList<>();

        final ReleaseUpdateSource handler = mock(ReleaseUpdateSource.class);
        final Map<Version, String> versions = new HashMap<>();
        versions.put(new Version("2.0.0"), "https://betonquest.org/release");
        when(handler.getReleaseVersions()).thenReturn(versions);
        handlerList.add(handler);

        return handlerList;
    }

    @NotNull
    private List<DevelopmentUpdateSource> getUpdateSourceDevelopment(final String version) throws IOException {
        final List<DevelopmentUpdateSource> handlerList = new ArrayList<>();

        final DevelopmentUpdateSource handler = mock(DevelopmentUpdateSource.class);
        final Map<Version, String> versions = new HashMap<>();
        versions.put(new Version(version), "https://betonquest.org/development");
        when(handler.getDevelopmentVersions()).thenReturn(versions);
        handlerList.add(handler);

        return handlerList;
    }
}
