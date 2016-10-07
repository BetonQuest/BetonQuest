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

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.EventID;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;

/**
 * Folder event is a collection of other events, that can be run after a delay
 * and the events can be randomly chosen to run or not
 * 
 * @author Jakub Sapalski
 */
public class FolderEvent extends QuestEvent {

	public VariableNumber delay;
	public VariableNumber random;
	public EventID[] events;

	public FolderEvent(final Instruction instruction) throws InstructionParseException {
		super(instruction);
		staticness = true;
		persistent = true;
		events = instruction.getList(e -> instruction.getEvent(e)).toArray(new EventID[0]);
		delay = instruction.getVarNum(instruction.getOptional("delay"));
		random = instruction.getVarNum(instruction.getOptional("random"));
	}

	@Override
	public void run(final String playerID) throws QuestRuntimeException {
		final ArrayList<EventID> chosenList = new ArrayList<>();
		// choose randomly which events should be fired
		int randomInt = random != null ? random.getInt(playerID) : 0;
		if (randomInt > 0 && randomInt <= events.length) {
			// copy events into the modifyable ArrayList
			ArrayList<EventID> eventsList = new ArrayList<>();
			for (EventID event : events) {
				eventsList.add(event);
			}
			// remove choosen events from that ArrayList and place them in a new
			// list
			for (int i = randomInt; i > 0; i--) {
				int chosen = new Random().nextInt(eventsList.size());
				chosenList.add(eventsList.remove(chosen));
			}
		} else {
			// add all events if it's not random
			for (EventID event : events) {
				chosenList.add(event);
			}
		}
		int seconds = (delay == null) ? 0 : delay.getInt(playerID);
		new BukkitRunnable() {
			@Override
			public void run() {
				for (EventID event : chosenList) {
					BetonQuest.event(playerID, event);
				}
			} // 20 ticks is a second
		}.runTaskLater(BetonQuest.getInstance(), seconds * 20);
	}

}
