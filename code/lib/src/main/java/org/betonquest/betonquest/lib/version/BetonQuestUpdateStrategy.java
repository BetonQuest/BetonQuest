package org.betonquest.betonquest.lib.version;

import org.betonquest.betonquest.api.version.Version;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

/**
 * Represent different strategies to select which versions are valid to update to.
 * These strategies should fit for versions that fulfil semantic versioning.
 */
public enum BetonQuestUpdateStrategy {
    /**
     * The first number of a semantic version.
     */
    MAJOR(""),
    /**
     * The second number of a semantic version.
     */
    MINOR("major"),
    /**
     * The third number of a semantic version.
     */
    PATCH("major", "minor");

    /**
     * The comparator elements for this strategy.
     */
    private final Set<String> comparatorElements;

    /**
     * Create a new strategy.
     *
     * @param comparatorElements the comparator elements to use
     */
    BetonQuestUpdateStrategy(final String... comparatorElements) {
        this.comparatorElements = Set.of(comparatorElements);
    }

    /**
     * Get the comparator for this strategy.
     *
     * @param excludeDevVersions whether to exclude dev versions from the comparison
     * @return the comparator for this strategy
     */
    public Comparator<Version> getComparator(final boolean excludeDevVersions) {
        final Comparator<Version> required = VersionComparisonStrategies.exclude(comparatorElements, true);
        final Comparator<Version> minimal = VersionComparisonStrategies.onlyCompare(comparatorElements, true);
        return (version, other) -> {
            final Optional<String> versionType = version.getNamedElement("type");
            final Optional<String> otherType = other.getNamedElement("type");
            final boolean isReleaseOther = otherType.map("RELEASE"::equals).orElse(false);
            if (otherType.map("DEV-ARTIFACT"::equals).orElse(false) && !versionType.map("DEV-ARTIFACT"::equals).orElse(false)
                    || otherType.map("DEV-UNOFFICIAL"::equals).orElse(false)) {
                return 1;
            }
            int requiredResult = required.compare(version, other);
            final int minimalResult = minimal.compare(version, other);
            if (excludeDevVersions && !isReleaseOther) {
                requiredResult = 1;
            }
            return minimalResult == 0 ? requiredResult : 0;
        };
    }
}
