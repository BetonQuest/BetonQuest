package org.betonquest.betonquest.compatibility.npcs.abstractnpc;

import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Supplies a NPC adapter for BetonQuest usage.
 * <p>
 * This is just a function which can throw an {@link InstructionParseException}.
 */
@FunctionalInterface
public interface NPCAdapterSupplierSupplier {
    /**
     * Gets a supplier which will return a new {@link BQNPCAdapter}
     * if the {@code npcID} has a valid npc or null.
     * <p>
     * This method validates the {@code npcId} format and will throw if it
     * has an invalid format for the backing NPC plugin.
     *
     * @param npcId of the npc returned by the supplier
     * @return the supplier which will return the npc or null if none was found by the npcId
     * @throws InstructionParseException if the npcId is invalid for the adapter
     */
    NPCAdapterSupplier getSupplierByID(String npcId) throws InstructionParseException;
}
