package org.betonquest.betonquest.compatibility.citizens.objective;

import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.objectives.EntityInteractObjective.Interaction;
import org.betonquest.betonquest.utils.PlayerConverter;
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
    private final int npcId;

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
    public NPCInteractObjective(final Instruction instruction) throws QuestException {
        super(instruction);
        npcId = instruction.getInt();
        if (npcId < 0) {
            throw new QuestException("ID cannot be negative");
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
    public void onNPCRightClick(final NPCRightClickEvent event) {
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
    public void onNPCLeftClick(final NPCLeftClickEvent event) {
        if (interactionType.equals(LEFT) || interactionType.equals(ANY)) {
            onNPCClick(event);
        }
    }

    private void onNPCClick(final NPCClickEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getClicker());
        if (event.getNPC().getId() != npcId || !containsPlayer(onlineProfile)) {
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
