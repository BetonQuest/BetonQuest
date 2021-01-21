package org.betonquest.betonquest;

/**
 * Represents the journal pointer.
 */
@SuppressWarnings("PMD.AvoidFieldNameMatchingTypeName")
public class Pointer {

    /**
     * String pointing to the journal entry in journal.yml
     */
    private final String pointer;
    /**
     * Timestamp indicating date of this entry
     */
    private final long timestamp;

    /**
     * Creates a new Pointer from the pointer string and relevant timestamp.
     *
     * @param pointer   the name of the journal entry
     * @param timestamp exact date this entry was added to journal
     */
    public Pointer(final String pointer, final long timestamp) {
        this.pointer = pointer;
        this.timestamp = timestamp;
    }

    /**
     * Returns the name of the journal entry from journal.yml.
     *
     * @return the name of the journal entry
     */
    public String getPointer() {
        return pointer;
    }

    /**
     * Returns the timestamp of the journal entry.
     *
     * @return the timestamp of the journal entry
     */
    public long getTimestamp() {
        return timestamp;
    }
}
