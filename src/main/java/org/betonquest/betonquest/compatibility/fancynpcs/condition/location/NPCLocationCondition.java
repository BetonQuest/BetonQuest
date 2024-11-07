package org.betonquest.betonquest.compatibility.fancynpcs.condition.location;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
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
     * The NPC id.
     */
    private final String npcId;

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
    public NPCLocationCondition(final String npcId, final VariableLocation location, final VariableNumber radius) {
        this.npcId = npcId;
        this.location = location;
        this.radius = radius;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestRuntimeException {
        final Npc npc = FancyNpcsPlugin.get().getNpcManager().getNpcById(npcId);
        if (npc == null) {
            throw new QuestRuntimeException("NPC with ID " + npcId + " does not exist");
        }
        final Location location = this.location.getValue(profile);
        if (!location.getWorld().equals(npc.getData().getLocation().getWorld())) {
            return false;
        }
        final double radius = this.radius.getValue(profile).doubleValue();
        return npc.getData().getLocation().distanceSquared(location) <= radius * radius;
    }
}
