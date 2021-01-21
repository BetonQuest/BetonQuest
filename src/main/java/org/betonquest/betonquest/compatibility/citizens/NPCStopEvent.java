package org.betonquest.betonquest.compatibility.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.event.Listener;

/**
 * Stop the NPC when he is walking
 */
@SuppressWarnings("PMD.CommentRequired")
public class NPCStopEvent extends QuestEvent implements Listener {
    private final int npcId;

    public NPCStopEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        super.persistent = true;
        super.staticness = true;
        npcId = instruction.getInt();
        if (npcId < 0) {
            throw new InstructionParseException("NPC ID cannot be less than 0");
        }
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc == null) {
            throw new QuestRuntimeException("NPC with ID " + npcId + " does not exist");
        }
        NPCMoveEvent.stopNPCMoving(npc);
        npc.getNavigator().cancelNavigation();
        return null;
    }
}
