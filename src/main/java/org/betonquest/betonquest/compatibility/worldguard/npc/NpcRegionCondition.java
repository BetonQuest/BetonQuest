package org.betonquest.betonquest.compatibility.worldguard.npc;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.compatibility.worldguard.WorldGuardIntegrator;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.jetbrains.annotations.Nullable;

/**
 * Checks if a Npc is inside a WorldGuard region.
 */
public class NpcRegionCondition implements NullableCondition {

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * The Npc id.
     */
    private final NpcID npcId;

    /**
     * The region name where the Npc should be.
     */
    private final VariableString region;

    /**
     * Create a new NpcRegionCondition.
     *
     * @param questTypeAPI the Quest Type API
     * @param npcId        the npc id, null or positive
     * @param region       the name of the region where the NPC should be
     */
    public NpcRegionCondition(final QuestTypeAPI questTypeAPI, final NpcID npcId, final VariableString region) {
        this.questTypeAPI = questTypeAPI;
        this.npcId = npcId;
        this.region = region;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        return WorldGuardIntegrator.isInsideRegion(questTypeAPI.getNpc(npcId).getLocation(), region.getValue(profile));
    }
}
