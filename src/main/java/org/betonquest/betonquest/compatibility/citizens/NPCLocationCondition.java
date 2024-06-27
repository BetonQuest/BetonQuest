package org.betonquest.betonquest.compatibility.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.bukkit.Location;

/**
 * Checks if a npc is at a specific location
 * <p>
 * Created on 01.10.2018.
 */
@SuppressWarnings("PMD.CommentRequired")
public class NPCLocationCondition extends Condition {
    private final int npcId;

    private final VariableLocation location;

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
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc == null) {
            throw new QuestRuntimeException("NPC with ID " + npcId + " does not exist");
        }
        final Location location = this.location.getValue(profile);
        if (!location.getWorld().equals(npc.getStoredLocation().getWorld())) {
            return false;
        }
        final double radius = this.radius.getValue(profile).doubleValue();
        return npc.getStoredLocation().distanceSquared(location) <= radius * radius;
    }
}
