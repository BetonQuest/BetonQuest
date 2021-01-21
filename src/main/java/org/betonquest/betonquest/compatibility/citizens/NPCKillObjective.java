package org.betonquest.betonquest.compatibility.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.MobKillNotifier.MobKilledEvent;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * Player has to kill an NPC
 */
@SuppressWarnings("PMD.CommentRequired")
public class NPCKillObjective extends Objective implements Listener {

    private final int npcId;
    private final int amount;

    public NPCKillObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = NPCData.class;
        npcId = instruction.getInt();
        if (npcId < 0) {
            throw new InstructionParseException("NPC ID cannot be less than 0");
        }
        amount = instruction.getInt(instruction.getOptional("amount"), 1);
        if (amount <= 0) {
            throw new InstructionParseException("Amount cannot be less than 1");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onNpcKill(final MobKilledEvent event) {
        final NPC npc = CitizensAPI.getNPCRegistry().getNPC(event.getEntity());
        if (npc == null) {
            return;
        }
        if (npc.getId() != npcId) {
            return;
        }
        final String playerID = PlayerConverter.getID(event.getPlayer());
        final NPCData playerData = (NPCData) dataMap.get(playerID);
        if (containsPlayer(playerID) && checkConditions(playerID)) {
            playerData.kill();
            if (playerData.killed()) {
                completeObjective(playerID);
            }
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return Integer.toString(amount);
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        if ("left".equalsIgnoreCase(name)) {
            return Integer.toString(amount - ((NPCData) dataMap.get(playerID)).getAmount());
        } else if ("amount".equalsIgnoreCase(name)) {
            return Integer.toString(((NPCData) dataMap.get(playerID)).getAmount());
        }
        return "";
    }

    public static class NPCData extends ObjectiveData {

        private int amount;

        public NPCData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        private void kill() {
            amount--;
            update();
        }

        private boolean killed() {
            return amount <= 0;
        }

        private int getAmount() {
            return amount;
        }

    }

}
