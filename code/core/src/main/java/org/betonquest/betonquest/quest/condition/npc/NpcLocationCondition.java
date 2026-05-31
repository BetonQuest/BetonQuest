package org.betonquest.betonquest.quest.condition.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.NullableCondition;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.service.npc.NpcManager;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

/**
 * Checks if a npc is at a specific location.
 */
public class NpcLocationCondition implements NullableCondition {

    /**
     * The npc manager.
     */
    private final NpcManager npcManager;

    /**
     * Id of the npc.
     */
    private final Argument<NpcIdentifier> npcId;

    /**
     * The location where the NPC has to be around.
     */
    private final Argument<Location> location;

    /**
     * The maximal distance between the NPC and the radius location.
     */
    private final Argument<Number> radius;

    /**
     * Create a new NPCLocationCondition.
     *
     * @param npcManager the npc manager
     * @param npcId      the id of the npc
     * @param location   the location where the npc has to be around
     * @param radius     the maximal distance between the npc and the radius location
     */
    public NpcLocationCondition(final NpcManager npcManager, final Argument<NpcIdentifier> npcId,
                                final Argument<Location> location, final Argument<Number> radius) {
        this.npcManager = npcManager;
        this.npcId = npcId;
        this.location = location;
        this.radius = radius;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final Set<Npc<?>> npcs = npcManager.getAll(profile, npcId.getValue(profile));
        final Location location = this.location.getValue(profile);
        final double radius = this.radius.getValue(profile).doubleValue();
        final double radiusRadius = radius * radius;
        return npcs.stream().anyMatch(npc -> isNpcNearby(npc, location, radiusRadius));
    }

    private boolean isNpcNearby(final Npc<?> npc, final Location location, final double radiusRadius) {
        if (!npc.isSpawned()) {
            return false;
        }
        final Optional<Location> loc = npc.getLocation();
        if (loc.isEmpty()) {
            return false;
        }
        final Location npcLocation = loc.get();
        return location.getWorld().equals(npcLocation.getWorld()) && npcLocation.distanceSquared(location) <= radiusRadius;
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
