package org.betonquest.betonquest.modules.updater.source.implementations;

import org.betonquest.betonquest.modules.updater.source.DevelopmentUpdateSource;
import org.betonquest.betonquest.modules.updater.source.ReleaseUpdateSource;
import org.betonquest.betonquest.modules.updater.source.UpdateSource;
import org.betonquest.betonquest.modules.versioning.Version;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a {@link UpdateSource} for the Nexus repository.
 */
public class NexusReleaseAndDevelopmentSource extends UpdateSource implements ReleaseUpdateSource, DevelopmentUpdateSource {

    /**
     * The sub path for the REST API of Nexus to append on the {@link NexusReleaseAndDevelopmentSource#apiUrl}.
     */
    public static final String SERVICE_REST_V_1 = "/service/rest/v1";
    /**
     * The sub path for a REST API call to Nexus to search for shaded jars
     * to append on a {@link NexusReleaseAndDevelopmentSource#SERVICE_REST_V_1}.
     */
    public static final String SEARCH_URL = "/search/assets?repository=betonquest&group=org.betonquest&name=betonquest&maven.extension=jar&maven.classifier=shaded&sort=version";
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
     * Creates a {@link NexusReleaseAndDevelopmentSource} with the given apiUrl.
     * Provide only the url to the nexus, not the url to the search itself.
     *
     * @param apiUrl path to the root page for a specific Nexus
     */
    public NexusReleaseAndDevelopmentSource(final String apiUrl) {
        super();
        this.apiUrl = apiUrl;
    }

    @Override
    public Map<Version, String> getReleaseVersions() throws IOException {
        return getVersions((versions, version, downloadUrl) -> {
            if (!version.hasQualifier() && !version.hasBuildNumber()) {
                versions.put(version, downloadUrl);
            }
        });
    }

    @Override
    public Map<Version, String> getDevelopmentVersions() throws IOException {
        return getVersions((versions, version, downloadUrl) -> {
            if (!version.hasQualifier() && !version.hasBuildNumber()) {
                return;
            }
            final boolean alreadyConsumed = versions.keySet().stream()
                    .anyMatch(v -> v.hasQualifier() && v.hasBuildNumber()
                            && v.getMajorVersion() == version.getMajorVersion()
                            && v.getMinorVersion() == version.getMinorVersion()
                            && v.getPatchVersion() == version.getPatchVersion());
            if (alreadyConsumed) {
                return;
            }
            final String pomXml = readStringFromURL(new URL(downloadUrl.replace("-shaded.jar", ".pom")));
            final Matcher matcher = POM_PATTERN.matcher(pomXml);
            if (matcher.find()) {
                final Version pomVersion = new Version(matcher.group("version"));
                versions.put(pomVersion, downloadUrl);
            }
        });
    }

    @NotNull
    private Map<Version, String> getVersions(final VersionConsumer consumer) throws IOException {
        final Map<Version, String> versions = new HashMap<>();

        String continuationToken = "";
        while (continuationToken != null) {
            final String url = apiUrl + SERVICE_REST_V_1 + SEARCH_URL + continuationToken;
            final JSONObject nexusResponse = new JSONObject(readStringFromURL(new URL(url)));
            final JSONArray items = nexusResponse.getJSONArray("items");
            for (int index = 0; index < items.length(); index++) {
                final JSONObject entry = items.getJSONObject(index);
                final JSONObject maven2 = entry.getJSONObject("maven2");
                final Version version = new Version(maven2.getString("version"));
                final String downloadUrl = entry.getString("downloadUrl");
                consumer.consume(versions, version, downloadUrl);
            }
            continuationToken = nexusResponse.isNull("continuationToken") ? null : CONTINUATION_TOKEN + nexusResponse.getString("continuationToken");
        }
        return versions;
    }

    /**
     * A functional interface to consume a version and a downloadUrl.
     */
    private interface VersionConsumer {
        /**
         * Consumes the given version and downloadUrl.
         *
         * @param versions    the versions map
         * @param version     the version
         * @param downloadUrl the downloadUrl
         */
        void consume(Map<Version, String> versions, Version version, String downloadUrl) throws IOException;
    }
}
