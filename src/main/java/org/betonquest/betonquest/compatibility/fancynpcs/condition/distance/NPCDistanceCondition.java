package org.betonquest.betonquest.compatibility.fancynpcs.condition.distance;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.entity.Player;

/**
 * Checks if the player is close to a npc.
 */
public class NPCDistanceCondition implements OnlineCondition {
    /**
     * The NPC id.
     */
    private final String npcId;

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
    public NPCDistanceCondition(final String npcId, final VariableNumber distance) {
        this.npcId = npcId;
        this.distance = distance;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestRuntimeException {
        final Npc npc = FancyNpcsPlugin.get().getNpcManager().getNpcById(npcId);

        if (npc == null) {
            throw new QuestRuntimeException("NPC with ID " + npcId + " does not exist");
        }
        final Player player = profile.getPlayer();
        if (!player.getWorld().equals(npc.getData().getLocation().getWorld())) {
            return false;
        }
        final double distance = this.distance.getValue(profile).doubleValue();
        return npc.getData().getLocation().distanceSquared(player.getLocation()) <= distance * distance;
    }
}
