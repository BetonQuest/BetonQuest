/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016 Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.events;

import java.util.ArrayList;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LogUtils;


/**
 * Allows for running multiple events with one instruction string.
 *
 * @author Jakub Sapalski
 */
public class RunEvent extends QuestEvent {

	ArrayList<QuestEvent> internalEvents = new ArrayList<>();

	public RunEvent(final Instruction instruction) throws InstructionParseException {
		super(instruction, false);
		staticness = false;
		persistent = false;
		final String[] parts = instruction.getInstruction().substring(3).trim().split(" ");
		if(parts.length < 1) {
			throw new InstructionParseException("Not enough arguments");
		}
		StringBuilder builder = new StringBuilder();
		for(final String part : parts) {
			if(part.startsWith("^")) {
				if(builder.length() != 0) {
					internalEvents.add(createEvent(builder.toString().trim()));
					builder = new StringBuilder();
				}
				builder.append(part.substring(1) + " ");
			} else {
				builder.append(part + " ");
			}
		}
		internalEvents.add(createEvent(builder.toString().trim()));
	}

	/**
	 * Constructs an event with given instruction and returns it.
	 */
	private QuestEvent createEvent(final String instruction) throws InstructionParseException {
		final String[] parts = instruction.split(" ");
		if(parts.length < 1) {
			throw new InstructionParseException("Not enough arguments in internal event");
		}
		final Class<? extends QuestEvent> eventClass = BetonQuest.getInstance().getEventClass(parts[0]);
		if(eventClass == null) {
			// if it's null then there is no such type registered, log an error
			throw new InstructionParseException("Event type " + parts[0] + " is not registered, check if it's" + " spelled correctly in internal event");
		}
		try {
			return eventClass.getConstructor(Instruction.class).newInstance(new Instruction(this.instruction.getPackage(), null, instruction));
		} catch(final Exception e) {
			if(e.getCause() instanceof InstructionParseException) {
				throw new InstructionParseException("Error in internal event: " + e.getCause().getMessage(), e);
			} else {
				LogUtils.logThrowableReport(e);
			}
		}
		return null;

	}

	@Override
	protected Void execute(final String playerID) throws QuestRuntimeException {
		for(final QuestEvent event : internalEvents) {
			event.handle(playerID);
		}
		return null;
	}
}
