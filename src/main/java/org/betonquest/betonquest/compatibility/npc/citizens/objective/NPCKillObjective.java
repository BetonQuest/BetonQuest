package org.betonquest.betonquest.compatibility.npc.citizens.objective;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.MobKillNotifier.MobKilledEvent;
import org.betonquest.betonquest.api.common.function.QuestBiPredicate;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * Player has to kill an NPC.
 */
public class NPCKillObjective extends CountingObjective implements Listener {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Tests if the id matches the NPC.
     */
    private final QuestBiPredicate<NPC, Profile> predicate;

    /**
     * Create a new Citizens NPC kill objective.
     *
     * @param instruction  the user-provided instruction
     * @param targetAmount the amount of NPCs to kill
     * @param log          the logger for this objective
     * @param predicate    the predicate to test if the NPC is the right one
     * @throws QuestException when the instruction cannot be parsed or is invalid
     */
    public NPCKillObjective(final Instruction instruction, final Variable<Number> targetAmount,
                            final BetonQuestLogger log, final QuestBiPredicate<NPC, Profile> predicate)
            throws QuestException {
        super(instruction, targetAmount, "mobs_to_kill");
        this.log = log;
        this.predicate = predicate;
    }

    /**
     * Handles the NPC death.
     *
     * @param event the event to listen
     */
    @EventHandler(ignoreCancelled = true)
    public void onNpcKill(final MobKilledEvent event) {
        final Profile profile = event.getProfile();
        final NPC npc = CitizensAPI.getNPCRegistry().getNPC(event.getEntity());
        try {
            if (npc == null || !predicate.test(npc, profile)) {
                return;
            }
        } catch (final QuestException e) {
            log.warn("Error while checking if NPC is the right one: " + e.getMessage(), e);
        }
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
