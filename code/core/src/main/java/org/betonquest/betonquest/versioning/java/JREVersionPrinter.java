package org.betonquest.betonquest.versioning.java;

import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Utility class for getting information about the Java Runtime Environment version.
 */

public final class JREVersionPrinter {
    /**
     * The version of the JRE.
     */
    private final String version;

    /**
     * The build number of the JRE.
     */
    private final String build;

    /**
     * Optional information about the version of the JRE, may not be provided.
     */
    private final String optional;

    /**
     * Information about the preRelease version of the JRE, may not be provided.
     */
    private final String preReleaseInfo;

    /**
     * The vendor of the JRE.
     */
    private final String vendor;

    /**
     * Prepares the JREVersionPrinter for generating output.
     */
    public JREVersionPrinter() {
        final Runtime.Version jreVersion = Runtime.version();

        version = jreVersion.version().stream().map(String::valueOf).collect(Collectors.joining("."));
        build = jreVersion.build().map(String::valueOf).orElse("N/A");
        optional = jreVersion.optional().orElse("N/A");
        preReleaseInfo = jreVersion.pre().orElse("N/A");

        final Properties properties = System.getProperties();
        vendor = properties.getProperty("java.vendor");
    }

    /**
     * Gets a human-readable string with information about the Java Runtime Environment version.
     *
     * @return the information string
     */
    public String getMessage() {
        return "Running on JRE " + version + " (build " + build + ", optional " + optional + ", pre-release info " + preReleaseInfo + ") by " + vendor;
    }
}


