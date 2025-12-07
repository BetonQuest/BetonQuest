package org.betonquest.betonquest.web.updater.source.implementations;

import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.web.ContentSource;
import org.betonquest.betonquest.web.WebContentSource;
import org.betonquest.betonquest.web.updater.source.DevelopmentUpdateSource;
import org.betonquest.betonquest.web.updater.source.ReleaseUpdateSource;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a {@link WebContentSource} for the Reposilite repository.
 */
public class ReposiliteReleaseAndDevelopmentSource implements ReleaseUpdateSource, DevelopmentUpdateSource {

    /**
     * The REST API call to Reposilite to get version information.
     */
    public static final String SEARCH_URL = "/api/pommapper/id/%s?limit=1&since=%s";

    /**
     * URL filter to get only snapshot versions.
     */
    public static final String SNAPSHOT_FILTER = "&releases=false";

    /**
     * URL filter to get only release versions.
     */
    public static final String RELEASE_FILTER = "&snapshots=false";

    /**
     * Path to the root page for a specific Reposilite.
     */
    private final String reposiliteUrl;

    /**
     * The repository to search in.
     */
    private final String repository;

    /**
     * The pom-mapper plugin id to search for.
     */
    private final String pomMapperId;

    /**
     * The {@link ContentSource} to use to read the content from the given {@link URL}.
     */
    private final ContentSource contentSource;

    /**
     * Creates a {@link ReposiliteReleaseAndDevelopmentSource} with the given apiUrl.
     * Provide only the url to the reposilite, not the url to the search itself.
     *
     * @param reposiliteUrl path to the root page for a specific Reposilite
     * @param repository    the repository to search in
     * @param pomMapperId   the pom-mapper plugin id to search for
     * @param contentSource the {@link ContentSource} to use to read the content from the given {@link URL}
     */
    public ReposiliteReleaseAndDevelopmentSource(final String reposiliteUrl, final String repository, final String pomMapperId, final ContentSource contentSource) {
        super();
        this.reposiliteUrl = reposiliteUrl;
        this.repository = repository;
        this.pomMapperId = pomMapperId;
        this.contentSource = contentSource;
    }

    @Override
    public Map<Version, String> getReleaseVersions(final Version currentVersion) throws IOException {
        return getVersions(currentVersion, RELEASE_FILTER);
    }

    @Override
    public Map<Version, String> getDevelopmentVersions(final Version currentVersion) throws IOException {
        return getVersions(currentVersion, SNAPSHOT_FILTER);
    }

    private Map<Version, String> getVersions(final Version currentVersion, final String filter) throws IOException {
        final Map<Version, String> versions = new HashMap<>();
        final String url = reposiliteUrl + String.format(SEARCH_URL, pomMapperId, getAdjustedVersion(currentVersion)) + filter;
        final JSONArray items = new JSONArray(contentSource.get(new URL(url)));
        for (int index = 0; index < items.length(); index++) {
            final JSONObject groupEntry = items.getJSONObject(index);
            if (!groupEntry.has("versions")) {
                continue;
            }
            final JSONArray groupVersions = groupEntry.getJSONArray("versions");
            if (groupVersions.isEmpty()) {
                continue;
            }
            final JSONObject versionEntry = groupVersions.getJSONObject(0);
            if (!versionEntry.has("jar")) {
                continue;
            }
            final String downloadPath = versionEntry.getString("jar");
            if (!versionEntry.has("entries")) {
                continue;
            }
            final JSONObject entries = versionEntry.getJSONObject("entries");
            if (!entries.has("pluginVersion")) {
                continue;
            }
            final String pluginVersion = entries.getString("pluginVersion");
            versions.put(new Version(pluginVersion),
                    reposiliteUrl + "/" + repository + "/" + downloadPath.replace(".jar", "-shaded.jar"));
        }
        return versions;
    }

    private String getAdjustedVersion(final Version currentVersion) {
        if (!currentVersion.hasQualifier() && !currentVersion.hasBuildNumber()) {
            return currentVersion.toString();
        }
        return currentVersion.getMajorVersion() + "." + currentVersion.getMinorVersion() + "." + currentVersion.getPatchVersion()
                + "-SNAPSHOT";
    }
}
