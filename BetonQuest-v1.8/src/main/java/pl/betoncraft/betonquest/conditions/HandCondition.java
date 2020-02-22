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

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Holding item in hand condition
 *
 * @author Jakub Sapalski
 */
public class HandCondition extends Condition {

    private final QuestItem questItem;

    public HandCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        questItem = new QuestItem(instruction.getItem());
    }

    @Override
    public boolean check(String playerID) {
        PlayerInventory inv = PlayerConverter.getPlayer(playerID).getInventory();
        ItemStack item = null;
        item = inv.getItemInHand();

        return questItem.compare(item);
    }

}
