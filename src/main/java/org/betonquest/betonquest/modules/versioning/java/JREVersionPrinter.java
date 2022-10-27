package org.betonquest.betonquest.modules.versioning.java;

import lombok.CustomLog;

import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Utility class for getting information about the Java Runtime Environment version.
 */
@CustomLog
public final class JREVersionPrinter {

    private final String preReleaseInfo;
    private final String optional;
    private final String build;
    private final String versionStr;
    private final String vendor;

    /**
     * Prints the Java Runtime Environment version to the console.
     */
    public JREVersionPrinter() {
        final Runtime.Version jreVersion = Runtime.version();

        versionStr = jreVersion.version().stream().map(String::valueOf).collect(Collectors.joining("."));
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
        return "Running on JRE " + versionStr + " (build " + build + ", optional " + optional + ", pre-release info " + preReleaseInfo + ") by " + vendor;
    }
}


