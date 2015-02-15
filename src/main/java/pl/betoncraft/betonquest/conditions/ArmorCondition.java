/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
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

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Instruction string: type:leggings material:iron
 * 
 * @author Co0sh
 */
public class ArmorCondition extends Condition {

    private Material armor;
    private String type;
    private String material;
    private Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();

    /**
     * Constructor method
     * 
     * @param playerID
     * @param instructions
     */
    public ArmorCondition(String playerID, String instructions) {
        super(playerID, instructions);
        for (String part : instructions.split(" ")) {
            if (part.contains("type:")) {
                type = part.substring(5).toUpperCase();
            } else if (part.contains("material:")) {
                material = part.substring(9).toUpperCase();
            } else if (part.contains("enchants:")) {
                for (String enchant : part.substring(9).split(",")) {
                    enchants.put(Enchantment.getByName(enchant.split(":")[0]),
                            Integer.decode(enchant.split(":")[1]));
                }
            }
        }
        if (type != null && material != null) {
            armor = Material.matchMaterial(material + "_" + type);
        }
    }

    @Override
    public boolean isMet() {
        for (ItemStack item : PlayerConverter.getPlayer(playerID).getEquipment().getArmorContents()) {
            if (item.getType().equals(armor)) {
                if (enchants != null) {
                    for (Enchantment enchant : enchants.keySet()) {
                        if (item.getEnchantments().get(enchant) == null) {
                            return false;
                        }
                        if (item.getEnchantments().get(enchant) < enchants.get(enchant)) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

}
