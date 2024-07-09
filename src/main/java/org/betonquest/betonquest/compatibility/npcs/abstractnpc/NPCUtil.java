package org.betonquest.betonquest.compatibility.npcs.abstractnpc;

import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.util.function.Supplier;

/**
 * Commonly used methods related to NPC Adapter.
 */
public final class NPCUtil {
    private NPCUtil() {
    }

    /**
     * Gets the npc from the supplier or throws on empty value from supplier.
     *
     * @param npcSupplier potentially providing the npc
     * @param npcId       the id of the npc used the exception message
     * @param <T>         the npc type
     * @return the supplied npc
     * @throws QuestRuntimeException when the supplier does not return a value
     */
    public static <T> T getNPC(final Supplier<T> npcSupplier, final String npcId) throws QuestRuntimeException {
        final T npc = npcSupplier.get();
        if (npc == null) {
            throw new QuestRuntimeException("NPC with ID " + npcId + " does not exist");
        }
        return npc;
    }
}
