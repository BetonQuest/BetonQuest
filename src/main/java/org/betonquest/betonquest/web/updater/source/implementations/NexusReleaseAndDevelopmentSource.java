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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a {@link WebContentSource} for the Nexus repository.
 */
public class NexusReleaseAndDevelopmentSource implements ReleaseUpdateSource, DevelopmentUpdateSource {
    /**
     * The sub path for the REST API of Nexus to append on the {@link NexusReleaseAndDevelopmentSource#apiUrl}.
     */
    public static final String SERVICE_REST_V_1 = "/service/rest/v1";

    /**
     * The sub path for a REST API call to Nexus to search for shaded jars
     * to append on a {@link NexusReleaseAndDevelopmentSource#SERVICE_REST_V_1}.
     */
    public static final String SEARCH_URL = "/search/assets?repository=betonquest&group=org.betonquest&name=betonquest&maven.extension=jar&maven.classifier=shaded&sort=version&prerelease=%s";

    /**
     * The sub path for a REST API call to Nexus with pagination to append to any path that has pagination.
     */
    public static final String CONTINUATION_TOKEN = "&continuationToken=";

    /**
     * Regex to get the 'betonquest.version' property from the pom.xml file
     */
    public static final Pattern POM_PATTERN = Pattern.compile("<betonquest\\.version>(?<version>.*)</betonquest\\.version>");

    /**
     * The path to the root page for a specific Nexus.
     */
    private final String apiUrl;

    /**
     * The {@link ContentSource} to use to read the content from the given {@link URL}.
     */
    private final ContentSource contentSource;

    /**
     * Creates a {@link NexusReleaseAndDevelopmentSource} with the given apiUrl.
     * Provide only the url to the nexus, not the url to the search itself.
     *
     * @param apiUrl        path to the root page for a specific Nexus
     * @param contentSource the {@link ContentSource} to use to read the content from the given {@link URL}
     */
    public NexusReleaseAndDevelopmentSource(final String apiUrl, final ContentSource contentSource) {
        super();
        this.apiUrl = apiUrl;
        this.contentSource = contentSource;
    }

    @Override
    public Map<Version, String> getReleaseVersions(final Version currentVersion) throws IOException {
        return getVersions((versionStringMap, version, downloadUrl) -> {
            if (doVersionsEqual(version, currentVersion)) {
                return false;
            }
            versionStringMap.put(version, downloadUrl);
            return true;
        }, false);
    }

    @Override
    public Map<Version, String> getDevelopmentVersions(final Version currentVersion) throws IOException {
        return getVersions((versions, version, downloadUrl) -> {
            if (versions.keySet().stream().anyMatch(v -> doVersionsEqual(version, v))) {
                return true;
            }
            final String pomXml = contentSource.get(new URL(downloadUrl.replace("-shaded.jar", ".pom")));
            final Matcher matcher = POM_PATTERN.matcher(pomXml);
            if (matcher.find()) {
                final Version pomVersion = new Version(matcher.group("version"));
                versions.put(pomVersion, downloadUrl);
            }
            return !doVersionsEqual(version, currentVersion);
        }, true);
    }

    private Map<Version, String> getVersions(final VersionConsumer consumer, final boolean prereleases) throws IOException {
        final Map<Version, String> versions = new HashMap<>();

        String continuationToken = "";
        while (continuationToken != null) {
            final String url = apiUrl + SERVICE_REST_V_1 + String.format(SEARCH_URL, prereleases) + continuationToken;
            final JSONObject nexusResponse = new JSONObject(contentSource.get(new URL(url)));
            final JSONArray items = nexusResponse.getJSONArray("items");
            for (int index = 0; index < items.length(); index++) {
                final JSONObject entry = items.getJSONObject(index);
                final JSONObject maven2 = entry.getJSONObject("maven2");
                final Version version = new Version(maven2.getString("version"));
                final String downloadUrl = entry.getString("downloadUrl");
                if (!consumer.consume(versions, version, downloadUrl)) {
                    return versions;
                }
            }
            continuationToken = nexusResponse.isNull("continuationToken") ? null : CONTINUATION_TOKEN + nexusResponse.getString("continuationToken");
        }
        return versions;
    }

    private boolean doVersionsEqual(final Version version1, final Version version2) {
        return version1.getMajorVersion() == version2.getMajorVersion()
                && version1.getMinorVersion() == version2.getMinorVersion()
                && version1.getPatchVersion() == version2.getPatchVersion();
    }

    /**
     * A functional interface to consume a version and a downloadUrl.
     */
    private interface VersionConsumer {
        /**
         * Consumes the given version and downloadUrl.
         *
         * @param versions    the version map
         * @param version     the version
         * @param downloadUrl the downloadUrl
         * @return true if the search should be continued, false otherwise
         */
        boolean consume(Map<Version, String> versions, Version version, String downloadUrl) throws IOException;
    }
}
