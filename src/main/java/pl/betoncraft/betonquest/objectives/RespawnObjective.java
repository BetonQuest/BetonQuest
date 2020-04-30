/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.logging.Level;

public class RespawnObjective extends Objective implements Listener {

    private final LocationData location;

    public RespawnObjective(Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        location = instruction.getLocation(instruction.getOptional("location"));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRespawn(PlayerRespawnEvent event) {
        String playerID = PlayerConverter.getID(event.getPlayer());
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
}
