package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the player to leave the server.
 */
public class LogoutObjective extends Objective implements Listener {

    public LogoutObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onQuit(final PlayerQuitEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(playerID) && checkConditions(playerID)) {
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
