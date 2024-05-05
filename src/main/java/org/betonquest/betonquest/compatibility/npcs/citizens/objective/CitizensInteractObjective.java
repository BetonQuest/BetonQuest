package org.betonquest.betonquest.compatibility.npcs.citizens.objective;

import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.objective.NPCInteractObjective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import static org.betonquest.betonquest.objectives.EntityInteractObjective.Interaction.LEFT;
import static org.betonquest.betonquest.objectives.EntityInteractObjective.Interaction.RIGHT;

/**
 * Citizens implementation of {@link NPCInteractObjective}.
 */
public class CitizensInteractObjective extends NPCInteractObjective {
    /**
     * Creates a new CitizensInteractObjective from the given instruction.
     *
     * @param instruction the user-provided instruction
     * @throws InstructionParseException if the instruction is invalid
     */
    public CitizensInteractObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        try {
            final int parsedId = Integer.parseInt(npcId);
            if (parsedId < 0) {
                throw new InstructionParseException("The specified NPC ID was not a positive or zero integer");
            }
        } catch (final NumberFormatException e) {
            throw new InstructionParseException("The specified NPC ID was not a valid integer", e);
        }
    }

    /**
     * Handles RightClick events.
     *
     * @param event the event provided by the NPC plugin
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onNPCRightClick(final NPCRightClickEvent event) {
        final boolean cancel = onNPCClick(String.valueOf(event.getNPC().getId()), RIGHT, event.getClicker());
        if (cancel) {
            event.setCancelled(true);
        }
    }

    /**
     * Handles LeftClick events.
     *
     * @param event the event provided by the NPC plugin
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onNPCLeftClick(final NPCLeftClickEvent event) {
        final boolean cancel = onNPCClick(String.valueOf(event.getNPC().getId()), LEFT, event.getClicker());
        if (cancel) {
            event.setCancelled(true);
        }
    }
}
