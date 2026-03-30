package org.betonquest.betonquest.lib.version;

import org.betonquest.betonquest.api.version.Version;
import org.betonquest.betonquest.api.version.VersionToken;
import org.betonquest.betonquest.api.version.VersionType;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A utility class for parsing version strings into {@link Version} objects using a specified {@link VersionType}.
 * This class provides methods to break down and validate version strings based on patterns
 * and tokenizing rules associated with the defined {@link VersionType}.
 * It ensures conformity with expected formats and provides error handling for invalid or incompatible version strings.
 */
public final class VersionParser {

    /**
     * Private constructor to prevent instantiation.
     */
    private VersionParser() {

    }

    /**
     * Parses a version string into a {@link Version} object using the specified {@link VersionType}.
     * <p>
     * This method tokenizes the input version string and validates it against the format defined
     * by the provided {@link VersionType}. The version string must conform to the expected pattern
     * of tokens, including required and optional components. If parsing succeeds, a new
     * {@link Version} object is returned containing the parsed tokens and their corresponding values.
     * <p>
     * For example, parsing "1.2.3" with a semantic version type would extract major, minor, and patch
     * components along with their separator tokens.
     *
     * @param type          the {@link VersionType} defining the expected version format and token structure
     * @param versionString the version string to parse; must not be null and should conform to the format
     *                      defined by the {@link VersionType}
     * @return a new {@link Version} object containing the parsed tokens and their values
     * @throws IllegalArgumentException if the version string does not match the expected format defined by
     *                                  the {@link VersionType}, is too lengthy, or ends on an illegal character
     */
    @Contract(pure = true, value = "_, _ -> new")
    public static Version parse(final VersionType type, final String versionString) {
        final VersionTokenizer versionTokenizer = new VersionTokenizer(versionString);
        final List<String> parsedContent = new ArrayList<>(type.tokens().size());
        final List<VersionToken> parsedTokens = new ArrayList<>(type.tokens().size());
        final Map<Integer, List<VersionToken>> tokenGroups = type.tokenGroups();
        boolean isLastTokenFinite = false;

        for (final List<VersionToken> tokenGroup : tokenGroups.values()) {
            final boolean wasCompleted = parseTokenGroup(type, versionString, tokenGroup, versionTokenizer, parsedContent, parsedTokens);
            if (!wasCompleted) {
                continue;
            }
            isLastTokenFinite = tokenGroup.get(tokenGroup.size() - 1).finite();
        }

        if (!isLastTokenFinite) {
            throw new IllegalArgumentException("Version-string '%s' does not match version-type '%s'. Input ends on an illegal character, remaining tokens: '%s', parsed content: '%s'"
                    .formatted(versionString, type, versionString.substring(versionTokenizer.getCurrentPointer()), String.join("", parsedContent)));
        }

        if (versionTokenizer.canConsume()) {
            throw new IllegalArgumentException("Version-string '%s' does not match version-type '%s', its too lengthy.".formatted(versionString, type));
        }

        return new DefaultVersion(type, parsedContent, parsedTokens);
    }

    /**
     * Parses a single token group from the version string.
     * <p>
     * A token group represents a set of consecutive tokens that must be parsed together as a unit.
     * If the group is optional and parsing fails, the method may restore the tokenizer state and
     * return false. For required groups, parsing failures result in an exception.
     * <p>
     * This method attempts to consume tokens from the version string that match the patterns
     * defined in the token group. If all tokens in the group are successfully matched, they are
     * added to the provided lists. If a token cannot be matched and has an absence default value,
     * that default is used instead.
     *
     * @param type             the {@link VersionType} defining the version format being parsed
     * @param versionString    the complete version string being parsed (used for error messages)
     * @param tokenGroup       the list of {@link VersionToken}s that form this token group and must be parsed together
     * @param versionTokenizer the {@link VersionTokenizer} used to consume tokens from the version string
     * @param parsedContent    the list to append successfully parsed content strings to
     * @param parsedTokens     the list to append successfully parsed {@link VersionToken}s to
     * @return {@code true} if the token group was successfully parsed, and all tokens were consumed or defaulted;
     * {@code false} if the group is optional and parsing failed, causing the tokenizer state to be reset
     * @throws IllegalArgumentException if a required token group cannot be parsed
     */
    private static boolean parseTokenGroup(final VersionType type, final String versionString,
                                           final List<VersionToken> tokenGroup, final VersionTokenizer versionTokenizer,
                                           final List<String> parsedContent, final List<VersionToken> parsedTokens) {
        final int resetPosition = versionTokenizer.getCurrentPointer();
        final boolean optional = tokenGroup.get(0).optional();
        final List<String> groupContents = new ArrayList<>(tokenGroup.size());
        final List<VersionToken> groupTokens = new ArrayList<>(tokenGroup.size());

        boolean resetPointer = false;

        for (final VersionToken token : tokenGroup) {
            if (versionTokenizer.has(token.pattern())) {
                groupContents.add(versionTokenizer.consume(token.pattern()));
                groupTokens.add(token);
                continue;
            }
            if (!optional) {
                throw new IllegalArgumentException("Version-string '%s' does not match version-type '%s'. Input ends on an illegal character. current group: '%s'"
                        .formatted(versionString, type, String.join("", parsedContent)));
            }
            if (token.absenceDefault() == null) {
                versionTokenizer.resetPointer(resetPosition);
                return false;
            }
            groupContents.add(token.absenceDefault());
            groupTokens.add(token);
            resetPointer = true;
        }

        parsedContent.addAll(groupContents);
        parsedTokens.addAll(groupTokens);
        if (resetPointer) {
            versionTokenizer.resetPointer(resetPosition);
        }
        return true;
    }
}
