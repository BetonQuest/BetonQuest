package org.betonquest.betonquest.compatibility.fancynpcs.condition.region;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.compatibility.worldguard.WorldGuardIntegrator;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Checks if a npc is inside a WorldGuard region.
 */
public class NPCRegionCondition implements PlayerlessCondition {
    /**
     * The NPC id.
     */
    private final String npcId;

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
    public NPCRegionCondition(final String npcId, final String region) {
        this.npcId = npcId;
        this.region = region;
    }

    @Override
    public boolean check() throws QuestRuntimeException {
        final Npc npc = FancyNpcsPlugin.get().getNpcManager().getNpcById(npcId);
        return npc != null && WorldGuardIntegrator.isInsideRegion(npc.getData().getLocation(), region);
    }
}
