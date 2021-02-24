package pl.betoncraft.betonquest.compatibility.citizens;


import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

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
        final Player player = PlayerConverter.getPlayer(playerID);
        if (!player.getWorld().equals(npc.getStoredLocation().getWorld())) {
            return false;
        }
        final double distance = this.distance.getDouble(playerID);
        return npc.getStoredLocation().distanceSquared(player.getLocation()) <= distance * distance;
    }
}
