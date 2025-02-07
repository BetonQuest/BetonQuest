package org.betonquest.betonquest.compatibility.worldguard.npc;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.compatibility.worldguard.WorldGuardIntegrator;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.quest.registry.processor.NpcProcessor;

/**
 * Checks if a Npc is inside a WorldGuard region.
 */
public class NpcRegionCondition implements PlayerlessCondition {
    /**
     * Processor to get npc.
     */
    private final NpcProcessor npcProcessor;

    /**
     * The Npc id.
     */
    private final NpcID npcId;

    /**
     * The region name where the NPC should be.
     */
    private final String region;

    /**
     * Create a new NPCRegionCondition.
     *
     * @param npcProcessor the processor to get npc
     * @param npcId        the npc id, null or positive
     * @param region       the name of the region where the NPC should be
     */
    public NpcRegionCondition(final NpcProcessor npcProcessor, final NpcID npcId, final String region) {
        this.npcProcessor = npcProcessor;
        this.npcId = npcId;
        this.region = region;
    }

    @Override
    public boolean check() throws QuestException {
        return WorldGuardIntegrator.isInsideRegion(npcProcessor.getNpc(npcId).getLocation(), region);
    }
}
