package org.betonquest.betonquest.lib.version;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Utility class for getting information about the Java Runtime Environment version.
 */
public final class JREVersionReader {

    /**
     * The version information of the JRE.
     */
    private final Map<String, String> versionInformation;

    /**
     * Prepares the JREVersionReader by reading and storing the version information.
     */
    public JREVersionReader() {
        final Runtime.Version jreVersion = Runtime.version();

        versionInformation = new HashMap<>();

        final String version = jreVersion.version().stream().map(String::valueOf).collect(Collectors.joining("."));
        final String build = jreVersion.build().map(String::valueOf).orElse("N/A");
        final String optional = jreVersion.optional().orElse("N/A");
        final String preReleaseInfo = jreVersion.pre().orElse("N/A");

        final Properties properties = System.getProperties();
        final String vendor = properties.getProperty("java.vendor");

        versionInformation.put("version", version);
        versionInformation.put("build", build);
        versionInformation.put("optional", optional);
        versionInformation.put("preReleaseInfo", preReleaseInfo);
        versionInformation.put("vendor", vendor);
    }

    /**
     * Gets the version information of the JRE.
     *
     * @return the version information
     */
    public Map<String, String> getVersionInformation() {
        return versionInformation;
    }

    /**
     * Gets a human-readable string with information about the Java Runtime Environment version.
     *
     * @return the information string
     */
    public String getReadableVersionInformation() {
        return "Running on JRE %s (build %s, optional %s, pre-release info %s) by %s"
                .formatted(versionInformation.get("version"), versionInformation.get("build"), versionInformation.get("optional"),
                        versionInformation.get("preReleaseInfo"), versionInformation.get("vendor"));
    }

    @Override
    public String toString() {
        return getReadableVersionInformation();
    }
}
