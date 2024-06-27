package org.betonquest.betonquest.compatibility.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.entity.Player;

/**
 * Checks if the player is close to a npc
 * <p>
 * Created on 30.09.2018.
 */
@SuppressWarnings("PMD.CommentRequired")
public class NPCDistanceCondition extends Condition {
    private final int npcId;

    private final VariableNumber distance;

    public NPCDistanceCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        npcId = instruction.getInt();
        if (npcId < 0) {
            throw new InstructionParseException("NPC ID cannot be less than 0");
        }
        distance = instruction.getVarNum();
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
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
