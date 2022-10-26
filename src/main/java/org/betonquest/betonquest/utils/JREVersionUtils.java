package org.betonquest.betonquest.utils;

import lombok.CustomLog;

import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Utility class for getting information about the Java Runtime Environment version.
 */
@CustomLog
public final class JREVersionUtils {

    /**
     * This is a utility class. You should not instantiate it.
     */
    private JREVersionUtils() {
    }

    /**
     * Logs the JRE version to the debug log.
     */
    public static void logJREVersion() {
        final Runtime.Version jreVersion = Runtime.version();

        final String versionStr = jreVersion.version().stream().map(String::valueOf).collect(Collectors.joining("."));
        final String build = jreVersion.build().map(String::valueOf).orElse("N/A");
        final String optional = jreVersion.optional().orElse("N/A");
        final String preReleaseInfo = jreVersion.pre().orElse("N/A");

        final Properties properties = System.getProperties();
        final String vendor = properties.getProperty("java.vendor");

        LOG.debug("Logging information about the Java Runtime Environment:");
        LOG.debug("Main Version: " + versionStr);
        LOG.debug("Build number: " + build);
        LOG.debug("Optional: " + optional);
        LOG.debug("Pre Release Info: " + preReleaseInfo);
        LOG.debug("Vendor: " + vendor);
    }
}
