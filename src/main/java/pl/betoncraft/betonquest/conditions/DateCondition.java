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
package pl.betoncraft.betonquest.conditions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.Condition;

/**
 * Returns true if given date is matched
 * 
 * @author Archerymaister
 */
public class DateCondition extends Condition {

	private Date startDate;
	private Date endDate;

	public DateCondition(Instruction instruction) throws InstructionParseException {
		super(instruction);

		if(instruction.size() > 3){
			throw new InstructionParseException("Too many arguments!");
		}
		if(instruction.size() < 2){
			throw new InstructionParseException("not enough arguments!");
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		try {
			startDate = cleanDate(dateFormat.parse(instruction.getPart(1)));
			if(instruction.size() > 2) {
				endDate = cleanDate(dateFormat.parse(instruction.getPart(2)));
			}
		} catch (ParseException e) {
			throw new InstructionParseException("Invalid date format!");
		}

		if(startDate != null && endDate != null){
			if(startDate.compareTo(endDate) > 0 ){
				throw new InstructionParseException("Start date can't be after end date!");
			}
		}
	}

	private Date cleanDate(Date currentDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	@Override
	public boolean check(String playerID) throws QuestRuntimeException {
		Date currentDate = cleanDate(new Date());
		if (endDate == null) {
			return currentDate.compareTo(startDate) == 0;
		} else {
			return currentDate.compareTo(startDate) > 0 && currentDate.compareTo(endDate) < 0;
		}
	}
}
