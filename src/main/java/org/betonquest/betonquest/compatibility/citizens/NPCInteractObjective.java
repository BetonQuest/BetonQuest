package org.betonquest.betonquest.compatibility.citizens;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * Player has to right click the NPC
 */
@SuppressWarnings("PMD.CommentRequired")
public class NPCInteractObjective extends Objective implements Listener {

    private final int npcId;
    private final boolean cancel;

    public NPCInteractObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        npcId = instruction.getInt();
        if (npcId < 0) {
            throw new InstructionParseException("ID cannot be negative");
        }
        cancel = instruction.hasArgument("cancel");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onNPCClick(final NPCRightClickEvent event) {
        final String playerID = PlayerConverter.getID(event.getClicker());
        if (event.getNPC().getId() != npcId || !containsPlayer(playerID)) {
            return;
        }
        if (checkConditions(playerID)) {
            if (cancel) {
                event.setCancelled(true);
            }
            completeObjective(playerID);
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
    public String getProperty(final String name, final String playerID) {
        return "";
    }

}
