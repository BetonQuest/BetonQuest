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
package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;


/**
 * Requires the player to enchant an item.
 * 
 * @author Jakub Sapalski
 */
public class EnchantObjective extends Objective implements Listener {
    
    private Material item;
    private byte data;
    private Enchantment enchant;
    private int level;

    public EnchantObjective(String packName, String label, String instructions)
            throws InstructionParseException {
        super(packName, label, instructions);
        template = ObjectiveData.class;
        String[] parts = instructions.split(" ");
        if (parts.length < 3)
            throw new InstructionParseException("Not enough arguments");
        String[] itemParts = parts[1].split(":");
        item = Material.matchMaterial(itemParts[0]);
        if (item == null)
            throw new InstructionParseException("Unknown item type");
        if (itemParts.length > 1) {
            try {
                data = Byte.parseByte(itemParts[1]);
            } catch (NumberFormatException e) {
                throw new InstructionParseException("Could not parse item data value");
            }
        } else {
            data = 0;
        }
        String[] enchantParts = parts[2].split(":");
        if (enchantParts.length != 2)
            throw new InstructionParseException("Could not parse enchantment");
        enchant = Enchantment.getByName(enchantParts[0].toUpperCase());
        if (enchant == null)
            throw new InstructionParseException("Enchantment type '" + enchantParts[0] + "' does not exist");
        try {
            level = Integer.parseInt(enchantParts[1]);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse enchantment level");
        }
    }
    
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        String playerID = PlayerConverter.getID(event.getEnchanter());
        if (!containsPlayer(playerID)) return;
        if (event.getItem().getType() != item) return;
        if (event.getItem().getData().getData() != data) return;
        for (Enchantment enchant : event.getEnchantsToAdd().keySet())
            if (enchant == this.enchant)
                if (event.getEnchantsToAdd().get(enchant) >= level)
                    if (checkConditions(playerID))
                        completeObjective(playerID);
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

}
