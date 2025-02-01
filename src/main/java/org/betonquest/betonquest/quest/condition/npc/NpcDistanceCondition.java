package org.betonquest.betonquest.quest.condition.npc;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.registry.processor.NpcProcessor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Checks if the player is close to a npc.
 */
public class NpcDistanceCondition implements OnlineCondition {
    /**
     * Processor to get npc.
     */
    private final NpcProcessor npcProcessor;

    /**
     * Id of the npc.
     */
    private final NpcID npcID;

    /**
     * The maximal distance between player and Npc.
     */
    private final VariableNumber distance;

    /**
     * Create a new Npc Distance Condition.
     *
     * @param npcProcessor the processor to get npc
     * @param npcID        the id of the npc
     * @param distance     the maximal distance between player and npc
     */
    public NpcDistanceCondition(final NpcProcessor npcProcessor, final NpcID npcID, final VariableNumber distance) {
        this.npcProcessor = npcProcessor;
        this.npcID = npcID;
        this.distance = distance;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final Location npcLocation = npcProcessor.getNpc(npcID).getLocation();
        final Player player = profile.getPlayer();
        if (!player.getWorld().equals(npcLocation.getWorld())) {
            return false;
        }
        final double distance = this.distance.getValue(profile).doubleValue();
        return npcLocation.distanceSquared(player.getLocation()) <= distance * distance;
    }
}
