package org.betonquest.betonquest.compatibility.worldguard.npc;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.compatibility.worldguard.WorldGuardIntegrator;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.jetbrains.annotations.Nullable;

/**
 * Checks if a npc is inside a WorldGuard region.
 */
public class NPCRegionCondition implements NullableCondition {
    /**
     * The NPC id.
     */
    private final int npcId;

    /**
     * The region name where the NPC should be.
     */
    private final VariableString region;

    /**
     * Create a new NPCRegionCondition.
     *
     * @param npcId  the npc id, null or positive
     * @param region the name of the region where the NPC should be
     */
    public NPCRegionCondition(final int npcId, final VariableString region) {
        this.npcId = npcId;
        this.region = region;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        return npc != null && WorldGuardIntegrator.isInsideRegion(npc.getStoredLocation(), region.getValue(profile));
    }
}
