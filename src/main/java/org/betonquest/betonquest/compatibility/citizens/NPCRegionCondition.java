package org.betonquest.betonquest.compatibility.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.compatibility.worldguard.WorldGuardIntegrator;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Checks if a npc is inside a region
 * <p>
 * Created on 01.10.2018.
 */
@SuppressWarnings("PMD.CommentRequired")
public class NPCRegionCondition extends Condition {

    private final int npcId;
    private final String region;

    public NPCRegionCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        super.persistent = true;
        super.staticness = true;
        npcId = instruction.getInt();
        if (npcId < 0) {
            throw new InstructionParseException("NPC ID cannot be less than 0");
        }
        region = instruction.next();
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        return WorldGuardIntegrator.isInsideRegion(npc.getEntity().getLocation(), region);
    }
}
