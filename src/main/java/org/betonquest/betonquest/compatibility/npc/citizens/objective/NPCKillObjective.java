package org.betonquest.betonquest.compatibility.npc.citizens.objective;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.MobKillNotifier.MobKilledEvent;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.function.Predicate;

/**
 * Player has to kill an NPC.
 */
public class NPCKillObjective extends CountingObjective implements Listener {
    /**
     * Tests if the id matches the NPC.
     */
    private final Predicate<NPC> predicate;

    /**
     * Create a new Citizens NPC kill objective.
     *
     * @param instruction  the user-provided instruction
     * @param targetAmount the amount of NPCs to kill
     * @param predicate    the predicate to test if the NPC is the right one
     * @throws QuestException when the instruction cannot be parsed or is invalid
     */
    public NPCKillObjective(final Instruction instruction, final VariableNumber targetAmount, final Predicate<NPC> predicate) throws QuestException {
        super(instruction, "mobs_to_kill");
        this.targetAmount = targetAmount;
        this.predicate = predicate;
    }

    /**
     * Handles the NPC death.
     *
     * @param event the event to listen
     */
    @EventHandler(ignoreCancelled = true)
    public void onNpcKill(final MobKilledEvent event) {
        final NPC npc = CitizensAPI.getNPCRegistry().getNPC(event.getEntity());
        if (npc == null || !predicate.test(npc)) {
            return;
        }
        final Profile profile = event.getProfile();
        if (containsPlayer(profile) && checkConditions(profile)) {
            getCountingData(profile).progress();
            completeIfDoneOrNotify(profile);
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
