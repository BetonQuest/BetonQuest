package org.betonquest.betonquest.versioning;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Compares two {@link Version}s. This comparator can also be used to sort lists of {@link Version}s.
 */
public class VersionComparator implements Comparator<Version>, Serializable {
    @Serial
    private static final long serialVersionUID = 1641779671214600158L;

    /**
     * The chosen {@link UpdateStrategy}
     */
    private final UpdateStrategy updateStrategy;

    /**
     * List of qualifiers in prioritized order
     */
    private final List<String> qualifiers;

    /**
     * The provided {@link UpdateStrategy} determines which digits of the version are allowed to change.
     * <p>
     * A list of allowed qualifiers can be provided. If not, no qualifiers are allowed.
     * An empty String represent a version without a qualifier but with a build number.
     * <p>
     * If a qualifier contains a separator before a build number,
     * you need to add the separator to the qualifier.
     *
     * @param updateStrategy The chosen update strategy
     * @param qualifiers     The list of valid qualifiers in prioritized order (first entry has the highest priority)
     */
    public VersionComparator(final UpdateStrategy updateStrategy, final String... qualifiers) {
        this.updateStrategy = updateStrategy;
        this.qualifiers = Lists.reverse(Arrays.asList(qualifiers));
    }

    /**
     * Gets the set {@link UpdateStrategy} for the {@link VersionComparator}.
     *
     * @return the {@link UpdateStrategy}
     */
    public UpdateStrategy getUpdateStrategy() {
        return updateStrategy;
    }

    /**
     * @param current The current version
     * @param other   The other version
     * @return true if the other version is newer than current
     */
    public boolean isOtherNewerThanCurrent(final Version current, final Version other) {
        return compare(current, other) < 0;
    }

    /**
     * @param current The current version
     * @param other   The other version
     * @return true if the other version is newer or equal than current
     */
    public boolean isOtherNewerOrEqualThanCurrent(final Version current, final Version other) {
        return compare(current, other) <= 0;
    }

    /**
     * Checks if the otherVersion is newer than the currentVersion.
     *
     * @param current The current version
     * @param other   The other version
     * @return 0 if equal; less than 0 if other is newer; more than 0 if current is newer
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    @Override
    public int compare(@Nullable final Version current, @Nullable final Version other) {
        if (current == null && other == null) {
            return 0;
        }
        if (current == null) {
            return -1;
        }
        if (other == null) {
            return 1;
        }
        if (current.getVersion().equals(other.getVersion())) {
            return 0;
        }
        if (current.hasQualifier() && !qualifiers.contains(current.getQualifier())
                && other.hasQualifier() && !qualifiers.contains(other.getQualifier())) {
            return 0;
        }
        if (other.hasQualifier() && !qualifiers.contains(other.getQualifier())) {
            return 1;
        }

        return compareVersions(current, other);
    }

    private int compareVersions(final Version current, final Version other) {
        final int currentQualifier = current.hasQualifier() ? qualifiers.contains(current.getQualifier())
                ? qualifiers.indexOf(current.getQualifier()) : Integer.MIN_VALUE : Integer.MAX_VALUE;
        final int otherQualifier = other.hasQualifier() ? qualifiers.contains(other.getQualifier())
                ? qualifiers.indexOf(other.getQualifier()) : Integer.MIN_VALUE : Integer.MAX_VALUE;
        final int currentBuildNumber = current.hasBuildNumber() ? current.getBuildNumber() : Integer.MAX_VALUE;
        final int otherBuildNumber = other.hasBuildNumber() ? other.getBuildNumber() : Integer.MAX_VALUE;

        final int majorVersion = Integer.compare(current.getMajorVersion(), other.getMajorVersion());
        final int minorVersion = Integer.compare(current.getMinorVersion(), other.getMinorVersion());
        final int patchVersion = Integer.compare(current.getPatchVersion(), other.getPatchVersion());
        final int qualifierVersion = Integer.compare(currentQualifier, otherQualifier);
        final int buildNumberVersion = Integer.compare(currentBuildNumber, otherBuildNumber);
        return compareVersionDigits(majorVersion, minorVersion, patchVersion, qualifierVersion, buildNumberVersion);
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ImplicitSwitchFallThrough"})
    private int compareVersionDigits(final int majorVersion, final int minorVersion, final int patchVersion,
                                     final int qualifierVersion, final int buildNumberVersion) {
        switch (updateStrategy) {
            case MAJOR:
                if (majorVersion != 0) {
                    return majorVersion;
                }
            case MINOR:
                if (majorVersion == 0 && minorVersion != 0) {
                    return minorVersion;
                }
            case PATCH:
                if (majorVersion == 0 && minorVersion == 0) {
                    if (patchVersion != 0) {
                        return patchVersion;
                    }
                    return compareVersionDigitQualifierAndBuildNumber(qualifierVersion, buildNumberVersion);
                }
            default:
                return 1;
        }
    }

    private int compareVersionDigitQualifierAndBuildNumber(final int qualifierVersion, final int buildNumberVersion) {
        if (qualifierVersion != 0) {
            return qualifierVersion;
        }
        return buildNumberVersion;
    }
}
