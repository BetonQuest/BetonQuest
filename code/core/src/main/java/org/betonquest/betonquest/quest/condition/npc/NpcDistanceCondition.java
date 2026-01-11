package org.betonquest.betonquest.quest.condition.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Checks if the player is close to a npc.
 */
public class NpcDistanceCondition implements OnlineCondition {

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * Id of the npc.
     */
    private final Argument<NpcIdentifier> npcID;

    /**
     * The maximal distance between player and Npc.
     */
    private final Argument<Number> distance;

    /**
     * Create a new Npc Distance Condition.
     *
     * @param featureApi the Feature API
     * @param npcID      the id of the npc
     * @param distance   the maximal distance between player and npc
     */
    public NpcDistanceCondition(final FeatureApi featureApi, final Argument<NpcIdentifier> npcID, final Argument<Number> distance) {
        this.featureApi = featureApi;
        this.npcID = npcID;
        this.distance = distance;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final Npc<?> npc = featureApi.getNpc(npcID.getValue(profile), profile);
        if (!npc.isSpawned()) {
            return false;
        }
        final Optional<Location> loc = npc.getLocation();
        if (loc.isEmpty()) {
            return false;
        }
        final Location npcLocation = loc.get();
        final Player player = profile.getPlayer();
        if (!player.getWorld().equals(npcLocation.getWorld())) {
            return false;
        }
        final double distance = this.distance.getValue(profile).doubleValue();
        return npcLocation.distanceSquared(player.getLocation()) <= distance * distance;
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
