package org.betonquest.betonquest.objectives;

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
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Requires the player to join the server.
 */
@SuppressWarnings("PMD.CommentRequired")
public class LoginObjective extends Objective implements Listener {

    public LoginObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onJoin(final PlayerJoinEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(playerID) && checkConditions(playerID)) {
            completeObjective(playerID);
        }
    }

    @Override
    public void start() {
        // Empty
    }

    @Override
    public void close() {
        HandlerList.unregisterAll(this);
        super.close();
    }

    @Override
    public void stop() {
        // Empty
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
