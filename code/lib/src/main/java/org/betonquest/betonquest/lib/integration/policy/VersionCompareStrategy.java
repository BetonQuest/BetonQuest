package org.betonquest.betonquest.lib.integration.policy;

import org.betonquest.betonquest.api.version.Version;
import org.betonquest.betonquest.lib.version.BetonQuestUpdateStrategy;
import org.betonquest.betonquest.lib.version.VersionComparisonStrategies;

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
     * with the {@code required} version using the {@link BetonQuestUpdateStrategy#MAJOR}.
     * <p>
     * It does the opposite of {@link #MAXIMAL}.
     */
    MINIMAL(">=") {
        @Override
        public boolean test(final Version actual, final Version required) {
            return actual.isCompatibleWith(VersionComparisonStrategies.DEFAULT, required);
        }
    },
    /**
     * A version comparison strategy that checks for an exact match between two versions.
     * This strategy considers two versions as compatible only if all their parsed components are strictly equal.
     */
    EXACT("=") {
        @Override
        public boolean test(final Version actual, final Version required) {
            return actual.equals(required);
        }
    },
    /**
     * A version comparison strategy that considers a minimal compatibility requirement.
     * This strategy checks whether the given {@code requird} version is compatible
     * with the {@code actual} version using the {@link BetonQuestUpdateStrategy#MAJOR}.
     * <p>
     * It does the opposite of {@link #MINIMAL}.
     */
    MAXIMAL("<=") {
        @Override
        public boolean test(final Version actual, final Version required) {
            return required.isCompatibleWith(VersionComparisonStrategies.DEFAULT, actual);
        }
    };

    /**
     * The readable short form of the strategy.
     */
    private final String representation;

    VersionCompareStrategy(final String representation) {
        this.representation = representation;
    }

    /**
     * Gets the readable short form of the strategy.
     *
     * @return short format string
     */
    public String getRepresentation() {
        return representation;
    }
}
