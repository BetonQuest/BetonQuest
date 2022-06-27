package org.betonquest.betonquest.modules.updater.source.implementations;

import org.betonquest.betonquest.modules.updater.source.DevelopmentUpdateSource;
import org.betonquest.betonquest.modules.versioning.Version;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This is a {@link DevelopmentUpdateSource} for the BetonQuest API backend.
 */
public class BetonQuestDevelopmentSource implements DevelopmentUpdateSource {
    /**
     * The API URL path to the latest versions.
     */
    public static final String DEV_API_LATEST = "/builds/latest";
    /**
     * The API URL path to the real file for download.
     */
    public static final String DEV_API_DOWNLOAD = "/builds/download/:version/:versionNumber/BetonQuest.jar";
    /**
     * The indicator for dev versions.
     */
    private static final String DEV_INDICATOR = "-DEV-";
    /**
     * The base apiUrl of this BetonQuest update source.
     */
    private final String apiUrl;

    /**
     * Create a {@link BetonQuestDevelopmentSource} with the given apiUrl.
     *
     * @param apiUrl root to the BetonQuest update source
     */
    public BetonQuestDevelopmentSource(final String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Override
    public Map<Version, String> getDevelopmentVersions() throws IOException {
        final Map<Version, String> versions = new HashMap<>();
        final JSONObject json = new JSONObject(readStringFromURL(new URL(apiUrl + DEV_API_LATEST)));
        final Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            final String coreVersion = keys.next();
            final String buildNumber = json.getString(coreVersion);
            final Version version = new Version(coreVersion + DEV_INDICATOR + buildNumber);
            final String url = (apiUrl + DEV_API_DOWNLOAD).replace(":versionNumber", buildNumber).replace(":version", coreVersion);
            versions.put(version, url);
        }
        return versions;
    }
}
