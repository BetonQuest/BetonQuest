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
import pl.betoncraft.betonquest.Point;
import pl.betoncraft.betonquest.database.GlobalData;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

/**
 * Modifies global Points
 *
 * @author Jonas Blocher
 */
public class GlobalPointEvent extends PointEvent {

    public GlobalPointEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        staticness = true;
        persistent = true;
    }

    @Override
    public void run(String playerID) throws QuestRuntimeException {
        GlobalData globalData = BetonQuest.getInstance().getGlobalData();
        addPoints(playerID, globalData);

    }

    private void addPoints(String playerId, GlobalData globalData) throws QuestRuntimeException {
        if (multi) {
            for (Point p : globalData.getPoints()) {
                if (p.getCategory().equalsIgnoreCase(category)) {
                    globalData.modifyPoints(category,
                            (int) Math.floor((p.getCount() * count.getDouble(playerId)) - p.getCount()));
                }
            }
        } else {
            globalData.modifyPoints(category, count.getInt(playerId));
        }
    }
}
