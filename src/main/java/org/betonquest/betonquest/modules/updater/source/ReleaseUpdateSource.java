package org.betonquest.betonquest.modules.updater.source;

import org.betonquest.betonquest.modules.versioning.Version;

import java.io.IOException;
import java.util.Map;

/**
 * A Source to download release builds from.
 */
public interface ReleaseUpdateSource extends UpdateSource {
    /**
     * Get a {@link Map} of {@link Version}s and {@link String}s from the given URL.
     * Every entry represent one possible version, that is available for a download.
     * The key is the version, while the value is the string URL where to download it from.
     *
     * @return the list of possible downloadable release versions
     * @throws IOException is thrown if any problem occurred while reading the version information.
     */
    Map<Version, String> getReleaseVersions() throws IOException;
}
