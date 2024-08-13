package org.betonquest.betonquest.compatibility.npcs.abstractnpc.condition.region;

import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCAdapterSupplier;
import org.betonquest.betonquest.compatibility.worldguard.WorldGuardIntegrator;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Checks if a npc is inside a region.
 */
public class NPCRegionCondition implements PlayerlessCondition {
    /**
     * The NPC Adapter supplier.
     */
    private final NPCAdapterSupplier npcSupplier;

    /**
     * The region name where the NPC should be.
     */
    private final String region;

    /**
     * Create a new NPCRegionCondition.
     *
     * @param npcSupplier the npc adapter supplier
     * @param region      the name of the region where the NPC should be
     */
    public NPCRegionCondition(final NPCAdapterSupplier npcSupplier, final String region) {
        this.npcSupplier = npcSupplier;
        this.region = region;
    }

    @Override
    public boolean check() throws QuestRuntimeException {
        return WorldGuardIntegrator.isInsideRegion(npcSupplier.get().getLocation(), region);
    }
}
