package org.betonquest.betonquest.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.NpcInteractEvent;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.objective.EntityInteractObjective.Interaction;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import static org.betonquest.betonquest.objective.EntityInteractObjective.Interaction.ANY;
import static org.betonquest.betonquest.objective.EntityInteractObjective.Interaction.RIGHT;

/**
 * An objective that requires the player to interact with a specific NPC.
 */
public class NpcInteractObjective extends Objective implements Listener {
    /**
     * The ID of the NPC to interact with.
     */
    protected final NpcID npcId;

    /**
     * Whether to cancel the interaction with the NPC.
     */
    private final boolean cancel;

    /**
     * The type of interaction with the NPC.
     */
    private final Interaction interactionType;

    /**
     * Creates a new NPCInteractObjective from the given instruction.
     *
     * @param instruction the user-provided instruction
     * @throws QuestException if the instruction is invalid
     */
    public NpcInteractObjective(final Instruction instruction) throws QuestException {
        super(instruction);
        this.npcId = instruction.getID(NpcID::new);
        cancel = instruction.hasArgument("cancel");
        interactionType = instruction.getEnum(instruction.getOptional("interaction"), Interaction.class, RIGHT);
    }

    /**
     * Handles npc interact events and completes the objective on match.
     *
     * @param event the event npc interact event
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onNPCLeftClick(final NpcInteractEvent event) {
        if (event.getInteraction() != ANY && event.getInteraction() != interactionType
                || !npcId.getInstruction().toString().equals(event.getNpcIdentifier())) {
            return;
        }

        final Profile profile = event.getProfile();
        if (containsPlayer(profile) && checkConditions(profile)) {
            if (cancel) {
                event.setCancelled(true);
            }
            completeObjective(profile);
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
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
