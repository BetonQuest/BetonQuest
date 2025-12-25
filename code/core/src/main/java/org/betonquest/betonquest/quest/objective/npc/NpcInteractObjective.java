package org.betonquest.betonquest.quest.objective.npc;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.npc.NpcInteractEvent;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.quest.objective.interact.Interaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static org.betonquest.betonquest.quest.objective.interact.Interaction.ANY;

/**
 * An objective that requires the player to interact with a specific NPC.
 */
public class NpcInteractObjective extends Objective implements Listener {

    /**
     * The ID of the NPC to interact with.
     */
    private final Argument<NpcID> npcId;

    /**
     * Whether to cancel the interaction with the NPC.
     */
    private final boolean cancel;

    /**
     * The type of interaction with the NPC.
     */
    private final Argument<Interaction> interactionType;

    /**
     * Creates a new NPCInteractObjective from the given instruction.
     *
     * @param instruction     the user-provided instruction
     * @param npcId           the ID of the NPC to interact with
     * @param cancel          whether to cancel the interaction with the NPC
     * @param interactionType the type of interaction with the NPC
     * @throws QuestException if the instruction is invalid
     */
    public NpcInteractObjective(final Instruction instruction, final Argument<NpcID> npcId,
                                final boolean cancel, final Argument<Interaction> interactionType) throws QuestException {
        super(instruction);
        this.npcId = npcId;
        this.cancel = cancel;
        this.interactionType = interactionType;
    }

    /**
     * Handles npc interact events and completes the objective on match.
     *
     * @param event the event npc interact event
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onNPCLeftClick(final NpcInteractEvent event) {
        final Profile profile = event.getProfile();
        if (!containsPlayer(profile)) {
            return;
        }

        qeHandler.handle(() -> {
            if (event.getInteraction() != ANY && event.getInteraction() != interactionType.getValue(profile)) {
                return;
            }

            if (event.getNpcIdentifier().contains(npcId.getValue(profile))
                    && checkConditions(profile)) {
                if (cancel) {
                    event.setCancelled(true);
                }
                completeObjective(profile);
            }
        });
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
