package org.betonquest.betonquest.modules.updater.source.implementations;

import org.betonquest.betonquest.modules.updater.source.ReleaseUpdateSource;
import org.betonquest.betonquest.modules.updater.source.UpdateSource;
import org.betonquest.betonquest.modules.versioning.Version;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a {@link ReleaseUpdateSource} for GitHub's releases API.
 */
public class GitHubReleaseSource extends UpdateSource implements ReleaseUpdateSource {

    /**
     * The basic url for the api of GitHub.
     */
    private static final String RELEASES_URL = "/releases";
    /**
     * The apiUrl to the GitHub releases API target.
     */
    private final String apiUrl;

    /**
     * Creates a {@link GitHubReleaseSource} with the given apiUrl.
     * Provide only the url to the repository, not the url to the releases itself.
     *
     * @param apiUrl to the GitHub releases API target
     */
    public GitHubReleaseSource(final String apiUrl) {
        super();
        this.apiUrl = apiUrl;
    }

    @Override
    public Map<Version, String> getReleaseVersions() throws IOException {
        final Map<Version, String> versions = new HashMap<>();
        final JSONArray releaseArray = new JSONArray(readStringFromURL(new URL(apiUrl + RELEASES_URL)));
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
        if (responseCode == RESPONSE_CODE_403) {
            throw new IOException("It looks like too many requests were made to the update server, please wait until you have been unblocked.");
        }
    }
}
