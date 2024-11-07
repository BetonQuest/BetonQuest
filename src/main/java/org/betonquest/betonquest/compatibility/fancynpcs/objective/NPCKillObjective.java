package org.betonquest.betonquest.compatibility.fancynpcs.objective;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.MobKillNotifier.MobKilledEvent;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.utils.Utils;
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
    private final String npcId;

    /**
     * Create a new FancyNpcs NPC kill objective.
     *
     * @param instruction the user-provided instruction
     * @throws InstructionParseException when the instruction cannot be parsed or is invalid
     */
    public NPCKillObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "mobs_to_kill");
        npcId = instruction.next();
        if (!Utils.isUUID(npcId)) {
            throw new InstructionParseException("NPC ID isn't a valid UUID");
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
        final Npc npc = FancyNpcsPlugin.get().getNpcManager().getNpcById(npcId);
        if (npc == null || !npc.getData().getId().equals(npcId)) {
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
