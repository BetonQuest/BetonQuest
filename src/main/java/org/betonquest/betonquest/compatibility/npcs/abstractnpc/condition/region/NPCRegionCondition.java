package org.betonquest.betonquest.compatibility.npcs.abstractnpc.condition.region;

import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.worldguard.WorldGuardIntegrator;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.util.function.Supplier;

/**
 * Checks if a npc is inside a region.
 */
public class NPCRegionCondition implements PlayerlessCondition {
    /**
     * The NPC Adapter supplier.
     */
    private final Supplier<BQNPCAdapter<?>> npcSupplier;

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
    public NPCRegionCondition(final Supplier<BQNPCAdapter<?>> npcSupplier, final String region) {
        this.npcSupplier = npcSupplier;
        this.region = region;
    }

    @Override
    public boolean check() throws QuestRuntimeException {
        final BQNPCAdapter<?> npc = npcSupplier.get();
        return npc != null && WorldGuardIntegrator.isInsideRegion(npc.getLocation(), region);
    }
}
