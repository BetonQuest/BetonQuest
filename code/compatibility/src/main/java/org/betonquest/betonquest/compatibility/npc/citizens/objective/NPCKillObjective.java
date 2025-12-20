package org.betonquest.betonquest.compatibility.npc.citizens.objective;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.MobKillNotifier.MobKilledEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Player has to kill an NPC.
 */
public class NPCKillObjective extends CountingObjective implements Listener {

    /**
     * Source Registry of NPCs to use.
     */
    private final NPCRegistry registry;

    /**
     * Tests if the id matches the NPC.
     */
    private final Variable<NpcID> npcID;

    /**
     * Create a new Citizens NPC kill objective.
     *
     * @param instruction  the user-provided instruction
     * @param registry     the registry of NPCs to use
     * @param targetAmount the amount of NPCs to kill
     * @param npcID        the npc id
     * @throws QuestException when the instruction cannot be parsed or is invalid
     */
    public NPCKillObjective(final Instruction instruction, final NPCRegistry registry, final Variable<Number> targetAmount,
                            final Variable<NpcID> npcID) throws QuestException {
        super(instruction, targetAmount, "mobs_to_kill");
        this.registry = registry;
        this.npcID = npcID;
    }

    /**
     * Handles the NPC death.
     *
     * @param event the event to listen
     */
    @EventHandler(ignoreCancelled = true)
    public void onNpcKill(final MobKilledEvent event) {
        final NPC npc = registry.getNPC(event.getEntity());
        if (npc == null) {
            return;
        }
        final Profile profile = event.getProfile();
        qeHandler.handle(() -> {
            final Instruction npcInstruction = npcID.getValue(profile).getInstruction();
            final String argument = npcInstruction.getPart(1);
            if (npcInstruction.hasArgument("byName")) {
                final String resolvedName = npcInstruction.get(argument, Argument.STRING).getValue(profile);
                if (!resolvedName.equals(npc.getName())) {
                    return;
                }
            } else {
                final int resolvedId = npcInstruction.get(argument, Argument.NUMBER_NOT_LESS_THAN_ONE).getValue(profile).intValue();
                if (resolvedId != npc.getId()) {
                    return;
                }
            }
            if (containsPlayer(profile) && checkConditions(profile)) {
                getCountingData(profile).progress();
                completeIfDoneOrNotify(profile);
            }
        });
    }
}
