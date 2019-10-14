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
package pl.betoncraft.betonquest.compatibility.shopkeepers;

import com.nisovin.shopkeepers.api.ShopkeepersAPI;
import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import com.nisovin.shopkeepers.api.shopkeeper.player.PlayerShopkeeper;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

/**
 * Checks if the player owns specified amount of shops.
 */
public class HavingShopCondition extends Condition {

    private final VariableNumber amount;

    public HavingShopCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        persistent = true;
        amount = instruction.getVarNum();
    }

    @Override
    public boolean check(String playerID) throws QuestRuntimeException {
        int count = amount.getInt(playerID);
        for (Shopkeeper s : ShopkeepersAPI.getShopkeeperRegistry().getAllShopkeepers()) {
            if (s instanceof PlayerShopkeeper) {
                PlayerShopkeeper ps = (PlayerShopkeeper) s;
                if (ps.getOwnerUUID() != null && ps.getOwnerUUID().toString().equals(playerID)) {
                    count--;
                    if (count == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
