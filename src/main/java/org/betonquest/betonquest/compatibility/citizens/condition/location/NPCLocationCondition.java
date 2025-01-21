package org.betonquest.betonquest.compatibility.citizens.condition.location;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Checks if a npc is at a specific location.
 */
public class NPCLocationCondition implements NullableCondition {
    /**
     * The NPC id.
     */
    private final int npcId;

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
     * @param npcId    the npc id, null or positive
     * @param location the location where the npc has to be around
     * @param radius   the maximal distance between the npc and the radius location
     */
    public NPCLocationCondition(final int npcId, final VariableLocation location, final VariableNumber radius) {
        this.npcId = npcId;
        this.location = location;
        this.radius = radius;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc == null) {
            throw new QuestException("NPC with ID " + npcId + " does not exist");
        }
        final Location location = this.location.getValue(profile);
        if (!location.getWorld().equals(npc.getStoredLocation().getWorld())) {
            return false;
        }
        final double radius = this.radius.getValue(profile).doubleValue();
        return npc.getStoredLocation().distanceSquared(location) <= radius * radius;
    }
}
