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

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks if the player has required amount of empty slots in his inventory
 *
 * @author Jakub Sapalski
 */
public class EmptySlotsCondition extends Condition {

    private final VariableNumber needed;

    public EmptySlotsCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        needed = instruction.getVarNum();
    }

    @Override
    public boolean check(String playerID) throws QuestRuntimeException {
        Player player = PlayerConverter.getPlayer(playerID);
        ItemStack[] items = null;
        items = player.getInventory().getContents();

        int empty = 0;
        for (ItemStack item : items) {
            if (item == null)
                empty++;
        }
        return empty >= needed.getInt(playerID);
    }

}
