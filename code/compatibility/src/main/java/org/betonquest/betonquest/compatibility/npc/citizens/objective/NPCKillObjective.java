package org.betonquest.betonquest.compatibility.npc.citizens.objective;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.MobKillNotifier.MobKilledEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;

/**
 * Player has to kill an NPC.
 */
public class NPCKillObjective extends CountingObjective {

    /**
     * Source Registry of NPCs to use.
     */
    private final NPCRegistry registry;

    /**
     * Tests if the id matches the NPC.
     */
    private final Argument<NpcID> npcID;

    /**
     * Create a new Citizens NPC kill objective.
     *
     * @param service      the objective factory service
     * @param registry     the registry of NPCs to use
     * @param targetAmount the amount of NPCs to kill
     * @param npcID        the npc id
     * @throws QuestException when the instruction cannot be parsed or is invalid
     */
    public NPCKillObjective(final ObjectiveService service, final NPCRegistry registry, final Argument<Number> targetAmount,
                            final Argument<NpcID> npcID) throws QuestException {
        super(service, targetAmount, "mobs_to_kill");
        this.registry = registry;
        this.npcID = npcID;
    }

    /**
     * Handles the NPC death.
     *
     * @param event   the event to listen
     * @param profile the player profile
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onNpcKill(final MobKilledEvent event, final Profile profile) throws QuestException {
        final NPC npc = registry.getNPC(event.getEntity());
        if (npc == null) {
            return;
        }
        final Instruction npcInstruction = npcID.getValue(profile).getInstruction();
        final String argument = npcInstruction.getPart(1);
        final boolean byName = npcInstruction.bool().getFlag("byName", true)
                .getValue(profile).orElse(false);
        if (byName) {
            final String resolvedName = npcInstruction.chainForArgument(argument).string().get().getValue(profile);
            if (!resolvedName.equals(npc.getName())) {
                return;
            }
        } else {
            final int resolvedId = npcInstruction.chainForArgument(argument).number().atLeast(1).get().getValue(profile).intValue();
            if (resolvedId != npc.getId()) {
                return;
            }
        }
        getCountingData(profile).progress();
        completeIfDoneOrNotify(profile);
    }
}
