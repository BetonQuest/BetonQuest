/**
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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the player to type a password in chat.
 * 
 * @author Jakub Sapalski
 */
public class PasswordObjective extends Objective implements Listener {

	private final String regex;
	private final boolean ignoreCase;

	public PasswordObjective(String packName, String label, String instructions) throws InstructionParseException {
		super(packName, label, instructions);
		template = ObjectiveData.class;
		String[] parts = instructions.split(" ");
		if (parts.length < 2) {
			throw new InstructionParseException("Not enough arguments");
		}
		regex = parts[1].replace('_', ' ');
		ignoreCase = parts.length > 2 && parts[2].equalsIgnoreCase("ignoreCase");
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onChat(AsyncPlayerChatEvent event) {
		final String playerID = PlayerConverter.getID(event.getPlayer());
		if (containsPlayer(playerID)) {
			String prefix = Config.getMessage(BetonQuest.getInstance().getPlayerData(playerID).getLanguage(),
					"password");
			if (event.getMessage().startsWith(prefix)) {
				event.setCancelled(true);
				String password = event.getMessage().substring(prefix.length());
				if (ignoreCase) {
					if (password.toLowerCase().matches(regex) && checkConditions(playerID))
						new BukkitRunnable() {
							@Override
							public void run() {
								completeObjective(playerID);
							}
						}.runTask(BetonQuest.getInstance());
				} else {
					if (password.matches(regex) && checkConditions(playerID))
						new BukkitRunnable() {
							@Override
							public void run() {
								completeObjective(playerID);
							}
						}.runTask(BetonQuest.getInstance());
				}
			}
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
