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
package pl.betoncraft.betonquest.events;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.util.ArrayList;

/**
 * Allows for running multiple events with one instruction string.
 *
 * @author Jakub Sapalski
 */
public class RunEvent extends QuestEvent {

    ArrayList<QuestEvent> internalEvents = new ArrayList<>();

    public RunEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        staticness = false;
        persistent = false;
        String[] parts = instruction.getInstruction().substring(3).trim().split(" ");
        if (parts.length < 1) {
            throw new InstructionParseException("Not enough arguments");
        }
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.startsWith("^")) {
                if (builder.length() != 0) {
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
    private QuestEvent createEvent(String instruction) throws InstructionParseException {
        String[] parts = instruction.split(" ");
        if (parts.length < 1) {
            throw new InstructionParseException("Not enough arguments in internal event");
        }
        Class<? extends QuestEvent> eventClass = BetonQuest.getInstance().getEventClass(parts[0]);
        if (eventClass == null) {
            // if it's null then there is no such type registered, log an error
            throw new InstructionParseException("Event type " + parts[0] + " is not registered, check if it's"
                    + " spelled correctly in internal event");
        }
        try {
            return eventClass.getConstructor(Instruction.class).newInstance(
                    new Instruction(this.instruction.getPackage(), null, instruction));
        } catch (Exception e) {
            if (e.getCause() instanceof InstructionParseException) {
                throw new InstructionParseException("Error in internal event: " + e.getCause().getMessage(), e);
            } else {
                LogUtils.logThrowableReport(e);
            }
        }
        return null;

    }

    @Override
    public void run(String playerID) throws QuestRuntimeException {
        for (QuestEvent event : internalEvents) {
            event.run(playerID);
        }
    }
}
