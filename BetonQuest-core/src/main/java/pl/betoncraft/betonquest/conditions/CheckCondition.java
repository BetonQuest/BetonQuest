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
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.util.ArrayList;

/**
 * Allows for checking multiple conditions with one instruction string.
 *
 * @author Jakub Sapalski
 */
public class CheckCondition extends Condition {

    ArrayList<Condition> internalConditions = new ArrayList<>();

    public CheckCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        String[] parts = instruction.getInstruction().substring(5).trim().split(" ");
        if (parts.length < 1) {
            throw new InstructionParseException("Not enough arguments");
        }
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.startsWith("^")) {
                if (builder.length() != 0) {
                    internalConditions.add(createCondition(builder.toString().trim()));
                    builder = new StringBuilder();
                }
                builder.append(part.substring(1) + " ");
            } else {
                builder.append(part + " ");
            }
        }
        internalConditions.add(createCondition(builder.toString().trim()));
    }

    /**
     * Constructs a condition with given instruction and returns it.
     */
    private Condition createCondition(String instruction) throws InstructionParseException {
        String[] parts = instruction.split(" ");
        if (parts.length < 1) {
            throw new InstructionParseException("Not enough arguments in internal condition");
        }
        Class<? extends Condition> conditionClass = BetonQuest.getInstance().getConditionClass(parts[0]);
        if (conditionClass == null) {
            // if it's null then there is no such type registered, log an error
            throw new InstructionParseException("Condition type " + parts[0] + " is not registered, check if it's"
                    + " spelled correctly in internal condition");
        }
        try {
            return conditionClass.getConstructor(Instruction.class).newInstance(
                    new Instruction(this.instruction.getPackage(), null, instruction));
        } catch (Exception e) {
            if (e.getCause() instanceof InstructionParseException) {
                throw new InstructionParseException("Error in internal condition: " + e.getCause().getMessage(), e);
            } else {
                LogUtils.logThrowableReport(e);
            }
        }
        return null;

    }

    @Override
    public boolean check(String playerID) throws QuestRuntimeException {
        for (Condition condition : internalConditions) {
            if (!condition.check(playerID))
                return false;
        }
        return true;
    }
}
