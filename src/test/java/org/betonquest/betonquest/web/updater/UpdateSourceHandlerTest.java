package org.betonquest.betonquest.web.updater;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.web.updater.source.DevelopmentUpdateSource;
import org.betonquest.betonquest.web.updater.source.ReleaseUpdateSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class tests the {@link UpdateSourceHandler}.
 */
@ExtendWith(MockitoExtension.class)
class UpdateSourceHandlerTest {
    @Mock
    private BetonQuestLogger logger;

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void development_update_available() throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.PATCH, true);
        final Version version = new Version("2.0.0-DEV-3");
        final List<ReleaseUpdateSource> releaseHandlerList = new ArrayList<>();
        final List<DevelopmentUpdateSource> developmentHandlerList = getUpdateSourceDevelopment(version, "2.0.0-DEV-201");

        final UpdateSourceHandler handler = new UpdateSourceHandler(logger, releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0-DEV-201", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNotNull(latest.getValue(), "Expected an URL");
        assertEquals("https://betonquest.org/development", latest.getValue(), "Actual URL does not match expected");
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void no_development_update_available() throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.MINOR, true);
        final Version version = new Version("2.0.0-DEV-3");
        final List<ReleaseUpdateSource> releaseHandlerList = new ArrayList<>();
        final List<DevelopmentUpdateSource> developmentHandlerList = getUpdateSourceDevelopment(version, "2.0.0-DEV-3");

        final UpdateSourceHandler handler = new UpdateSourceHandler(logger, releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0-DEV-3", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNull(latest.getValue(), "Expected no update URL");
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void release_update_available() throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.MAJOR, false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<ReleaseUpdateSource> releaseHandlerList = getUpdateSourceRelease(version);
        final List<DevelopmentUpdateSource> developmentHandlerList = new ArrayList<>();

        final UpdateSourceHandler handler = new UpdateSourceHandler(logger, releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNotNull(latest.getValue(), "Expected an URL");
        assertEquals("https://betonquest.org/release", latest.getValue(), "Actual URL does not match expected");
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void no_release_update_available() throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.PATCH, true);
        final Version version = new Version("2.0.0");
        final List<ReleaseUpdateSource> releaseHandlerList = getUpdateSourceRelease(version);
        final List<DevelopmentUpdateSource> developmentHandlerList = new ArrayList<>();

        final UpdateSourceHandler handler = new UpdateSourceHandler(logger, releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNull(latest.getValue(), "Expected no update URL");
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void release_and_development_update_available_forced() throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.MINOR, true);
        when(config.isForcedStrategy()).thenReturn(true);
        final Version version = new Version("2.0.0-DEV-3");
        final List<ReleaseUpdateSource> releaseHandlerList = getUpdateSourceRelease(version);
        final List<DevelopmentUpdateSource> developmentHandlerList = new ArrayList<>();

        final UpdateSourceHandler handler = new UpdateSourceHandler(logger, releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNotNull(latest.getValue(), "Expected an URL");
        assertEquals("https://betonquest.org/release", latest.getValue(), "Actual URL does not match expected");
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void release_and_development_update_available_not_forced() throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.MAJOR, true);
        when(config.isForcedStrategy()).thenReturn(false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<ReleaseUpdateSource> releaseHandlerList = getUpdateSourceRelease(version);
        final List<DevelopmentUpdateSource> developmentHandlerList = getUpdateSourceDevelopment(version, "2.0.1-DEV-201");

        final UpdateSourceHandler handler = new UpdateSourceHandler(logger, releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.1-DEV-201", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNotNull(latest.getValue(), "Expected an URL");
        assertEquals("https://betonquest.org/development", latest.getValue(), "Actual URL does not match expected");
    }

    @Test
    void release_and_development_no_update_available() {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.PATCH, true);
        final Version version = new Version("2.0.0-DEV-3");
        final List<ReleaseUpdateSource> releaseHandlerList = new ArrayList<>();
        final List<DevelopmentUpdateSource> developmentHandlerList = new ArrayList<>();

        final UpdateSourceHandler handler = new UpdateSourceHandler(logger, releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0-DEV-3", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNull(latest.getValue(), "Expected no update URL");
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void release_throws_UnknownHostException() throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.MINOR, false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<ReleaseUpdateSource> releaseHandlerList = new ArrayList<>();
        final List<DevelopmentUpdateSource> developmentHandlerList = new ArrayList<>();

        final ReleaseUpdateSource releaseHandler = mock(ReleaseUpdateSource.class);
        when(releaseHandler.getReleaseVersions(version)).thenThrow(new UnknownHostException("Unknown host Test"));
        releaseHandlerList.add(releaseHandler);

        final UpdateSourceHandler handler = new UpdateSourceHandler(logger, releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0-DEV-3", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNull(latest.getValue(), "Expected no update URL");

        verify(logger, times(1)).warn("Could not fetch version updates. Probably the host is currently not available: Unknown host Test");
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void release_throws_IOException() throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.MAJOR, false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<ReleaseUpdateSource> releaseHandlerList = new ArrayList<>();
        final List<DevelopmentUpdateSource> developmentHandlerList = new ArrayList<>();

        final ReleaseUpdateSource releaseHandler = mock(ReleaseUpdateSource.class);
        when(releaseHandler.getReleaseVersions(version)).thenThrow(new IOException("Unexpected problem"));
        releaseHandlerList.add(releaseHandler);

        final UpdateSourceHandler handler = new UpdateSourceHandler(logger, releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0-DEV-3", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNull(latest.getValue(), "Expected no update URL");

        verify(logger, times(1)).warn(matches("Could not fetch version updates from a source: Unexpected problem"), any(IOException.class));
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void one_of_two_releases_throws_IOException() throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.MAJOR, false);
        final Version version = new Version("2.0.0-DEV-3");
        final List<ReleaseUpdateSource> releaseHandlerList = getUpdateSourceRelease(version);
        final List<DevelopmentUpdateSource> developmentHandlerList = new ArrayList<>();

        final ReleaseUpdateSource releaseHandler = mock(ReleaseUpdateSource.class);
        when(releaseHandler.getReleaseVersions(version)).thenThrow(new IOException("Unexpected problem"));
        releaseHandlerList.add(releaseHandler);

        final UpdateSourceHandler handler = new UpdateSourceHandler(logger, releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNotNull(latest.getValue(), "Expected an URL");
        assertEquals("https://betonquest.org/release", latest.getValue(), "Actual URL does not match expected");

        verify(logger, times(1)).warn(matches("Could not fetch version updates from a source: Unexpected problem"), any(IOException.class));
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void development_throws_UnknownHostException() throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.PATCH, true);
        final Version version = new Version("2.0.0-DEV-3");
        final List<ReleaseUpdateSource> releaseHandlerList = new ArrayList<>();
        final List<DevelopmentUpdateSource> developmentHandlerList = new ArrayList<>();

        final DevelopmentUpdateSource developmentHandler = mock(DevelopmentUpdateSource.class);
        when(developmentHandler.getDevelopmentVersions(version)).thenThrow(new UnknownHostException("Unknown host Test"));
        developmentHandlerList.add(developmentHandler);

        final UpdateSourceHandler handler = new UpdateSourceHandler(logger, releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0-DEV-3", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNull(latest.getValue(), "Expected no update URL");

        verify(logger, times(1)).warn("Could not fetch version updates. Probably the host is currently not available: Unknown host Test");
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void development_throws_IOException() throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.MINOR, true);
        final Version version = new Version("2.0.0-DEV-3");
        final List<ReleaseUpdateSource> releaseHandlerList = new ArrayList<>();
        final List<DevelopmentUpdateSource> developmentHandlerList = new ArrayList<>();

        final DevelopmentUpdateSource developmentHandler = mock(DevelopmentUpdateSource.class);
        when(developmentHandler.getDevelopmentVersions(version)).thenThrow(new IOException("Unexpected problem"));
        developmentHandlerList.add(developmentHandler);

        final UpdateSourceHandler handler = new UpdateSourceHandler(logger, releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0-DEV-3", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNull(latest.getValue(), "Expected no update URL");

        verify(logger, times(1)).warn(eq("Could not fetch version updates from a source: Unexpected problem"), any(IOException.class));
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void one_of_two_developments_throws_IOException() throws IOException {
        final UpdaterConfig config = getUpdaterConfig(UpdateStrategy.MINOR, true);
        final Version version = new Version("2.0.0-DEV-3");
        final List<ReleaseUpdateSource> releaseHandlerList = new ArrayList<>();
        final List<DevelopmentUpdateSource> developmentHandlerList = getUpdateSourceDevelopment(version, "2.0.0-DEV-201");

        final DevelopmentUpdateSource developmentHandler = mock(DevelopmentUpdateSource.class);
        when(developmentHandler.getDevelopmentVersions(version)).thenThrow(new IOException("Unexpected problem"));
        developmentHandlerList.add(developmentHandler);

        final UpdateSourceHandler handler = new UpdateSourceHandler(logger, releaseHandlerList, developmentHandlerList);
        final Pair<Version, String> latest = handler.searchUpdate(config, version, "DEV");

        assertEquals("2.0.0-DEV-201", latest.getKey().getVersion(), "Actual version does not match expected");
        assertNotNull(latest.getValue(), "Expected an URL");
        assertEquals("https://betonquest.org/development", latest.getValue(), "Actual URL does not match expected");

        verify(logger, times(1)).warn(eq("Could not fetch version updates from a source: Unexpected problem"), any(IOException.class));
        verifyNoMoreInteractions(logger);
    }

    private UpdaterConfig getUpdaterConfig(final UpdateStrategy strategy, final boolean devDownloadEnabled) {
        final UpdaterConfig config = mock(UpdaterConfig.class);
        when(config.getStrategy()).thenReturn(strategy);
        when(config.isDevDownloadEnabled()).thenReturn(devDownloadEnabled);
        return config;
    }

    private List<ReleaseUpdateSource> getUpdateSourceRelease(final Version version) throws IOException {
        final List<ReleaseUpdateSource> handlerList = new ArrayList<>();

        final ReleaseUpdateSource handler = mock(ReleaseUpdateSource.class);
        final Map<Version, String> versions = new HashMap<>();
        versions.put(new Version("2.0.0"), "https://betonquest.org/release");
        when(handler.getReleaseVersions(version)).thenReturn(versions);
        handlerList.add(handler);

        return handlerList;
    }

    private List<DevelopmentUpdateSource> getUpdateSourceDevelopment(final Version version, final String versionString) throws IOException {
        final List<DevelopmentUpdateSource> handlerList = new ArrayList<>();

        final DevelopmentUpdateSource handler = mock(DevelopmentUpdateSource.class);
        final Map<Version, String> versions = new HashMap<>();
        versions.put(new Version(versionString), "https://betonquest.org/development");
        when(handler.getDevelopmentVersions(version)).thenReturn(versions);
        handlerList.add(handler);

        return handlerList;
    }
}
