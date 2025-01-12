package org.betonquest.betonquest.compatibility.citizens.objective;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.MobKillNotifier.MobKilledEvent;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * Player has to kill an NPC.
 */
public class NPCKillObjective extends CountingObjective implements Listener {
    /**
     * The NPC id.
     */
    private final int npcId;

    /**
     * Create a new Citizens NPC kill objective.
     *
     * @param instruction the user-provided instruction
     * @throws QuestException when the instruction cannot be parsed or is invalid
     */
    public NPCKillObjective(final Instruction instruction) throws QuestException {
        super(instruction, "mobs_to_kill");
        npcId = instruction.getInt();
        if (npcId < 0) {
            throw new QuestException("NPC ID cannot be less than 0");
        }
        targetAmount = instruction.getVarNum(instruction.getOptional("amount", "1"), VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
    }

    /**
     * Handles the NPC death.
     *
     * @param event the event to listen
     */
    @EventHandler(ignoreCancelled = true)
    public void onNpcKill(final MobKilledEvent event) {
        final NPC npc = CitizensAPI.getNPCRegistry().getNPC(event.getEntity());
        if (npc == null || npc.getId() != npcId) {
            return;
        }
        final OnlineProfile onlineProfile = event.getProfile().getOnlineProfile().get();
        if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
            getCountingData(onlineProfile).progress();
            completeIfDoneOrNotify(onlineProfile);
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
}
