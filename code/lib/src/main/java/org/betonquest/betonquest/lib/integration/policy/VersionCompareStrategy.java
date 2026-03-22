package org.betonquest.betonquest.lib.integration.policy;

import org.betonquest.betonquest.lib.versioning.UpdateStrategy;
import org.betonquest.betonquest.lib.versioning.Version;
import org.betonquest.betonquest.lib.versioning.VersionComparator;

import java.util.function.BiPredicate;

/**
 * Represents a strategy for comparing two versions based on specific criteria.
 * Each strategy determines compatibility between two given versions.
 * This enum implements the {@link BiPredicate} interface with two {@link Version}s.
 */
public enum VersionCompareStrategy implements BiPredicate<Version, Version> {

    /**
     * A version comparison strategy that considers a minimal compatibility requirement.
     * This strategy checks whether the given {@code actual} version is compatible
     * with the {@code required} version using the {@link UpdateStrategy#MAJOR}.
     * <p>
     * It does the opposite of {@link #MAXIMAL}.
     */
    MINIMAL {
        @Override
        public boolean test(final Version actual, final Version required) {
            return new VersionComparator(UpdateStrategy.MAJOR).isCompatibleWith(actual, required);
        }
    },
    /**
     * A version comparison strategy that checks for an exact match between two versions.
     * This strategy considers two versions as compatible only if all their parsed components are strictly equal.
     *
     * @see Version#equals(Object)
     */
    EXACT {
        @Override
        public boolean test(final Version actual, final Version required) {
            return actual.equals(required);
        }
    },
    /**
     * A version comparison strategy that considers a minimal compatibility requirement.
     * This strategy checks whether the given {@code requird} version is compatible
     * with the {@code actual} version using the {@link UpdateStrategy#MAJOR}.
     * <p>
     * It does the opposite of {@link #MINIMAL}.
     */
    MAXIMAL {
        @Override
        public boolean test(final Version actual, final Version required) {
            return new VersionComparator(UpdateStrategy.MAJOR).isCompatibleWith(required, actual);
        }
    }
}
