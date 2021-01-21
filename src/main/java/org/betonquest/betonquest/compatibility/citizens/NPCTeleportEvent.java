package org.betonquest.betonquest.compatibility.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Stop the NPC when he is walking and teleport hin to a given location
 */
@SuppressWarnings("PMD.CommentRequired")
public class NPCTeleportEvent extends QuestEvent implements Listener {
    private final CompoundLocation location;
    private final int npcId;

    public NPCTeleportEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        super.persistent = true;
        super.staticness = true;
        npcId = instruction.getInt();
        if (npcId < 0) {
            throw new InstructionParseException("NPC ID cannot be less than 0");
        }
        location = instruction.getLocation();
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc == null) {
            throw new QuestRuntimeException("NPC with ID " + npcId + " does not exist");
        }
        NPCMoveEvent.stopNPCMoving(npc);
        npc.getNavigator().cancelNavigation();
        if (npc.isSpawned()) {
            npc.teleport(location.getLocation(playerID), PlayerTeleportEvent.TeleportCause.PLUGIN);
        } else {
            npc.spawn(location.getLocation(playerID), SpawnReason.PLUGIN);
        }
        return null;
    }
}
