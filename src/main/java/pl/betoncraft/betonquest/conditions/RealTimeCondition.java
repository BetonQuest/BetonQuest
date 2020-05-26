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

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

import java.util.Calendar;
import java.util.Date;

/**
 * A condition which checks if the current time is in the specified period
 * <p>
 * Created by Jonas Blocher on 27.11.2017.
 */
public class RealTimeCondition extends Condition {

    private final int hoursMin;
    private final int minutesMin;
    private final int hoursMax;
    private final int minutesMax;

    public RealTimeCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        super.staticness = true;
        super.persistent = true;
        String[] theTime = instruction.next().split("-");
        if (theTime.length != 2) {
            throw new InstructionParseException("Wrong time format");
        }
        try {
            String[] timeMin = theTime[0].split(":");
            String[] timeMax = theTime[1].split(":");
            if (timeMin.length != 2 || timeMax.length != 2) {
                throw new InstructionParseException("Could not parse time");
            }
            hoursMin = Integer.parseInt(timeMin[0]);
            minutesMin = Integer.parseInt(timeMin[1]);
            hoursMax = Integer.parseInt(timeMax[0]);
            minutesMax = Integer.parseInt(timeMax[1]);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse time", e);
        }
        if (hoursMax < 0 || hoursMax > 23) throw new InstructionParseException("Could not parse time");
        if (hoursMin < 0 || hoursMin > 23) throw new InstructionParseException("Could not parse time");
        if (minutesMax < 0 || minutesMax > 59) throw new InstructionParseException("Could not parse time");
        if (minutesMin < 0 || minutesMin > 59) throw new InstructionParseException("Could not parse time");
        if (hoursMin == hoursMax && minutesMin == minutesMax)
            throw new InstructionParseException("min and max time must be different");
    }


    @Override
    public boolean check(String playerID) throws QuestRuntimeException {
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();
        Date startTime = atTime(cal, hoursMin, minutesMin);
        Date endTime = atTime(cal, hoursMax, minutesMax);
        if (startTime.before(endTime)) {
            return now.after(startTime) && now.before(endTime);
        } else {
            return now.after(startTime) || now.before(endTime);
        }
    }

    private Date atTime(Calendar day, int hours, int minutes) {
        day.setLenient(false);
        day.set(Calendar.HOUR_OF_DAY, hours);
        day.set(Calendar.HOUR, hours < 12 ? hours : hours - 12);
        day.set(Calendar.AM_PM, hours < 12 ? Calendar.AM : Calendar.PM);
        day.set(Calendar.MINUTE, minutes);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MILLISECOND, 0);
        return day.getTime();
    }
}
