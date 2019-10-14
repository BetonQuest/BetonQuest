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

import org.bukkit.block.Block;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.BlockSelector;
import pl.betoncraft.betonquest.utils.LocationData;

/**
 * Checks block at specified location against specified Material
 *
 * @author Jakub Sapalski
 */
public class TestForBlockCondition extends Condition {

    private final LocationData loc;
    private final BlockSelector selector;

    public TestForBlockCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        staticness = true;
        persistent = true;
        loc = instruction.getLocation();
        selector = new BlockSelector(instruction.next());

        // To remain backwards compatible pre 1.13 we will support data and add it to the selector if needed but
        // a separate data tag is deprecated to remain consistent with the other objectives/conditions
        String dataString = instruction.getOptional("data");
        if (dataString != null) {
            selector.setData(instruction.getInt(dataString, 0));
        }
    }

    @Override
    public boolean check(String playerID) throws QuestRuntimeException {
        Block block = loc.getLocation(playerID).getBlock();

        return selector.match(block);
    }

}
