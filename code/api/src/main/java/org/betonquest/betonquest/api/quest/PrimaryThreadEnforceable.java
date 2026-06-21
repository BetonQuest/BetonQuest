package org.betonquest.betonquest.api.quest;

import org.jetbrains.annotations.Contract;

/**
 * Interface for quest types that can enforce execution on the primary server thread.
 *
 * @since 3.0.0
 */
public interface PrimaryThreadEnforceable {

    /**
     * Indicates whether the quest type enforces execution on the primary server thread.
     *
     * @return true if primary thread enforcement is required, false otherwise
     * @since 3.0.0
     */
    @Contract(pure = true)
    default boolean isPrimaryThreadEnforced() {
        return false;
    }
}
