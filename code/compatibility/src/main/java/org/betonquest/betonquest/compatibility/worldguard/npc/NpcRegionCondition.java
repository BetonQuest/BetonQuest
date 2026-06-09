package org.betonquest.betonquest.compatibility.worldguard.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.NullableCondition;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.service.npc.NpcManager;
import org.betonquest.betonquest.compatibility.worldguard.WorldGuardIntegrator;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

/**
 * Checks if a Npc is inside a WorldGuard region.
 */
public class NpcRegionCondition implements NullableCondition {

    /**
     * The npc manager to get npcs.
     */
    private final NpcManager npcManager;

    /**
     * The Npc id.
     */
    private final Argument<NpcIdentifier> npcId;

    /**
     * The region name where the Npc should be.
     */
    private final Argument<String> region;

    /**
     * Create a new NpcRegionCondition.
     *
     * @param npcManager the npc manager to get npcs
     * @param npcId      the npc id, null or positive
     * @param region     the name of the region where the NPC should be
     */
    public NpcRegionCondition(final NpcManager npcManager, final Argument<NpcIdentifier> npcId, final Argument<String> region) {
        this.npcManager = npcManager;
        this.npcId = npcId;
        this.region = region;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final Set<Npc<?>> npcs = npcManager.getAll(profile, npcId.getValue(profile));
        final String region = this.region.getValue(profile);
        return npcs.stream().anyMatch(npc -> {
            if (!npc.isSpawned()) {
                return false;
            }
            final Optional<Location> location = npc.getLocation();
            return location.isPresent() && WorldGuardIntegrator.isInsideRegion(location.get(), region);
        });
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
