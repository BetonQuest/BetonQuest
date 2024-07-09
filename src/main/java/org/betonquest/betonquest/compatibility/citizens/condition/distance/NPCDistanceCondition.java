package org.betonquest.betonquest.compatibility.citizens.condition.distance;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.entity.Player;

/**
 * Checks if the player is close to a npc.
 */
public class NPCDistanceCondition implements PlayerCondition {
    /**
     * The NPC id.
     */
    private final int npcId;

    /**
     * The maximal distance between player and NPC.
     */
    private final VariableNumber distance;

    /**
     * Create a new NPCDistanceCondition.
     *
     * @param npcId    the npc id
     * @param distance the maximal distance between player and npc
     */
    public NPCDistanceCondition(final int npcId, final VariableNumber distance) {
        this.npcId = npcId;
        this.distance = distance;
    }

    @Override
    public boolean check(final Profile profile) throws QuestRuntimeException {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc == null) {
            throw new QuestRuntimeException("NPC with ID " + npcId + " does not exist");
        }
        final Player player = profile.getOnlineProfile().get().getPlayer();
        if (!player.getWorld().equals(npc.getStoredLocation().getWorld())) {
            return false;
        }
        final double distance = this.distance.getValue(profile).doubleValue();
        return npc.getStoredLocation().distanceSquared(player.getLocation()) <= distance * distance;
    }
}
