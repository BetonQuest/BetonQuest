package org.betonquest.betonquest.quest.condition.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Checks if a npc is at a specific location.
 */
public class NpcLocationCondition implements NullableCondition {

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * Id of the npc.
     */
    private final Variable<NpcID> npcId;

    /**
     * The location where the NPC has to be around.
     */
    private final Variable<Location> location;

    /**
     * The maximal distance between the NPC and the radius location.
     */
    private final Variable<Number> radius;

    /**
     * Create a new NPCLocationCondition.
     *
     * @param featureApi the Quest Type API
     * @param npcId      the id of the npc
     * @param location   the location where the npc has to be around
     * @param radius     the maximal distance between the npc and the radius location
     */
    public NpcLocationCondition(final FeatureApi featureApi, final Variable<NpcID> npcId,
                                final Variable<Location> location, final Variable<Number> radius) {
        this.featureApi = featureApi;
        this.npcId = npcId;
        this.location = location;
        this.radius = radius;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final Npc<?> npc = featureApi.getNpc(npcId.getValue(profile), profile);
        final Location location = this.location.getValue(profile);
        final Optional<Location> loc = npc.getLocation();
        if (loc.isEmpty()) {
            return false;
        }
        final Location npcLocation = loc.get();
        if (!location.getWorld().equals(npcLocation.getWorld())) {
            return false;
        }
        final double radius = this.radius.getValue(profile).doubleValue();
        return npcLocation.distanceSquared(location) <= radius * radius;
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
