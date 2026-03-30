package org.betonquest.betonquest.lib.version;

import org.betonquest.betonquest.api.version.VersionToken;
import org.betonquest.betonquest.api.version.VersionType;

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
public record DefaultVersionType(List<VersionToken> tokens) implements VersionType {

    /**
     * The {@link DefaultVersionType} for simple semantic versioning.
     */
    public static final DefaultVersionType SIMPLE_SEMANTIC_VERSION = builder()
            .number("major")
            .dot().number("minor")
            .opt()
            .dot().finite().number("patch", 0)
            .opt()
            .finite().any("remainder")
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
        final String collectedTokens = tokens.stream().map(VersionToken::name).collect(Collectors.joining(""));
        if (groupNumber == 0) {
            return collectedTokens;
        }
        final StringBuilder builder = new StringBuilder(collectedTokens);
        builder.insert(0, '{').append('}');
        if (tokens.get(0).optional()) {
            builder.append('?');
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        final Map<Integer, List<VersionToken>> tokenGroups = tokenGroups();
        return tokenGroups.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> parseGroup(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(""));
    }

    @Override
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
        final DefaultVersionType that = (DefaultVersionType) other;
        return tokens.equals(that.tokens);
    }

    @Override
    public int hashCode() {
        return tokens.hashCode();
    }
}
