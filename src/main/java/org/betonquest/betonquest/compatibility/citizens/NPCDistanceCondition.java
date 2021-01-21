package org.betonquest.betonquest.compatibility.citizens;


import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Entity;
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
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc == null) {
            throw new QuestRuntimeException("NPC with ID " + npcId + " does not exist");
        }
        final Entity npcEntity = npc.getEntity();
        if (npcEntity == null) {
            return false;
        }
        final Player player = PlayerConverter.getPlayer(playerID);
        if (!npcEntity.getWorld().equals(player.getWorld())) {
            return false;
        }
        final double distance = this.distance.getDouble(playerID);
        return npcEntity.getLocation().distanceSquared(player.getLocation()) <= distance * distance;
    }
}
