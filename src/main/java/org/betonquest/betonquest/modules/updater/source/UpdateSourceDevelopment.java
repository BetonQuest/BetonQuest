package org.betonquest.betonquest.modules.updater.source;

import org.betonquest.betonquest.modules.versioning.Version;

import java.io.IOException;
import java.util.Map;

public interface UpdateSourceDevelopment extends UpdateSource {

    Map<Version, String> getDevelopmentVersions() throws IOException;
}
