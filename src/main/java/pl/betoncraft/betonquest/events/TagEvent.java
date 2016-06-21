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
package pl.betoncraft.betonquest.events;

import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Adds or removes tags from the player
 * 
 * @author Jakub Sapalski
 */
public class TagEvent extends QuestEvent {

	private final String[] tags;
	private final boolean add;

	public TagEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		persistent = true;
		String[] parts = instructions.split(" ");
		if (parts.length < 3) {
			throw new InstructionParseException("Not enough arguments");
		}
		add = parts[1].equalsIgnoreCase("add");
		tags = parts[2].split(",");
		for (int i = 0; i < tags.length; i++) {
			tags[i] = Utils.addPackage(packName, tags[i]);
		}
	}

	@Override
	public void run(final String playerID) {
		if (PlayerConverter.getPlayer(playerID) != null) {
			PlayerData playerData = BetonQuest.getInstance().getPlayerData(playerID);
			if (add) {
				for (String tag : tags) {
					playerData.addTag(tag);
				}
			} else {
				for (String tag : tags) {
					playerData.removeTag(tag);
				}
			}
		} else {
			new BukkitRunnable() {
				@Override
				public void run() {
					PlayerData playerData = new PlayerData(playerID);
					if (add) {
						for (String tag : tags) {
							playerData.addTag(tag);
						}
					} else {
						for (String tag : tags) {
							playerData.removeTag(tag);
						}
					}
				}
			}.runTaskAsynchronously(BetonQuest.getInstance());
		}
	}
}
