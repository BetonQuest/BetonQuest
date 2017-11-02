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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
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

	public PasswordObjective(Instruction instruction) throws InstructionParseException {
		super(instruction);
		template = ObjectiveData.class;
		regex = instruction.next().replace('_', ' ');
		ignoreCase = instruction.hasArgument("ignoreCase");
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onChat(AsyncPlayerChatEvent event) {
		if(chatInput(false, event.getPlayer(), event.getMessage())) {
			event.setCancelled(true);
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		if(chatInput(true, event.getPlayer(), event.getMessage())) {
			event.setCancelled(true);
		}
	}

	private boolean chatInput(boolean fromCommand, Player player, String message) {
		final String playerID = PlayerConverter.getID(player);
		if (containsPlayer(playerID)) {
			String prefix = Config.getMessage(BetonQuest.getInstance().getPlayerData(playerID).getLanguage(),
					"password");
			if (message.startsWith(prefix)) {
				String password = message.substring(prefix.length());
				if ((ignoreCase ? password.toLowerCase() : password).matches(regex) && checkConditions(playerID)) {
					new BukkitRunnable() {
						@Override
						public void run() {
							completeObjective(playerID);
						}
					}.runTask(BetonQuest.getInstance());
				}
                return !(fromCommand && prefix.isEmpty());
			}
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

}
