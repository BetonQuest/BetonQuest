package org.betonquest.betonquest.api.version;

import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.regex.Pattern;

/**
 * Represents a type of token used in version parsing and comparison.
 * A {@code VersionTokenType} defines the characteristics of individual
 * components within a version string, including their name, pattern,
 * comparison logic, and whether they are optionally ejectable.
 * <p>
 * Implementations of this interface are used as part of a version type to
 * determine the structure and parsing rules for version strings.
 */
public interface VersionToken {

    /**
     * The name of the token.
     *
     * @return the name of the token
     */
    String name();

    /**
     * The regular expression pattern used to match the token.
     *
     * @return the regular expression pattern
     */
    Pattern pattern();

    /**
     * The comparator used to compare tokens of this type.
     *
     * @return the comparator used to compare tokens of this type
     */
    Comparator<String> tokenComparator();

    /**
     * The group of the token.
     * <p>
     * Only the first element of a group might be {@link #optional()} to indicate that the group is optional.
     * Only the last element of a group might be {@link #finite()} to indicate that the group is finite.
     * <p>
     * Starts from 0 and increases by 1 for each element.
     *
     * @return the group of the token
     */
    int group();

    /**
     * The default value to use if the token is absent.
     * <p>
     * This is used when parsing a version string that does not contain the token,
     * so this value is used instead of null.
     * <p>
     * Has no effect if the token is not {@link #optional()}.
     *
     * @return the default value to use if the optional token is absent
     */
    @Nullable
    String absenceDefault();

    /**
     * Whether the token can be ejected from the version string.
     * <p>
     * Ejectable tokens are optional parts of a version that may or may not be present.
     * For example, in a version string like "1.0.0+123", the build number "123"
     * could be an ejectable token, allowing versions "1.0.0+123" and "1.0.0"
     * to both be valid.
     *
     * @return true if the token can be ejected, false otherwise.
     */
    boolean optional();

    /**
     * Whether the token is finite.
     * <p>
     * At least one finite token must be present in a version string at the end
     * for the version string to be parsed successfully.
     * A valid version string must always end with a finite token.
     *
     * @return true if the token is finite, false otherwise.
     */
    boolean finite();
}
