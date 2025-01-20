package org.betonquest.betonquest.compatibility.citizens.condition.region;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.compatibility.worldguard.WorldGuardIntegrator;

/**
 * Checks if a npc is inside a WorldGuard region.
 */
public class NPCRegionCondition implements PlayerlessCondition {
    /**
     * The NPC id.
     */
    private final int npcId;

    /**
     * The region name where the NPC should be.
     */
    private final String region;

    /**
     * Create a new NPCRegionCondition.
     *
     * @param npcId  the npc id, null or positive
     * @param region the name of the region where the NPC should be
     */
    public NPCRegionCondition(final int npcId, final String region) {
        this.npcId = npcId;
        this.region = region;
    }

    @Override
    public boolean check() throws QuestException {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        return npc != null && WorldGuardIntegrator.isInsideRegion(npc.getStoredLocation(), region);
    }
}
