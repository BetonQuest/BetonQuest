package org.betonquest.betonquest.web.updater.source;

import org.betonquest.betonquest.versioning.Version;

import java.io.IOException;
import java.util.Map;

/**
 * A Source to download development builds from.
 */
public interface DevelopmentUpdateSource {

    /**
     * Gets a {@link Map} of {@link Version}s and {@link String}s from the given URL.
     * Every entry represents a version that is available to download.
     * The key is the version, while the value is the string URL where to download it from.
     *
     * @param currentVersion the current plugin version, to stop the search for older versions
     * @return the map of possible downloadable development versions
     * @throws IOException is thrown if any problem occurred while reading the version information.
     */
    Map<Version, String> getDevelopmentVersions(Version currentVersion) throws IOException;
}
