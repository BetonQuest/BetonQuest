package org.betonquest.betonquest.modules.updater.source.implementations;

import org.betonquest.betonquest.modules.updater.source.UpdateSourceReleaseHandler;
import org.betonquest.betonquest.modules.versioning.Version;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GitHubReleasesSource implements UpdateSourceReleaseHandler {
    private final String apiUrl;

    public GitHubReleasesSource(final String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Override
    public Map<Version, String> getReleaseVersions() throws IOException {
        final Map<Version, String> versions = new HashMap<>();
        final JSONArray releaseArray = new JSONArray(readStringFromURL(apiUrl));
        for (int index = 0; index < releaseArray.length(); index++) {
            final JSONObject release = releaseArray.getJSONObject(index);
            final Version version = new Version(release.getString("tag_name").substring(1));
            final JSONArray assetsArray = release.getJSONArray("assets");
            for (int i = 0; i < assetsArray.length(); i++) {
                final JSONObject asset = assetsArray.getJSONObject(i);
                if ("BetonQuest.jar".equals(asset.getString("name"))) {
                    final String url = asset.getString("browser_download_url");
                    versions.put(version, url);
                }
            }
        }
        return versions;
    }

    @Override
    public void handleResponseCode(final int responseCode) throws IOException {
        if (responseCode == 403) {
            throw new IOException("It looks like too many requests were made to the update server, please wait until you have been unblocked.");
        }
    }
}
