package pl.betoncraft.betonquest.compatibility.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

/**
 * Stop the NPC when he is walking
 */
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
        if (!npc.isSpawned()) {
            return null;
        }
        NPCMoveEvent.stopNPCMoving(npc);
        npc.getNavigator().cancelNavigation();
        return null;
    }
}
