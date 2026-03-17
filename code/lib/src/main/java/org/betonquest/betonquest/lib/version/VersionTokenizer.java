package org.betonquest.betonquest.lib.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class for tokenizing version strings based on regular expressions.
 * This class allows iteration through version strings and consumption of tokens
 * matching specific patterns, facilitating parsing and validation of version strings.
 * <p>
 * Instances of this class maintain a pointer to the current position in the version
 * string and provide mechanisms to check for token matches, consume tokens, and
 * verify whether tokens remain.
 * <p>
 * This class is primarily used in conjunction with version-related classes like
 * {@code Version}, {@code VersionType}, and {@code VersionTokenType}.
 */
public class VersionTokenizer {

    /**
     * The version string to tokenize.
     */
    private final String versionString;

    /**
     * The current pointer.
     */
    private int pointer;

    /**
     * Creates a new VersionTokenizer for the given version string.
     *
     * @param versionString the version string
     */
    public VersionTokenizer(final String versionString) {
        this.versionString = versionString;
        this.pointer = 0;
    }

    /**
     * Checks if the version string at the current pointer matches the given regex.
     *
     * @param regex the regex to match against
     * @return true if the regex matches, false otherwise
     */
    public boolean has(final Pattern regex) {
        return canConsume() && regex.asPredicate().test(versionString.substring(pointer));
    }

    /**
     * Consumes the next token that matches the given regex.
     * Check if the regex matches at the current pointer by calling {@link #has(Pattern)}.
     *
     * @param regex the regex to match against
     * @return the consumed token or null if no token was consumed
     */
    public String consume(final Pattern regex) {
        final Matcher matcher = regex.matcher(versionString.substring(pointer));
        if (!matcher.find()) {
            throw new IllegalArgumentException("No match found for pattern: '%s'".formatted(regex));
        }
        final int end = pointer + matcher.end();
        final String token = versionString.substring(pointer, end);
        pointer = end;
        return token;
    }

    /**
     * Checks if there is a token left that can be consumed.
     *
     * @return true if there is a token left
     */
    public boolean canConsume() {
        return pointer < versionString.length();
    }

    /**
     * Get the current pointer.
     *
     * @return the current pointer
     */
    public int getCurrentPointer() {
        return pointer;
    }

    /**
     * Reset the pointer to the given position.
     *
     * @param pointer the new pointer position
     */
    public void resetPointer(final int pointer) {
        this.pointer = pointer;
    }

    @Override
    public String toString() {
        return "VersionTokenizer{version='%s', pointer=%d, pointedVersion='%s'}".formatted(versionString, pointer, versionString.substring(pointer));
    }
}
