package org.betonquest.betonquest.quest.action.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.NullableAction;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.service.npc.NpcManager;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Teleports a Npc to a given location.
 */
public class NpcTeleportAction implements NullableAction {

    /**
     * The npc manager.
     */
    private final NpcManager npcManager;

    /**
     * The npc id.
     */
    private final Argument<NpcIdentifier> npcId;

    /**
     * The location to teleport the Npc to.
     */
    private final Argument<Location> location;

    /**
     * Spawns the Npc if not already spawned.
     */
    private final FlagArgument<Boolean> spawn;

    /**
     * Create a new Npc Teleport Action.
     *
     * @param npcManager the npc manager
     * @param npcId      the npc id
     * @param location   the location the Npc will be teleported to
     * @param spawn      if the npc should be spawned if not in the world
     */
    public NpcTeleportAction(final NpcManager npcManager, final Argument<NpcIdentifier> npcId, final Argument<Location> location,
                             final FlagArgument<Boolean> spawn) {
        this.npcManager = npcManager;
        this.npcId = npcId;
        this.location = location;
        this.spawn = spawn;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final Location loc = location.getValue(profile);
        final Set<Npc<?>> npcs = npcManager.getAll(profile, npcId.getValue(profile));
        final boolean shouldSpawn = spawn.getValue(profile).orElse(false);
        npcs.forEach(npc -> {
            if (npc.isSpawned()) {
                npc.teleport(loc);
            } else if (shouldSpawn) {
                npc.spawn(loc);
            }
        });
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
