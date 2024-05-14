package org.betonquest.betonquest.compatibility.npcs.abstractnpc.condition.distance;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCUtil;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

/**
 * Checks if the player is close to a npc.
 */
public class NPCDistanceCondition implements PlayerCondition {
    /**
     * The ID used in exception message when no NPC was found.
     */
    private final String npcId;

    /**
     * Supplier for the NPC to use.
     */
    private final Supplier<BQNPCAdapter> npcSupplier;

    /**
     * The maximal distance between player and NPC.
     */
    private final VariableNumber distance;

    /**
     * Create a new NPCDistanceCondition.
     *
     * @param npcId       the npc id used in logging
     * @param npcSupplier the supplier for the npc
     * @param distance    the maximal distance between player and npc
     */
    public NPCDistanceCondition(final String npcId, final Supplier<BQNPCAdapter> npcSupplier, final VariableNumber distance) {
        this.npcId = npcId;
        this.npcSupplier = npcSupplier;
        this.distance = distance;
    }

    @Override
    public boolean check(final Profile profile) throws QuestRuntimeException {
        final Location npcLocation = NPCUtil.getNPC(npcSupplier, npcId).getLocation();
        final Player player = profile.getOnlineProfile().get().getPlayer();
        if (!player.getWorld().equals(npcLocation.getWorld())) {
            return false;
        }
        final double distance = this.distance.getValue(profile).doubleValue();
        return npcLocation.distanceSquared(player.getLocation()) <= distance * distance;
    }
}
