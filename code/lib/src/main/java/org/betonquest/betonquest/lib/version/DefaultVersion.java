package org.betonquest.betonquest.lib.version;

import org.betonquest.betonquest.api.version.Version;
import org.betonquest.betonquest.api.version.VersionToken;
import org.betonquest.betonquest.api.version.VersionType;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Represents a version, composed of a series of parsed tokens and categorized by a specific {@link VersionType}.
 * A {@code Version} allows parsing, comparison, and representation of version strings based on predefined formats.
 * Instances of this class are immutable and are created through parsing of version strings.
 * <p>
 * This class implements {@link Comparable} to enable comparisons between versions using
 * the {@link VersionComparisonStrategies#DEFAULT} strategy.
 */
public class DefaultVersion implements Version {

    /**
     * The {@link VersionType} of this version.
     */
    private final VersionType versionType;

    /**
     * The parsed tokens of this version.
     */
    private final List<String> parsedElements;

    /**
     * The parsed tokens of this version.
     */
    private final List<VersionToken> parsedTokens;

    /**
     * Create a new {@link DefaultVersion} with the given {@link VersionType} and parsed tokens.
     *
     * @param versionType    the {@link VersionType} of this version
     * @param parsedElements the parsed token contents
     * @param parsedTokens   the parsed token types
     */
    protected DefaultVersion(final VersionType versionType, final List<String> parsedElements, final List<VersionToken> parsedTokens) {
        this.versionType = versionType;
        this.parsedElements = Collections.unmodifiableList(parsedElements);
        this.parsedTokens = Collections.unmodifiableList(parsedTokens);
    }

    @Override
    public Optional<String> getNamedElement(final String elementName) {
        for (int i = 0; i < parsedTokens.size(); i++) {
            final VersionToken token = parsedTokens.get(i);
            if (token.name().equalsIgnoreCase(elementName)) {
                return Optional.of(parsedElements.get(i));
            }
        }
        return Optional.empty();
    }

    @Override
    public int compareTo(final Version other) {
        return compareTo(VersionComparisonStrategies.DEFAULT, other);
    }

    @Override
    public int compareTo(final Comparator<Version> comparisonStrategy, final Version other) {
        return comparisonStrategy.compare(this, other);
    }

    @Override
    public boolean isNewerThan(final Comparator<Version> comparisonStrategy, final Version other) {
        return comparisonStrategy.compare(this, other) > 0;
    }

    @Override
    public boolean isCompatibleWith(final Comparator<Version> comparisonStrategy, final Version other) {
        return comparisonStrategy.compare(this, other) >= 0;
    }

    @Override
    public boolean isOlderThan(final Comparator<Version> comparisonStrategy, final Version other) {
        return comparisonStrategy.compare(this, other) < 0;
    }

    @Override
    public VersionType type() {
        return versionType;
    }

    @Override
    public List<String> elements() {
        return parsedElements;
    }

    @Override
    public List<VersionToken> tokens() {
        return parsedTokens;
    }

    @Override
    public String toString() {
        final int last = parsedTokens.size() - 1;
        for (int i = last; i >= 0; i--) {
            final String absenceDefault = parsedTokens.get(i).absenceDefault();
            if (!parsedElements.get(i).equals(absenceDefault)) {
                return String.join("", parsedElements.subList(0, i + 1));
            }
        }
        return String.join("", parsedElements);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        final DefaultVersion version = (DefaultVersion) other;
        return versionType.equals(version.versionType) && parsedElements.equals(version.parsedElements);
    }

    @Override
    public int hashCode() {
        int result = versionType.hashCode();
        result = 31 * result + parsedElements.hashCode();
        return result;
    }
}
