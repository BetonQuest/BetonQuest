package org.betonquest.betonquest;

/**
 * Represents the journal pointer.
 *
 * @param pointer   String pointing to the journal entry.
 * @param timestamp Timestamp indicating date of this entry.
 */
@SuppressWarnings("PMD.AvoidFieldNameMatchingTypeName")
public record Pointer(String pointer, long timestamp) {
}
