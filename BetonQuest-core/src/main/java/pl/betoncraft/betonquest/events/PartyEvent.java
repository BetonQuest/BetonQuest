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
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.id.EventID;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.ArrayList;

/**
 * Fires specified events for every player in the party
 *
 * @author Jakub Sapalski
 */
public class PartyEvent extends QuestEvent {

    private ConditionID[] conditions;
    private EventID[] events;
    private VariableNumber range;

    public PartyEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        range = instruction.getVarNum();
        conditions = instruction.getList(e -> instruction.getCondition(e)).toArray(new ConditionID[0]);
        events = instruction.getList(e -> instruction.getEvent(e)).toArray(new EventID[0]);
    }

    @Override
    public void run(String playerID) throws QuestRuntimeException {
        ArrayList<String> members = Utils.getParty(playerID, range.getDouble(playerID), instruction.getPackage()
                .getName(), conditions);
        for (String memberID : members) {
            for (EventID event : events) {
                BetonQuest.event(memberID, event);
            }
        }
    }

}
