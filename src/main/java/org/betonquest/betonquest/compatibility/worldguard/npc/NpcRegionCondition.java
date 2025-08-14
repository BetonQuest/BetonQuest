package org.betonquest.betonquest.compatibility.worldguard.npc;

import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.compatibility.worldguard.WorldGuardIntegrator;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.jetbrains.annotations.Nullable;

/**
 * Checks if a Npc is inside a WorldGuard region.
 */
public class NpcRegionCondition implements NullableCondition {

    /**
     * Quest Type API.
     */
    private final FeatureApi featureApi;

    /**
     * The Npc id.
     */
    private final Variable<NpcID> npcId;

    /**
     * The region name where the Npc should be.
     */
    private final Variable<String> region;

    /**
     * Create a new NpcRegionCondition.
     *
     * @param featureApi the Feature API
     * @param npcId      the npc id, null or positive
     * @param region     the name of the region where the NPC should be
     */
    public NpcRegionCondition(final FeatureApi featureApi, final Variable<NpcID> npcId, final Variable<String> region) {
        this.featureApi = featureApi;
        this.npcId = npcId;
        this.region = region;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        return WorldGuardIntegrator.isInsideRegion(featureApi.getNpc(npcId.getValue(profile), profile).getLocation(), region.getValue(profile));
    }
}
