package org.betonquest.betonquest.quest.event.npc;

import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Teleports a Npc to a given location.
 */
public class NpcTeleportEvent implements NullableEvent {

    /**
     * Quest Type API.
     */
    private final FeatureApi featureApi;

    /**
     * The npc id.
     */
    private final Variable<NpcID> npcId;

    /**
     * The location to teleport the Npc to.
     */
    private final Variable<Location> location;

    /**
     * Spawns the Npc if not already spawned.
     */
    private final boolean spawn;

    /**
     * Create a new Npc Teleport Event.
     *
     * @param featureApi the Feature API
     * @param npcId      the npc id
     * @param location   the location the Npc will be teleported to
     * @param spawn      if the npc should be spawned if not in the world
     */
    public NpcTeleportEvent(final FeatureApi featureApi, final Variable<NpcID> npcId, final Variable<Location> location, final boolean spawn) {
        this.featureApi = featureApi;
        this.npcId = npcId;
        this.location = location;
        this.spawn = spawn;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final Location loc = location.getValue(profile);
        final Npc<?> npc = featureApi.getNpc(npcId.getValue(profile), profile);
        if (npc.isSpawned()) {
            npc.teleport(loc);
        } else if (spawn) {
            npc.spawn(loc);
        }
    }
}
