package org.betonquest.betonquest.compatibility.npc.citizens.objective;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.MobKillNotifier.MobKilledEvent;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
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
     * @param instruction the user-provided instruction
     * @throws QuestException when the instruction cannot be parsed or is invalid
     */
    public NPCKillObjective(final Instruction instruction) throws QuestException {
        super(instruction, "mobs_to_kill");
        final Instruction npcInstruction = instruction.getID(NpcID::new).getInstruction();
        if (!"citizens".equals(npcInstruction.getPart(0))) {
            throw new QuestException("Cannot use non-Citizens NPC ID!");
        }
        final String argument = npcInstruction.getPart(1);
        if (npcInstruction.hasArgument("byName")) {
            predicate = npc -> argument.equals(npc.getName());
        } else {
            final int npcId = npcInstruction.getInt(argument, -1);
            predicate = npc -> npcId == npc.getId();
        }
        targetAmount = instruction.get(instruction.getOptional("amount", "1"), VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
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
