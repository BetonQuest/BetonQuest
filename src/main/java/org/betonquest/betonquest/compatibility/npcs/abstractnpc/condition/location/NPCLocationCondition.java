package org.betonquest.betonquest.compatibility.npcs.abstractnpc.condition.location;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCAdapterSupplier;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Checks if a npc is at a specific location.
 */
public class NPCLocationCondition implements NullableCondition {
    /**
     * The NPC Adapter supplier.
     */
    private final NPCAdapterSupplier npcSupplier;

    /**
     * The location where the NPC has to be around.
     */
    private final VariableLocation location;

    /**
     * The maximal distance between the NPC and the radius location.
     */
    private final VariableNumber radius;

    /**
     * Create a new NPCLocationCondition.
     *
     * @param npcSupplier the supplier for the npc adapter
     * @param location    the location where the npc has to be around
     * @param radius      the maximal distance between the npc and the radius location
     */
    public NPCLocationCondition(final NPCAdapterSupplier npcSupplier,
                                final VariableLocation location, final VariableNumber radius) {
        this.npcSupplier = npcSupplier;
        this.location = location;
        this.radius = radius;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestRuntimeException {
        final BQNPCAdapter<?> npc = npcSupplier.get();
        final Location location = this.location.getValue(profile);
        final Location npcLocation = npc.getLocation();
        if (!location.getWorld().equals(npcLocation.getWorld())) {
            return false;
        }
        final double radius = this.radius.getValue(profile).doubleValue();
        return npcLocation.distanceSquared(location) <= radius * radius;
    }
}
