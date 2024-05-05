package org.betonquest.betonquest.compatibility.npcs.abstractnpc.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.objectives.EntityInteractObjective.Interaction;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import static org.betonquest.betonquest.objectives.EntityInteractObjective.Interaction.ANY;
import static org.betonquest.betonquest.objectives.EntityInteractObjective.Interaction.RIGHT;

/**
 * An objective that requires the player to interact with a specific NPC.
 */
public class NPCInteractObjective extends Objective implements Listener {
    /**
     * The ID of the NPC to interact with.
     */
    protected final String npcId;

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
        cancel = instruction.hasArgument("cancel");
        interactionType = instruction.getEnum(instruction.getOptional("interaction"), Interaction.class, RIGHT);
    }

    /**
     * Processes all checks and completes the objective.
     *
     * @param npcId       the String representation of the npc id that got clicked
     * @param interaction the kind of click
     * @param clicker     the player who clicked
     * @return if the triggering event should be cancelled
     */
    protected boolean onNPCClick(final String npcId, final Interaction interaction, final Player clicker) {
        if (interactionType != ANY && interactionType != interaction || !this.npcId.equals(npcId)) {
            return false;
        }
        final OnlineProfile onlineProfile = PlayerConverter.getID(clicker);
        if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
            completeObjective(onlineProfile);
            return cancel;
        }
        return false;
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
