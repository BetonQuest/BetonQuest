package org.betonquest.betonquest.lib.version;

import org.betonquest.betonquest.api.version.VersionToken;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a version type composed of a sequence of {@link VersionToken} instances.
 * A {@code VersionType} defines the format and structure of version strings for parsing,
 * comparison, and validation.
 * <p>
 * Instances of this class are immutable and should be built using the {@code VersionTypeBuilder}.
 *
 * @param tokens the sequence of {@link VersionToken} instances that define the version type
 */
public record VersionType(List<VersionToken> tokens) {

    /**
     * The {@link VersionType} for simple semantic versioning.
     */
    public static final VersionType SIMPLE_SEMANTIC_VERSION = builder()
            .number("major")
            .dot().number("minor")
            .opt()
            .dot().finite().number("patch", 0)
            .opt()
            .dash().finite().exact("snapshot", "SNAPSHOT")
            .build();

    /**
     * Create a new {@link VersionTypeBuilder} instance.
     *
     * @return a new {@link VersionTypeBuilder} instance
     */
    public static VersionTypeBuilder builder() {
        return new VersionTypeBuilder();
    }

    private String parseGroup(final int groupNumber, final List<VersionToken> tokens) {
        String asString = tokens.stream().map(VersionToken::name).collect(Collectors.joining(""));
        if (groupNumber == 0) {
            return asString;
        }
        asString = '{' + asString + '}';
        if (tokens.get(0).optional()) {
            asString = asString + '?';
        }
        return asString;
    }

    @Override
    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
    public String toString() {
        final Map<Integer, List<VersionToken>> tokenGroups = tokenGroups();
        return tokenGroups.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> parseGroup(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(""));
    }

    /**
     * Get the tokens grouped by their group number.
     * <p>
     * The order of the tokens inside each group is preserved.
     *
     * @return a map of token groups, where the key is the group number and the value is a list of tokens in that group
     */
    public Map<Integer, List<VersionToken>> tokenGroups() {
        final Map<Integer, List<VersionToken>> tokenGroups = new LinkedHashMap<>();
        tokens().forEach(token -> tokenGroups.computeIfAbsent(token.group(), k -> new ArrayList<>()).add(token));
        return tokenGroups;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        final VersionType that = (VersionType) other;
        return tokens.equals(that.tokens);
    }

    @Override
    public int hashCode() {
        return tokens.hashCode();
    }
}
