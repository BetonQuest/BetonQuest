package org.betonquest.betonquest.api.quest;

/**
 * Interface for quest types that can enforce execution on the primary server thread.
 */
public interface PrimaryThreadEnforceable {

    /**
     * Indicates whether the quest type enforces execution on the primary server thread.
     *
     * @return true if primary thread enforcement is required, false otherwise
     */
    default boolean isPrimaryThreadEnforced() {
        return false;
    }
}
