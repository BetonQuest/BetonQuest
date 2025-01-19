package org.betonquest.betonquest.versioning;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is an abstract representation of a version.
 * A version must contain MAJOR, MINOR and PATCH as integer values, seperated with dots.
 * <p>
 * After that an optional dash is allowed. Behind that, you are free to use any format.
 * A typical example is a qualifier like SNAPSHOT which is followed by the build number.
 * A qualifier or a qualifier with a build number is read in this class and can be compared.
 */
@SuppressWarnings("NullAway")
public class Version {
    /**
     * Regex for the qualifier and the build number.
     */
    public static final Pattern BUILD_QUALIFIER_PATTERN = Pattern.compile("^(?<qualifier>.*?)(?<buildnumber>0|[1-9]\\d*)?$");

    /**
     * This is a help object that splits the raw version into MAJOR, MINOR, PATCH and any remaining parts.
     */
    private final DefaultArtifactVersion artifactVersion;

    /**
     * The qualifier if existent.
     */
    @Nullable
    private final String qualifier;

    /**
     * Null if no build number could be parsed.
     */
    @Nullable
    private final Integer buildNumber;

    /**
     * Creates a new Version.
     *
     * @param versionString The raw version string
     */
    public Version(final String versionString) {
        this.artifactVersion = new DefaultArtifactVersion(versionString);

        String qualifier = null;
        Integer buildNumber = null;
        if (artifactVersion.getQualifier() != null) {
            final Matcher qualifierMatcher = BUILD_QUALIFIER_PATTERN.matcher(artifactVersion.getQualifier());
            if (qualifierMatcher.matches()) {
                qualifier = qualifierMatcher.group(1);
                final String buildNumberString = qualifierMatcher.group(2);
                if (buildNumberString != null) {
                    buildNumber = Integer.valueOf(buildNumberString);
                }
            }
        } else if (artifactVersion.getBuildNumber() != 0 || versionString.endsWith("-0")) {
            buildNumber = artifactVersion.getBuildNumber();
            qualifier = "";
        }
        this.qualifier = qualifier;
        this.buildNumber = buildNumber;
    }

    /**
     * Get the version string.
     *
     * @return The string of this version.
     */
    public String getVersion() {
        return artifactVersion.toString();
    }

    /**
     * Get the major version digit.
     *
     * @return The major digit
     */
    public int getMajorVersion() {
        return artifactVersion.getMajorVersion();
    }

    /**
     * Get the minor version digit.
     *
     * @return The minor digit
     */
    public int getMinorVersion() {
        return artifactVersion.getMinorVersion();
    }

    /**
     * Get the patch version digit.
     *
     * @return The patch digit
     */
    public int getPatchVersion() {
        return artifactVersion.getIncrementalVersion();
    }

    /**
     * Get the qualifier.
     *
     * @return The qualifier
     */
    @Nullable
    public String getQualifier() {
        return qualifier;
    }

    /**
     * Get the build number.
     *
     * @return The build number
     */
    @Nullable
    public Integer getBuildNumber() {
        return buildNumber;
    }

    /**
     * Check if the version has a qualifier.
     *
     * @return True if qualifier exist
     */
    public boolean hasQualifier() {
        return qualifier != null;
    }

    /**
     * Check if the version has a build number.
     *
     * @return True if build number exist
     */
    public boolean hasBuildNumber() {
        return buildNumber != null;
    }

    @Override
    public boolean equals(@Nullable final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        final Version version = (Version) other;
        return artifactVersion.equals(version.artifactVersion) && Objects.equals(qualifier, version.qualifier) && Objects.equals(buildNumber, version.buildNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artifactVersion, qualifier, buildNumber);
    }
}
