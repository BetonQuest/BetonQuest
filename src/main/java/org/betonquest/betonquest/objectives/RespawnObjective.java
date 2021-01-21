package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.LogUtils;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.logging.Level;

@SuppressWarnings("PMD.CommentRequired")
public class RespawnObjective extends Objective implements Listener {

    private final CompoundLocation location;

    public RespawnObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        location = instruction.getLocation(instruction.getOptional("location"));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRespawn(final PlayerRespawnEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(playerID) && checkConditions(playerID)) {

            if (location != null) {
                try {
                    event.setRespawnLocation(location.getLocation(playerID));
                } catch (QuestRuntimeException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Error while handling '" + instruction.getID() + "' objective: \" + e.getMessage()");
                    LogUtils.logThrowable(e);
                }
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
