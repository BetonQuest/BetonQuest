package org.betonquest.betonquest.modules.versioning.java;

import lombok.CustomLog;

import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Utility class for getting information about the Java Runtime Environment version.
 */
@CustomLog
public final class JREVersionPrinter {

    /**
     * Prints the Java Runtime Environment version to the console.
     */
    public JREVersionPrinter() {
        final Runtime.Version jreVersion = Runtime.version();

        final String versionStr = jreVersion.version().stream().map(String::valueOf).collect(Collectors.joining("."));
        final String build = jreVersion.build().map(String::valueOf).orElse("N/A");
        final String optional = jreVersion.optional().orElse("N/A");
        final String preReleaseInfo = jreVersion.pre().orElse("N/A");

        final Properties properties = System.getProperties();
        final String vendor = properties.getProperty("java.vendor");

        LOG.info("Running on JRE " + versionStr + " (build " + build + ", optional " + optional + ", pre-release info " + preReleaseInfo + ") by " + vendor);
    }
}


