package org.betonquest.betonquest.compatibility.fancynpcs.objective;

import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.objectives.EntityInteractObjective.Interaction;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import static org.betonquest.betonquest.objectives.EntityInteractObjective.Interaction.ANY;
import static org.betonquest.betonquest.objectives.EntityInteractObjective.Interaction.LEFT;
import static org.betonquest.betonquest.objectives.EntityInteractObjective.Interaction.RIGHT;

/**
 * An objective that requires the player to interact with a specific NPC.
 */

public class NPCInteractObjective extends Objective implements Listener {
    /**
     * The ID of the NPC to interact with.
     */
    private final String npcId;

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
     * @throws InstructionParseException if the instruction is invalid
     */
    public NPCInteractObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        npcId = instruction.next();
        if (!Utils.isUUID(npcId)) {
            throw new InstructionParseException("NPC ID isn't a valid UUID");
        }
        cancel = instruction.hasArgument("cancel");
        interactionType = instruction.getEnum(instruction.getOptional("interaction"), Interaction.class, RIGHT);
    }

    /**
     * Handles RightClick events.
     *
     * @param event the event provided by the NPC plugin
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onNPCRightClick(final NpcInteractEvent event) {
        if (event.getInteractionType() != ActionTrigger.RIGHT_CLICK) return;
        if (interactionType.equals(RIGHT) || interactionType.equals(ANY)) {
            onNPCClick(event);
        }
    }

    /**
     * Handles LeftClick events.
     *
     * @param event the event provided by the NPC plugin
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onNPCLeftClick(final NpcInteractEvent event) {
        if (event.getInteractionType() != ActionTrigger.LEFT_CLICK) return;
        if (interactionType.equals(LEFT) || interactionType.equals(ANY)) {
            onNPCClick(event);
        }
    }

    private void onNPCClick(final NpcInteractEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (!event.getNpc().getData().getId().equals(npcId) || !containsPlayer(onlineProfile)) {
            return;
        }
        if (checkConditions(onlineProfile)) {
            if (cancel) {
                event.setCancelled(true);
            }
            completeObjective(onlineProfile);
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
