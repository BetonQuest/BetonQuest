package org.betonquest.betonquest.compatibility.worldguard.npc;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.compatibility.worldguard.WorldGuardIntegrator;
import org.betonquest.betonquest.id.NpcID;

/**
 * Checks if a Npc is inside a WorldGuard region.
 */
public class NpcRegionCondition implements PlayerlessCondition {

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

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
     * @param questTypeAPI the Quest Type API
     * @param npcId        the npc id, null or positive
     * @param region       the name of the region where the NPC should be
     */
    public NpcRegionCondition(final QuestTypeAPI questTypeAPI, final NpcID npcId, final String region) {
        this.questTypeAPI = questTypeAPI;
        this.npcId = npcId;
        this.region = region;
    }

    @Override
    public boolean check() throws QuestException {
        return WorldGuardIntegrator.isInsideRegion(questTypeAPI.getNpc(npcId).getLocation(), region);
    }
}
