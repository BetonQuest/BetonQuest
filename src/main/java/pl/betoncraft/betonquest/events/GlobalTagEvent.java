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
package pl.betoncraft.betonquest.events;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.database.GlobalData;

/**
 * Adds or removes global tags
 *
 * @author Jonas Blocher
 */
public class GlobalTagEvent extends TagEvent {

	public GlobalTagEvent(Instruction instruction) throws InstructionParseException {
		super(instruction);
		staticness = true;
		persistent = true;
	}

	@Override
	public void run(String playerID) {
		GlobalData globalData = BetonQuest.getInstance().getGlobalData();
		if (add) {
			for (String tag : tags) {
				globalData.addTag(tag);
			}
		} else {
			for (String tag : tags) {
				globalData.removeTag(tag);
			}
		}
	}
}
