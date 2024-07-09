package org.betonquest.betonquest.compatibility.npcs.abstractnpc.condition.location;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCUtil;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Checks if a npc is at a specific location.
 */
public class NPCLocationCondition implements NullableCondition {
    /**
     * The NPC id.
     */
    private final String npcId;

    /**
     * The NPC Adapter supplier.
     */
    private final Supplier<BQNPCAdapter<?>> npcSupplier;

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
     * @param npcId       the npc id, null or positive
     * @param npcSupplier the supplier for the npc adapter
     * @param location    the location where the npc has to be around
     * @param radius      the maximal distance between the npc and the radius location
     */
    public NPCLocationCondition(final String npcId, final Supplier<BQNPCAdapter<?>> npcSupplier,
                                final VariableLocation location, final VariableNumber radius) {
        this.npcId = npcId;
        this.npcSupplier = npcSupplier;
        this.location = location;
        this.radius = radius;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestRuntimeException {
        final BQNPCAdapter<?> npc = NPCUtil.getNPC(npcSupplier, npcId);
        final Location location = this.location.getValue(profile);
        final Location npcLocation = npc.getLocation();
        if (!location.getWorld().equals(npcLocation.getWorld())) {
            return false;
        }
        final double radius = this.radius.getValue(profile).doubleValue();
        return npcLocation.distanceSquared(location) <= radius * radius;
    }
}
