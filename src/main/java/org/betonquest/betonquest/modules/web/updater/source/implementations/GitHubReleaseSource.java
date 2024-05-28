package org.betonquest.betonquest.modules.web.updater.source.implementations;

import org.betonquest.betonquest.modules.versioning.Version;
import org.betonquest.betonquest.modules.web.ContentSource;
import org.betonquest.betonquest.modules.web.WebContentSource;
import org.betonquest.betonquest.modules.web.updater.source.ReleaseUpdateSource;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;

/**
 * This is a {@link ReleaseUpdateSource} for GitHub's releases API.
 */
public class GitHubReleaseSource implements ReleaseUpdateSource {
    /**
     * The sub path of the release API of GitHub to append on the {@link GitHubReleaseSource#apiUrl}.
     */
    public static final String RELEASES_URL = "/releases";

    /**
     * The default {@link WebContentSource.HTTPCodeHandler} to use for GitHub releases api.
     */
    public static final WebContentSource.HTTPCodeHandler HTTP_CODE_HANDLER = (code) -> {
        if (code == HTTP_FORBIDDEN) {
            throw new IOException("It looks like too many requests were made to the update server, please wait until you have been unblocked.");
        }
    };

    /**
     * The name of the jar.
     */
    private static final String JAR_NAME = "BetonQuest.jar";

    /**
     * The path to the GitHub API for a specific repository.
     */
    private final String apiUrl;

    /**
     * The {@link ContentSource} to use to read the content from the given {@link URL}.
     */
    private final ContentSource contentSource;

    /**
     * Creates a {@link GitHubReleaseSource} with the given apiUrl.
     * Provide only the path to the repository, not the path to a specific API backend itself.
     *
     * @param apiUrl        path to the GitHub API for a specific repository
     * @param contentSource the {@link ContentSource} to use to read the content from the given {@link URL}
     */
    public GitHubReleaseSource(final String apiUrl, final ContentSource contentSource) {
        super();
        this.apiUrl = apiUrl;
        this.contentSource = contentSource;
    }

    @Override
    public Map<Version, String> getReleaseVersions() throws IOException {
        final Map<Version, String> versions = new HashMap<>();
        final JSONArray releaseArray = new JSONArray(contentSource.get(new URL(apiUrl + RELEASES_URL)));
        for (int index = 0; index < releaseArray.length(); index++) {
            final JSONObject release = releaseArray.getJSONObject(index);
            final Version version = new Version(release.getString("tag_name").substring(1));
            final JSONArray assetsArray = release.getJSONArray("assets");
            for (int i = 0; i < assetsArray.length(); i++) {
                final JSONObject asset = assetsArray.getJSONObject(i);
                if (JAR_NAME.equals(asset.getString("name"))) {
                    final String url = asset.getString("browser_download_url");
                    versions.put(version, url);
                }
            }
        }
        return versions;
    }
}
