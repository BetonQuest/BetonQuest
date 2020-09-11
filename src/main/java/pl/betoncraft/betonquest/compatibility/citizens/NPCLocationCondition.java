package pl.betoncraft.betonquest.compatibility.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LocationData;

/**
 * Checks if a npc is at a specific location
 * <p>
 * Created on 01.10.2018.
 */
public class NPCLocationCondition extends Condition {

    private final int npcId;
    private final LocationData location;
    private final VariableNumber radius;

    public NPCLocationCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        super.persistent = true;
        super.staticness = true;
        npcId = instruction.getInt();
        if (npcId < 0) {
            throw new InstructionParseException("NPC ID cannot be less than 0");
        }
        location = instruction.getLocation();
        radius = instruction.getVarNum();
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
        final Location location = this.location.getLocation(playerID);
        if (!location.getWorld().equals(npcEntity.getWorld())) {
            return false;
        }
        final double radius = this.radius.getDouble(playerID);
        return npcEntity.getLocation().distanceSquared(location) <= radius * radius;
    }
}
