package org.betonquest.betonquest.lib.version;

import org.betonquest.betonquest.api.version.VersionToken;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A builder class for constructing {@link DefaultVersionType} instances.
 * This builder supports adding various token types such as numbers,
 * strings, or specific symbols to define the structure of a version string.
 * Tokens can be marked as optional and are added in sequence.
 * <p>
 * The builder facilitates the creation of complex version types
 * by allowing fine-grained specification of the tokens and their
 * characteristics, including patterns, comparators, and optionality.
 * <p>
 * The {@link VersionTypeBuilder} is designed for incremental token addition;
 * once all desired tokens are added, the {@code build()} method can be called
 * to create an instance of {@link DefaultVersionType}.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class VersionTypeBuilder {

    /**
     * The pattern used to match dot tokens.
     */
    private static final Pattern PATTERN_DOT = Pattern.compile("^\\.");

    /**
     * The pattern used to match dash tokens.
     */
    private static final Pattern PATTERN_DASH = Pattern.compile("^-");

    /**
     * The pattern used to match plus tokens.
     */
    private static final Pattern PATTERN_PLUS = Pattern.compile("^\\+");

    /**
     * The pattern used to match number tokens.
     */
    private static final Pattern PATTERN_NUMBER = Pattern.compile("^[0-9]+");

    /**
     * The pattern used to match any string tokens.
     */
    private static final Pattern PATTERN_ANY = Pattern.compile("^.+");

    /**
     * The list of tokens that are added to the version type.
     */
    private final List<VersionToken> tokens = new ArrayList<>();

    /**
     * The current group of the next token.
     */
    private int currentGroup;

    /**
     * If the next token is optionally ejectable.
     */
    private boolean optionalFlag;

    /**
     * If the next token is finite.
     * Parsing a valid version string may only succeed if the last parsed token is finite.
     */
    private boolean finiteFlag;

    /**
     * Create a new VersionTypeBuilder.
     */
    public VersionTypeBuilder() {
        optionalFlag = false;
        finiteFlag = false;
        currentGroup = 0;
    }

    /**
     * Add a number token with a name matching any number like {@code [0-9]+}.
     *
     * @param name the name of the token
     * @return itself
     */
    @Contract(mutates = "this", value = "_ -> this")
    public VersionTypeBuilder number(final String name) {
        return token(name, PATTERN_NUMBER, Comparator.comparing(Integer::parseInt));
    }

    /**
     * Add a number token with a name matching any number like {@code [0-9]+} and an absence default value.
     *
     * @param name           the name of the token
     * @param absenceDefault the default value to return if the token is absent
     * @return itself
     */
    @Contract(mutates = "this", value = "_, _ -> this")
    public VersionTypeBuilder number(final String name, final int absenceDefault) {
        return token(name, PATTERN_NUMBER, Comparator.comparing(Integer::parseInt), String.valueOf(absenceDefault));
    }

    /**
     * Add a token with a name matching any string (except newlines).
     *
     * @param name the name of the token
     * @return itself
     */
    @Contract(mutates = "this", value = "_ -> this")
    public VersionTypeBuilder any(final String name) {
        return token(name, PATTERN_ANY, Comparator.naturalOrder());
    }

    /**
     * Add a token with a name matching any string (except newlines) and an absence default value.
     *
     * @param name           the name of the token
     * @param absenceDefault the default value to return if the token is absent
     * @return itself
     */
    @Contract(mutates = "this", value = "_, _ -> this")
    public VersionTypeBuilder any(final String name, final String absenceDefault) {
        return token(name, PATTERN_ANY, Comparator.naturalOrder(), absenceDefault);
    }

    /**
     * Add an exact token with a name and a regex to match.
     *
     * @param name  the name of the token
     * @param token the string token
     * @return itself
     */
    @Contract(mutates = "this", value = "_, _ -> this")
    public VersionTypeBuilder exact(final String name, final String token) {
        return exact(name, token, Comparator.naturalOrder());
    }

    /**
     * Add an exact token with a name, a regex to match, and an absence default value.
     *
     * @param name           the name of the token
     * @param token          the string token
     * @param absenceDefault the default value to return if the token is absent
     * @return itself
     */
    @Contract(mutates = "this", value = "_, _, _ -> this")
    public VersionTypeBuilder exact(final String name, final String token, final String absenceDefault) {
        return exact(name, token, Comparator.naturalOrder(), absenceDefault);
    }

    /**
     * Add an exact token with a name, a regex to match, and a comparator for the token.
     *
     * @param name       the name of the token
     * @param token      the string token
     * @param comparator the comparator for the token
     * @return itself
     */
    @Contract(mutates = "this", value = "_, _, _ -> this")
    public VersionTypeBuilder exact(final String name, final String token, final Comparator<String> comparator) {
        return token(name, Pattern.compile("^(%s)".formatted(token)), comparator);
    }

    /**
     * Add an exact token with a name, a regex to match, a comparator for the token, and an absence default value.
     *
     * @param name           the name of the token
     * @param token          the string token
     * @param comparator     the comparator for the token
     * @param absenceDefault the default value to return if the token is absent
     * @return itself
     */
    @Contract(mutates = "this", value = "_, _, _, _ -> this")
    public VersionTypeBuilder exact(final String name, final String token, final Comparator<String> comparator, final String absenceDefault) {
        return token(name, Pattern.compile("^(%s)".formatted(token)), comparator, absenceDefault);
    }

    /**
     * Add a dot token matching the symbol {@code .}.
     *
     * @return itself
     */
    @Contract(mutates = "this", value = "-> this")
    public VersionTypeBuilder dot() {
        return token(".", PATTERN_DOT, identityComparator(), ".");
    }

    /**
     * Add a dash token matching the symbol {@code -}.
     *
     * @return itself
     */
    @Contract(mutates = "this", value = "-> this")
    public VersionTypeBuilder dash() {
        return token("-", PATTERN_DASH, identityComparator(), "-");
    }

    /**
     * Add a plus token matching the symbol {@code +}.
     *
     * @return itself
     */
    @Contract(mutates = "this", value = "-> this")
    public VersionTypeBuilder plus() {
        return token("+", PATTERN_PLUS, identityComparator(), "+");
    }

    /**
     * Mark the next token as optionally ejectable.
     *
     * @return itself
     */
    @Contract(mutates = "this", value = "-> this")
    public VersionTypeBuilder opt() {
        if (tokens.isEmpty()) {
            throw new IllegalStateException("Cannot mark the first token as optional.");
        }
        if (optionalFlag) {
            throw new IllegalStateException("Cannot close an optional group without elements.");
        }
        optionalFlag = true;
        return endGroup();
    }

    /**
     * Mark the next token as finite.
     *
     * @return itself
     */
    @Contract(mutates = "this", value = "-> this")
    public VersionTypeBuilder finite() {
        if (finiteFlag) {
            throw new IllegalStateException("Cannot mark a token as finite twice.");
        }
        finiteFlag = true;
        return this;
    }

    /**
     * Build the version type out of the tokens added to the builder in the given order.
     *
     * @return the built version type
     */
    @Contract(mutates = "this", value = "-> new")
    public DefaultVersionType build() {
        if (tokens.isEmpty()) {
            throw new IllegalStateException("Cannot build an empty version type.");
        }
        if (!tokens.get(tokens.size() - 1).finite()) {
            throw new IllegalStateException("Last token group is not closed. Last token '%s' is not finite.".formatted(tokens.get(tokens.size() - 1).name()));
        }
        return new DefaultVersionType(tokens);
    }

    /**
     * Add a token with a name, the given pattern, and comparator for the token.
     *
     * @param name            the name of the token
     * @param pattern         the pattern of the token
     * @param tokenComparator the comparator for the token
     * @param absenceDefault  the default value to return if the token is absent
     * @return itself
     */
    private VersionTypeBuilder token(final String name, final Pattern pattern, final Comparator<String> tokenComparator, @Nullable final String absenceDefault) {
        tokens.add(new DefaultVersionToken(name, pattern, tokenComparator, currentGroup, optionalFlag, finiteFlag, absenceDefault));
        optionalFlag = false;
        if (finiteFlag) {
            finiteFlag = false;
            return endGroup();
        }
        return this;
    }

    /**
     * Add a token with a name, the given pattern, and comparator for the token.
     *
     * @param name            the name of the token
     * @param pattern         the pattern of the token
     * @param tokenComparator the comparator for the token
     * @return itself
     */
    private VersionTypeBuilder token(final String name, final Pattern pattern, final Comparator<String> tokenComparator) {
        return token(name, pattern, tokenComparator, null);
    }

    /**
     * Create a new group of the next tokens.
     *
     * @return itself
     */
    private VersionTypeBuilder endGroup() {
        if (tokens.isEmpty()) {
            throw new IllegalStateException("Cannot create a new group without tokens.");
        }
        final VersionToken lastToken = tokens.get(tokens.size() - 1);
        if (lastToken.group() < currentGroup) {
            if (!lastToken.finite()) {
                throw new IllegalStateException("Cannot close a group that is not closed.");
            }
            return this;
        }
        currentGroup++;
        return this;
    }

    /**
     * A comparator that does nothing and always returns 0.
     *
     * @return an identity comparator
     */
    private Comparator<String> identityComparator() {
        return (elem1, elem2) -> 0;
    }

    /**
     * Default implementation of VersionTokenType.
     *
     * @param name            the name of the token
     * @param pattern         the pattern of the token
     * @param tokenComparator the comparator of the token
     * @param group           the group of the token
     * @param optional        if the token is optionally ejectable
     * @param finite          if the token is finite
     * @param absenceDefault  the default value to return if the token is absent
     */
    private record DefaultVersionToken(String name, Pattern pattern, Comparator<String> tokenComparator,
                                       int group, boolean optional, boolean finite,
                                       @Nullable String absenceDefault) implements VersionToken {

        @Override
        public String toString() {
            return "DefaultVersionToken{name='%s', pattern=%s, group=%d, optional=%s, finite=%s, default=%s}"
                    .formatted(name, pattern, group, optional, finite, absenceDefault);
        }
    }
}
