package org.betonquest.betonquest.utils.versioning;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is an abstract representation of a version.
 * A version must contain MAJOR, MINOR and PATCH as integer values, seperated with dots.
 * <p>
 * After that an optional minus is allowed. Behind that, you are free to use any format.
 * A typical example is a qualifier like SNAPSHOT which is followed by the build number.
 * A qualifier or a qualifier with a build number is read in this class and can be compared.
 */
public class Version {
    /**
     * Regex for the qualifier and the build number.
     */
    public static final Pattern BUILD_QUALIFIER_PATTERN = Pattern.compile("^(?<qualifier>.*?)(?<buildnumber>0|[1-9][0-9]*)?$");
    /**
     * This is a help object that splits the raw version into MAJOR, MINOR, PATCH and any remaining parts.
     */
    private final DefaultArtifactVersion version;
    /**
     * The qualifier if existent.
     */
    private final String qualifier;
    /**
     * Null if no build number could be parsed.
     */
    private final Integer buildNumber;

    /**
     * Creates a new Version.
     *
     * @param versionString The raw version string
     */
    public Version(final String versionString) {
        this.version = new DefaultArtifactVersion(versionString);

        String qualifier = null;
        Integer buildNumber = null;
        if (version.getQualifier() != null) {
            final Matcher qualifierMatcher = BUILD_QUALIFIER_PATTERN.matcher(version.getQualifier());
            if (qualifierMatcher.matches()) {
                qualifier = qualifierMatcher.group(1);
                final String buildNumberString = qualifierMatcher.group(2);
                if (buildNumberString != null) {
                    buildNumber = Integer.valueOf(buildNumberString);
                }
            }
        } else if (version.getBuildNumber() != 0 || versionString.endsWith("-0")) {
            buildNumber = version.getBuildNumber();
            qualifier = "";
        }
        this.qualifier = qualifier;
        this.buildNumber = buildNumber;
    }

    /**
     * Checks if the otherVersion is newer than the currentVersion.
     * <p>
     * If a qualifier contains a separator before a build number,
     * you need to add the separator to the qualifier.
     *
     * @param currentVersion The current version
     * @param otherVersion   The other version
     * @param qualifiers     The list of valid qualifiers in prioritized order
     * @return True if the otherVersion is newer then the currentVersion
     */
    public static boolean isNewer(final Version currentVersion, final Version otherVersion,
                                  final UpdateStrategy updateStrategy, final String... qualifiers) {
        final List<String> qualifiersList = Arrays.asList(qualifiers);
        if (otherVersion.hasQualifier() && !qualifiersList.contains(otherVersion.getQualifier())) {
            return false;
        }
        final int currentQualifier = qualifiersList.contains(currentVersion.getQualifier()) ?
                qualifiersList.indexOf(currentVersion.getQualifier()) :
                currentVersion.hasQualifier() ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        final int otherQualifier = qualifiersList.contains(otherVersion.getQualifier()) ?
                qualifiersList.indexOf(otherVersion.getQualifier()) :
                otherVersion.hasQualifier() ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        final int currentBuildNumber = currentVersion.hasBuildNumber() ?
                currentVersion.getBuildNumber() : -1;
        final int otherBuildNumber = otherVersion.hasBuildNumber() ?
                otherVersion.getBuildNumber() : -1;

        final int majorVersion = Integer.compare(currentVersion.getMajorVersion(), otherVersion.getMajorVersion());
        final int minorVersion = Integer.compare(currentVersion.getMinorVersion(), otherVersion.getMinorVersion());
        final int patchVersion = Integer.compare(currentVersion.getPatchVersion(), otherVersion.getPatchVersion());
        final int qualifierVersion = Integer.compare(currentQualifier, otherQualifier);
        final int buildNumberVersion = Integer.compare(currentBuildNumber, otherBuildNumber);
        return isNewerCheckQualifiers(updateStrategy, majorVersion, minorVersion, patchVersion, qualifierVersion, buildNumberVersion);
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private static boolean isNewerCheckQualifiers(final UpdateStrategy updateStrategy,
                                                  final int majorVersion, final int minorVersion, final int patchVersion,
                                                  final int qualifierVersion, final int buildNumberVersion) {
        switch (updateStrategy) {
            case MAJOR:
                if (majorVersion > 0) {
                    return false;
                } else if (majorVersion < 0) {
                    return true;
                }
            case MINOR:
                if (majorVersion == 0) {
                    if (minorVersion > 0) {
                        return false;
                    } else if (minorVersion < 0) {
                        return true;
                    }
                }
            case PATCH:
                if (majorVersion == 0 && minorVersion == 0) {
                    if (patchVersion > 0) {
                        return false;
                    } else if (patchVersion < 0) {
                        return true;
                    }
                    return isNewerQualifier(qualifierVersion, buildNumberVersion);
                }
            default:
                return false;
        }
    }

    private static boolean isNewerQualifier(final int qualifierVersion, final int buildNumberVersion) {
        if (qualifierVersion < 0) {
            return false;
        } else if (qualifierVersion > 0) {
            return true;
        } else {
            return buildNumberVersion < 0;
        }
    }

    /**
     * Get the version string.
     *
     * @return The string of this version.
     */
    public String getVersion() {
        return version.toString();
    }

    public int getMajorVersion() {
        return version.getMajorVersion();
    }

    public int getMinorVersion() {
        return version.getMinorVersion();
    }

    public int getPatchVersion() {
        return version.getIncrementalVersion();
    }

    public String getQualifier() {
        return qualifier;
    }

    public Integer getBuildNumber() {
        return buildNumber;
    }

    public boolean hasQualifier() {
        return qualifier != null;
    }

    public boolean hasBuildNumber() {
        return buildNumber != null;
    }
}
