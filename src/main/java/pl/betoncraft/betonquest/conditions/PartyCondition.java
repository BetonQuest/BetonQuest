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
package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.ArrayList;

/**
 * Checks the conditions for the whole party (including the player that started
 * the checking)
 *
 * @author Jakub Sapalski
 */
public class PartyCondition extends Condition {

    private VariableNumber range;
    private ConditionID[] conditions;
    private ConditionID[] everyone;
    private ConditionID[] anyone;
    private VariableNumber count;

    public PartyCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        range = instruction.getVarNum();
        conditions = instruction.getList(e -> instruction.getCondition(e)).toArray(new ConditionID[0]);
        everyone = instruction.getList(instruction.getOptional("every"), e -> instruction.getCondition(e)).toArray(new ConditionID[0]);
        anyone = instruction.getList(instruction.getOptional("any"), e -> instruction.getCondition(e)).toArray(new ConditionID[0]);
        count = instruction.getVarNum(instruction.getOptional("count"));
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        // get the party
        final ArrayList<String> members = Utils.getParty(playerID, range.getDouble(playerID), instruction.getPackage().getName(), conditions);
        // check every condition against every player - all of them must meet those conditions
        for (final ConditionID condition : everyone) {
            for (final String memberID : members) {
                // if this condition wasn't met by someone, return false
                if (!BetonQuest.condition(memberID, condition)) {
                    return false;
                }
            }
        }
        // check every condition against every player - at least one of them must meet each of those
        for (final ConditionID condition : anyone) {
            boolean met = false;
            for (final String memberID : members) {
                if (BetonQuest.condition(memberID, condition)) {
                    met = true;
                    break;
                }
            }
            // if this condition wasn't met by anyone, return false
            if (!met) {
                return false;
            }
        }
        // if the count is more than 0, we need to check if there are more
        // players in the party than required minimum
        final int pCount = count == null ? 0 : count.getInt(playerID);
        return pCount <= 0 || members.size() >= pCount;
    }

}
