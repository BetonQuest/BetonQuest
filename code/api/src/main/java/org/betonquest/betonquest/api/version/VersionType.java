package org.betonquest.betonquest.api.version;

import java.util.List;
import java.util.Map;

/**
 * A {@code VersionType} specifies the format and components of version strings, enabling
 * parsing, validation, and comparison. It consists of a sequence of {@link VersionToken}
 * instances that define the expected structure of version strings.
 * <p>
 * Tokens can be organized into groups, where each group represents a logical unit within
 * the version structure. Groups are used to define optional sections and finite boundaries
 * in version strings.
 *
 * @see VersionToken
 */
public interface VersionType {

    /**
     * Returns the sequence of tokens that define this version type.
     * <p>
     * The tokens are returned in the order they appear in the version string format.
     * This ordered list is used for parsing version strings and determining the structure
     * of valid versions.
     *
     * @return an unmodifiable list of tokens in the order they define this version type
     */
    List<VersionToken> tokens();

    /**
     * Returns the tokens grouped by their group number.
     * <p>
     * Tokens within the same group belong to the same logical section of the version string.
     * Each group is identified by a numeric key, starting from 0. The order of tokens within
     * each group is preserved according to their original sequence.
     * <p>
     * Groups are particularly useful for handling optional sections and finite boundaries.
     * Only the first token of a group may be optional (indicating the entire group is optional),
     * and only the last token of a group may be finite (indicating the group marks a valid
     * ending point for the version string).
     *
     * @return an unmodifiable map where keys are group numbers and values are lists of tokens
     * in that group, preserving their original order
     */
    Map<Integer, List<VersionToken>> tokenGroups();
}
