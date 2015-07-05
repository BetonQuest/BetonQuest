/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
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

import java.util.Date;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.core.InstructionParseException;

/**
 * Player has to wait specified amount of time. He may logout, the objective
 * will be completed as soon as the time is up and he logs in again.
 * 
 * @author Jakub Sapalski
 */
public class DelayObjective extends Objective {

    private final long delay;
    private BukkitTask runnable;

    public DelayObjective(String packName, String label, String instruction)
            throws InstructionParseException {
        super(packName, label, instruction);
        template = DelayData.class;
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            throw new InstructionParseException("Not enough arguments");
        }
        try {
            delay = Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse delay");
        }
        if (delay < 1) {
            throw new InstructionParseException("Delay cannot be less than 1");
        }
    }

    @Override
    public void start() {
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                for (String playerID : dataMap.keySet()) {
                    DelayData playerData = (DelayData) dataMap.get(playerID);
                    if (new Date().getTime() >= playerData.getTime() &&
                            checkConditions(playerID)) {
                        completeObjective(playerID);
                    }
                }
            }
        }.runTaskTimer(BetonQuest.getInstance(), 0, 20 * 10);
    }

    @Override
    public void stop() {
        if (runnable != null) runnable.cancel();
    }

    @Override
    public String getDefaultDataInstruction() {
        return Long.toString(new Date().getTime() + delay*1000*60);
    }
    
    public static class DelayData extends ObjectiveData {
        
        private final long timestamp;

        public DelayData(String instruction) {
            super(instruction);
            timestamp = Long.parseLong(instruction);
        }
        
        private long getTime() {
            return timestamp;
        }
        
    }
}
