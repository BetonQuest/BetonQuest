package org.betonquest.betonquest.lib.version;

import org.betonquest.betonquest.api.version.Version;
import org.betonquest.betonquest.api.version.VersionToken;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A utility class providing various strategies for comparing versions.
 */
public final class VersionComparisonStrategies {

    /**
     * The default comparison strategy used by {@link DefaultVersion#compareTo(Version)}.
     * <p>
     * Iterates over all tokens in the order provided by the {@link VersionType} and compares them.
     * <p>
     * If all compared tokens are equal for both compared versions,
     * they are only considered equal in the context of this strategy if the actual version strings are equal.
     */
    public static final Comparator<Version> DEFAULT = predicatedStrategy(token -> true, false);

    /**
     * Private constructor to prevent instantiation.
     */
    private VersionComparisonStrategies() {
        super();
    }

    /**
     * Only compare the versions up to the given last token (inclusive).
     * <p>
     * Iterates over the tokens in the order provided by the {@link VersionType}
     * and compares them as long as the last token is compared. No further tokens are compared after that.
     * <p>
     * If all compared tokens are equal for both compared versions,
     * they are considered equal in the context of this strategy.
     * <p>
     * If the given token is optional, it is only compared if both compared versions contain it.
     * And if at least one version does not contain it, the comparison is omitted and may lead to equality earlier.
     *
     * @param lastTokenToCompare the last token to compare
     * @return the comparison strategy that only compares up to the given last token
     */
    public static Comparator<Version> limitedMostSignificantDigit(final String lastTokenToCompare) {
        return predicatedStrategy(new Predicate<>() {
            /**
             * Remembers if the last token to compare has already been tested.
             */
            private boolean state = true;

            @Override
            public boolean test(final VersionToken versionToken) {
                if (versionToken.name().equals(lastTokenToCompare)) {
                    state = false;
                    return true;
                }
                return state;
            }
        }, true);
    }

    /**
     * Only compare the given tokens.
     * <p>
     * Iterates over the tokens in the order provided by the {@link VersionType}
     * and compares them only if they are contained in the given set.
     * <p>
     * If all given tokens are equal for both compared versions,
     * they are considered equal in the context of this strategy.
     * <p>
     * If the given set contains optional tokens, they are only compared if both compared versions contain them.
     * And if at least one version does not contain them,
     * they are omitted from comparison and may lead to equality earlier.
     *
     * @param tokensToCompare the tokens to compare
     * @return the comparison strategy that only compares the given tokens
     */
    public static Comparator<Version> onlyCompare(final Set<String> tokensToCompare) {
        return predicatedStrategy(token -> tokensToCompare.contains(token.name()), true);
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
    private static Comparator<Version> predicatedStrategy(final Predicate<VersionToken> tokenToCompare, final boolean completeIsEqual) {
        return (first, second) -> {
            if (!first.type().equals(second.type())) {
                throw new IllegalArgumentException("Cannot compare versions of different types.");
            }
            final Map<Integer, List<VersionToken>> tokenGroups = tokenGroups(first.type());
            int versionTokenPointer = 0;
            final List<VersionToken> firstTokens = first.tokens();
            final List<VersionToken> secondTokens = second.tokens();
            for (int i = 0; i < tokenGroups.size(); i++) {
                final List<VersionToken> tokenGroup = tokenGroups.get(i);
                if (tokenGroup == null || tokenGroup.isEmpty()) {
                    continue;
                }
                for (final VersionToken token : tokenGroup) {
                    if (!tokenToCompare.test(token)) {
                        versionTokenPointer++;
                        continue;
                    }
                    final boolean firstHas = firstTokens.size() > versionTokenPointer && firstTokens.get(versionTokenPointer).equals(token);
                    final boolean secondHas = secondTokens.size() > versionTokenPointer && secondTokens.get(versionTokenPointer).equals(token);
                    if (firstHas && secondHas) {
                        final int compare = token.tokenComparator().compare(first.elements().get(versionTokenPointer), second.elements().get(versionTokenPointer));
                        if (compare != 0) {
                            return compare;
                        }
                        versionTokenPointer++;
                        continue;
                    }
                    if (firstHas) {
                        return -1;
                    }
                    if (secondHas) {
                        return 1;
                    }
                }
            }
            return completeIsEqual ? 0 : first.toString().compareTo(second.toString());
        };
    }

    private static Map<Integer, List<VersionToken>> tokenGroups(final List<VersionToken> tokens) {
        final Map<Integer, List<VersionToken>> tokenGroups = new LinkedHashMap<>();
        tokens.forEach(token -> tokenGroups.computeIfAbsent(token.group(), k -> new ArrayList<>()).add(token));
        return tokenGroups;
    }
}
