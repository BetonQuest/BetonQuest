package org.betonquest.betonquest.api.version;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Interface representing a version object that adheres to a specific version type.
 * A version is typically a structured string composed of multiple elements and tokens
 * which can be parsed, compared, and validated based on the defined version type.
 */
public interface Version extends Comparable<Version> {

    /**
     * Returns the version type defining the structure of this version.
     * <p>
     * The version type is represented as an ordered sequence of {@link VersionToken}s
     * that define the expected format and components of the version string.
     *
     * @return an unmodifiable list of {@link VersionToken}s defining the version type
     */
    List<VersionToken> type();

    /**
     * Returns the tokens actually parsed from this version string.
     * <p>
     * This list contains only the tokens that were present in the parsed version string,
     * making it a subset of the tokens returned by {@link #type()}. The tokens in this
     * list correspond one-to-one with the elements returned by {@link #elements()},
     * matched by their index positions.
     * <p>
     * For example, if a version type allows optional tokens and the version string
     * "1.0" is parsed, this method returns only the tokens for major and minor versions,
     * even if the type defines additional optional tokens like patch or build.
     * Separator tokens (e.g., ".") are also included in the list.
     *
     * @return an unmodifiable list of {@link VersionToken}s that were successfully parsed from the version string
     */
    List<VersionToken> tokens();

    /**
     * Returns the string values of the parsed version components.
     * <p>
     * Each element in this list represents the actual string value extracted from
     * the version string for the corresponding token. The elements correspond one-to-one
     * with the tokens returned by {@link #tokens()}, matched by their index positions.
     * <p>
     * For example, parsing the version string "1.2.3" might return a list containing
     * {@code ["1", ".", "2", ".", "3"]}, where each element corresponds to major, minor, and
     * patch tokens respectively. Separator tokens (e.g., ".") are also included in the list.
     *
     * @return an unmodifiable list of string values representing the parsed version components
     */
    List<String> elements();

    /**
     * Retrieves the value of a version component by its token name.
     * <p>
     * This method searches for a token with the specified name and returns its
     * corresponding element value if found. Token names are defined by the
     * {@link VersionToken#name()} method.
     * <p>
     * For example, if a version "1.2.3" has tokens named "major", "minor", and "patch",
     * calling {@code getNamedElement("minor")} would return {@code Optional.of("2")}.
     * <p>
     * If there are multiple tokens with the same name, the first one found is returned.
     *
     * @param elementName the name of the token whose value should be retrieved
     * @return an {@link Optional} containing the element value if a token with the
     * given name exists in this version, or an empty {@link Optional} if no
     * such token is found
     */
    Optional<String> getNamedElement(String elementName);

    /**
     * Compare this version to another version using the given comparison strategy.
     *
     * @param comparisonStrategy the comparison strategy to use
     * @param other              the other version to compare to
     * @return a negative integer, zero, or a positive integer as this version is
     * less than, equal to, or greater than the specified version.
     */
    int compareTo(Comparator<Version> comparisonStrategy, Version other);

    /**
     * Checks if this version is newer than the specified version using the given comparison strategy.
     * <p>
     * A version is considered newer if the comparison result is positive, according to the provided strategy.
     *
     * @param comparisonStrategy the comparison strategy to use for determining version order
     * @param other              the other version to compare against
     * @return {@code true} if this version is newer than the specified version, {@code false} otherwise
     */
    boolean isNewerThan(Comparator<Version> comparisonStrategy, Version other);

    /**
     * Checks if this version is compatible with the specified version using the given comparison strategy.
     * <p>
     * A version is considered compatible if it is equal to or greater than the specified version,
     * according to the provided strategy.
     * <p>
     * For example, version "1.2.3" and "1.0.0" may be compatible with "1.0.0" when using a standard semantic version comparator,
     * but version "0.9.5" is not compatible with "1.0.0" in the same way.
     *
     * @param comparisonStrategy the comparison strategy to use for determining version compatibility
     * @param other              the other version to check compatibility against
     * @return {@code true} if this version is compatible with the specified version, {@code false} otherwise
     */
    boolean isCompatibleWith(Comparator<Version> comparisonStrategy, Version other);

    /**
     * Checks if this version is older than the specified version using the given comparison strategy.
     * <p>
     * A version is considered older if the comparison result is negative, according to the provided strategy.
     *
     * @param comparisonStrategy the comparison strategy to use for determining version order
     * @param other              the other version to compare against
     * @return {@code true} if this version is older than the specified version, {@code false} otherwise
     */
    boolean isOlderThan(Comparator<Version> comparisonStrategy, Version other);
}
