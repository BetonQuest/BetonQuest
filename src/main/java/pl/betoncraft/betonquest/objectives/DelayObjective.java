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
import pl.betoncraft.betonquest.core.Objective;

public class DelayObjective extends Objective {
	
	private long stamp = -1;
	private BukkitTask runnable;

	public DelayObjective(String playerID, String instructions) {
		super(playerID, instructions);
		// if you don't define stamp: or delay: then objective will be completed immediately after creating, just like folder event
		for (String part : instructions.split(" ")) {
			if (part.contains("delay:")) {
				stamp = new Date().getTime() + (Integer.parseInt(part.substring(6)) * 1000 * 60);
			} else if (part.contains("stamp:")) {
				stamp = Long.parseLong(part.substring(6));
			}
		}
		final long finalStamp = stamp;
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				if (new Date().getTime() >= finalStamp && checkConditions()) {
					this.cancel();
					completeObjective();
				}
			}
		}.runTaskTimer(BetonQuest.getInstance(), 0, 20*60);
	}

	@Override
	public String getInstructions() {
		runnable.cancel();
		return "delay stamp:" + stamp + " " + events + " " + conditions + " tag:" + tag;
	}
}
