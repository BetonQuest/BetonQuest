package org.betonquest.betonquest.compatibility.npcs.abstractnpc;

import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.util.function.Supplier;

/**
 * Supplies a NPC in an easy usable way in Events, Objectives, Conditions and Variables.
 */
@FunctionalInterface
public interface NPCSupplierStandard {
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
    Supplier<BQNPCAdapter> getSupplierByID(String npcId) throws InstructionParseException;
}
