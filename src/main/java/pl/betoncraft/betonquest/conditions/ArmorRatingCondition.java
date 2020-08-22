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

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the player to have specific armor rating
 *
 * @author Jakub Sapalski
 */
public class ArmorRatingCondition extends Condition {

    private final VariableNumber required;

    public ArmorRatingCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        required = instruction.getVarNum();
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final PlayerInventory inv = PlayerConverter.getPlayer(playerID).getInventory();
        int rating = 0;
        final ItemStack boots = inv.getBoots();
        final ItemStack helmet = inv.getHelmet();
        final ItemStack chest = inv.getChestplate();
        final ItemStack leggings = inv.getLeggings();
        if (helmet != null) {
            if (helmet.getType() == Material.LEATHER_HELMET) {
                rating += 1;
            } else if (helmet.getType() == Material.GOLDEN_HELMET) {
                rating += 2;
            } else if (helmet.getType() == Material.CHAINMAIL_HELMET) {
                rating += 2;
            } else if (helmet.getType() == Material.IRON_HELMET) {
                rating += 2;
            } else if (helmet.getType() == Material.DIAMOND_HELMET) {
                rating += 3;
            } else if (helmet.getType().toString().equals("NETHERITE_HELMET")) {
                rating += 3;
            }
        }
        if (boots != null) {
            if (boots.getType() == Material.LEATHER_BOOTS) {
                rating += 1;
            } else if (boots.getType() == Material.GOLDEN_BOOTS) {
                rating += 1;
            } else if (boots.getType() == Material.CHAINMAIL_BOOTS) {
                rating += 1;
            } else if (boots.getType() == Material.IRON_BOOTS) {
                rating += 2;
            } else if (boots.getType() == Material.DIAMOND_BOOTS) {
                rating += 3;
            } else if (boots.getType().toString().equals("NETHERITE_BOOTS")) {
                rating += 3;
            }

        }
        if (leggings != null) {
            if (leggings.getType() == Material.LEATHER_LEGGINGS) {
                rating += 2;
            } else if (leggings.getType() == Material.GOLDEN_LEGGINGS) {
                rating += 3;
            } else if (leggings.getType() == Material.CHAINMAIL_LEGGINGS) {
                rating += 4;
            } else if (leggings.getType() == Material.IRON_LEGGINGS) {
                rating += 5;
            } else if (leggings.getType() == Material.DIAMOND_LEGGINGS) {
                rating += 6;
            } else if (leggings.getType().toString().equals("NETHERITE_LEGGINGS")) {
                rating += 6;
            }
        }
        if (chest != null) {
            if (chest.getType() == Material.LEATHER_CHESTPLATE) {
                rating += 3;
            } else if (chest.getType() == Material.GOLDEN_CHESTPLATE) {
                rating += 5;
            } else if (chest.getType() == Material.CHAINMAIL_CHESTPLATE) {
                rating += 5;
            } else if (chest.getType() == Material.IRON_CHESTPLATE) {
                rating += 6;
            } else if (chest.getType() == Material.DIAMOND_CHESTPLATE) {
                rating += 8;
            } else if (chest.getType().toString().equals("NETHERITE_CHESTPLATE")) {
                rating += 8;
            }
        }
        return rating >= required.getInt(playerID);
    }

}
