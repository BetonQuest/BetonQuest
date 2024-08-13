package org.betonquest.betonquest.compatibility.npcs.abstractnpc;

import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Supplies a {@link BQNPCAdapter}.
 * <p>
 * This is just a function which can throw a {@link QuestRuntimeException}.
 */
@FunctionalInterface
public interface NPCAdapterSupplier {
    /**
     * Gets a {@link BQNPCAdapter} which got prior validated.
     *
     * @return the NPC Adapter
     * @throws QuestRuntimeException when no NPC Adapter can be returned
     */
    BQNPCAdapter<?> get() throws QuestRuntimeException;
}
