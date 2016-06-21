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
package pl.betoncraft.betonquest.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Superclass for all events. You need to extend it in order to create new
 * custom events.
 * <p/>
 * Registering your events is done through
 * {@link pl.betoncraft.betonquest.BetonQuest#registerEvents(String, Class<?
 * extends QuestEvent>) registerEvents} method.
 * 
 * @author Jakub Sapalski
 */
public abstract class QuestEvent {

	/**
	 * Stores instruction string for the event.
	 */
	protected final String instructions;
	/**
	 * Stores conditions that must be met when firing this event
	 */
	protected final String[] conditions;
	/**
	 * ConfigPackage in which this event is defined
	 */
	protected final ConfigPackage pack;
	/**
	 * Describes if the event is static
	 */
	protected boolean staticness = false;
	/**
	 * Describes if the event is persistent
	 */
	protected boolean persistent = false;

	/**
	 * Creates new instance of the event. The event should parse instruction
	 * string without doing anything else. If anything goes wrong, throw
	 * {@link InstructionParseException} with error message describing the
	 * problem.
	 * 
	 * @param packName
	 *            ID of the player this event is related to. It will be passed
	 *            at runtime, you only need to use it according to what your
	 *            event does.
	 * @param instructions
	 *            instruction string passed at runtime. You need to extract all
	 *            required data from it and display errors if there is anything
	 *            wrong.
	 */
	public QuestEvent(String packName, String instructions) throws InstructionParseException {
		this.instructions = instructions;
		this.pack = Config.getPackage(packName);
		String[] tempConditions1 = new String[] {};
		String[] tempConditions2 = new String[] {};
		String[] parts = instructions.split(" ");
		for (String part : parts) {
			if (part.startsWith("conditions:")) {
				tempConditions1 = part.substring(11).split(",");
			} else if (part.startsWith("condition:")) {
				tempConditions2 = part.substring(10).split(",");
			}
		}
		int length = tempConditions1.length + tempConditions2.length;
		conditions = new String[length];
		for (int i = 0; i < length; i++) {
			conditions[i] = (i >= tempConditions1.length) ? tempConditions2[i - tempConditions1.length]
					: tempConditions1[i];
		}
		for (int i = 0; i < conditions.length; i++) {
			conditions[i] = Utils.addPackage(pack.getName(), conditions[i]);
		}
	}

	/**
	 * This method should contain all logic for firing the event and use the
	 * data parsed by the constructor. When this method is called all the
	 * required data is present and parsed correctly.
	 * 
	 * @param playerID
	 *            ID of the player for whom the event will fire
	 */
	abstract public void run(String playerID) throws QuestRuntimeException;

	/**
	 * Fires an event for the player. The event conditions are checked, so it's
	 * not needed to check them explicitly.
	 * 
	 * @param playerID
	 *            ID of the player for whom the event will fire
	 */
	public final void fire(String playerID) throws QuestRuntimeException {
		if (playerID == null) {
			// handle static event
			if (!staticness) {
				Debug.info("Static event will be fired once for every player:");
				for (Player player : Bukkit.getOnlinePlayers()) {
					String ID = PlayerConverter.getID(player);
					for (String condition : conditions) {
						if (!BetonQuest.condition(ID, condition)) {
							Debug.info("  Event conditions were not met for player " + player.getName());
							continue;
						}
					}
					Debug.info("  Firing this static event for player " + player.getName());
					run(ID);
				}
			} else {
				run(null);
			}
		} else if (PlayerConverter.getPlayer(playerID) == null) {
			// handle persistent event
			if (!persistent) {
				Debug.info("Player " + playerID + " is offline, cannot fire event because it's not persistent.");
				return;
			}
			run(playerID);
		} else {
			// handle standard event
			for (String condition : conditions) {
				if (!BetonQuest.condition(playerID, condition)) {
					Debug.info("Event conditions were not met.");
					return;
				}
			}
			run(playerID);
		}
	}
}
