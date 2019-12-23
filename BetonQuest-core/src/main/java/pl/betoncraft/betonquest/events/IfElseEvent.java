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
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.id.EventID;

/**
 * Runs one or another event, depending of the condition outcome.
 *
 * @author Jakub Sapalski
 */
public class IfElseEvent extends QuestEvent {

    private ConditionID condition;
    private EventID event;
    private EventID elseEvent;

    public IfElseEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        condition = instruction.getCondition();
        event = instruction.getEvent();
        if (!instruction.next().equalsIgnoreCase("else")) {
            throw new InstructionParseException("Missing 'else' keyword");
        }
        elseEvent = instruction.getEvent();
    }

    @Override
    public void run(String playerID) {
        if (BetonQuest.condition(playerID, condition)) {
            BetonQuest.event(playerID, event);
        } else {
            BetonQuest.event(playerID, elseEvent);
        }
    }

}
