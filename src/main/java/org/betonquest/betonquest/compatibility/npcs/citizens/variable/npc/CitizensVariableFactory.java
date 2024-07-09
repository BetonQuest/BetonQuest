package org.betonquest.betonquest.compatibility.npcs.citizens.variable.npc;

import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCSupplierStandard;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.variable.npc.NPCVariableFactory;

/**
 * Factory to create NPCVariables with {@link NPC Citizens NPC}s from Instructions.
 */
public class CitizensVariableFactory extends NPCVariableFactory {
    /**
     * Create a new NPCVariable factory for Citizens NPCs.
     *
     * @param supplierStandard the supplier providing the npc adapter supplier
     * @param loggerFactory    the logger factory creating new custom logger
     */
    public CitizensVariableFactory(final NPCSupplierStandard supplierStandard, final BetonQuestLoggerFactory loggerFactory) {
        super(supplierStandard, loggerFactory);
    }
}
