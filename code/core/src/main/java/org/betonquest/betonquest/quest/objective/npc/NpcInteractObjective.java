package org.betonquest.betonquest.quest.objective.npc;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.npc.NpcInteractEvent;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.betonquest.betonquest.quest.objective.interact.Interaction;

import static org.betonquest.betonquest.quest.objective.interact.Interaction.ANY;

/**
 * An objective that requires the player to interact with a specific NPC.
 */
public class NpcInteractObjective extends DefaultObjective {

    /**
     * The ID of the NPC to interact with.
     */
    private final Argument<NpcID> npcId;

    /**
     * Whether to cancel the interaction with the NPC.
     */
    private final FlagArgument<Boolean> cancel;

    /**
     * The type of interaction with the NPC.
     */
    private final Argument<Interaction> interactionType;

    /**
     * Creates a new NPCInteractObjective from the given instruction.
     *
     * @param service         the objective factory service
     * @param npcId           the ID of the NPC to interact with
     * @param cancel          whether to cancel the interaction with the NPC
     * @param interactionType the type of interaction with the NPC
     * @throws QuestException if the instruction is invalid
     */
    public NpcInteractObjective(final ObjectiveFactoryService service, final Argument<NpcID> npcId,
                                final FlagArgument<Boolean> cancel, final Argument<Interaction> interactionType) throws QuestException {
        super(service);
        this.npcId = npcId;
        this.cancel = cancel;
        this.interactionType = interactionType;
    }

    /**
     * Handles npc interact events and completes the objective on match.
     *
     * @param event   the event npc interact event
     * @param profile the profile of the player interacting with the NPC
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onNPCLeftClick(final NpcInteractEvent event, final Profile profile) throws QuestException {
        if (event.getInteraction() != ANY && event.getInteraction() != interactionType.getValue(profile)) {
            return;
        }

        if (event.getNpcIdentifier().contains(npcId.getValue(profile))) {
            if (cancel.getValue(profile).orElse(false)) {
                event.setCancelled(true);
            }
            getService().complete(profile);
        }
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
