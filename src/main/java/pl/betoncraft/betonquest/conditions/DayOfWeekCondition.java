/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.Condition;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.util.Calendar;

/**
 *
 * Created by Jonas Blocher on 27.11.2017.
 */
public class DayOfWeekCondition extends Condition {

    private final DayOfWeek day;

    public DayOfWeekCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        super.staticness = true;
        super.persistent = true;
        String dayString = instruction.next();
        DayOfWeek d;
        try {
            d = DayOfWeek.of(Integer.parseInt(dayString));
        } catch (DateTimeException e) {
            throw new InstructionParseException(dayString + " is not a valid day of a week");
        } catch (NumberFormatException NFE) {
            try {
                d = DayOfWeek.valueOf(dayString.toUpperCase());
            } catch (IllegalArgumentException IAE) {
                throw new InstructionParseException(dayString + " is not a valid day of a week");
            }
        }
        this.day = d;
    }

    @Override
    public boolean check(String playerID) throws QuestRuntimeException {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        day = day == 1 ? 7: day - 1;
        //As calendar.get(Calendar.DAY_OF_WEEK) returns 1 on sunday, 2 oin monday, and so on this has to be fixed
        return day == this.day.getValue();
    }
}
