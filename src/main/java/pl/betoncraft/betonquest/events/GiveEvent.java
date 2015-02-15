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
package pl.betoncraft.betonquest.events;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.ConfigHandler;
import pl.betoncraft.betonquest.core.QuestItem;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * 
 * @author Co0sh
 */
public class GiveEvent extends QuestEvent {

    private QuestItem questItem;
    private int amount = 1;

    /**
     * Constructor method
     * 
     * @param playerID
     * @param instructions
     */
    public GiveEvent(String playerID, String instructions) {
        super(playerID, instructions);

        String[] parts = instructions.split(" ");
        questItem = new QuestItem(parts[1]);
        for (String part : parts) {
            if (part.contains("amount:")) {
                amount = Integer.valueOf(part.substring(7));
            }
        }
        while (amount > 0) {
            int stackSize;
            if (amount > 64) {
                stackSize = 64;
            } else {
                stackSize = amount;
            }
            byte data;
            if (questItem.getData() < 0) {
                data = 0;
            } else {
                data = (byte) questItem.getData();
            }
            ItemStack item = new ItemStack(Material.matchMaterial(questItem.getMaterial()),
                    stackSize, data);
            ItemMeta meta = item.getItemMeta();
            if (questItem.getName() != null) {
                meta.setDisplayName(questItem.getName());
            }
            meta.setLore(questItem.getLore());
            for (String enchant : questItem.getEnchants().keySet()) {
                meta.addEnchant(Enchantment.getByName(enchant), questItem.getEnchants()
                        .get(enchant), true);
            }
            if (Material.matchMaterial(questItem.getMaterial()).equals(Material.WRITTEN_BOOK)) {
                BookMeta bookMeta = (BookMeta) meta;
                if (questItem.getAuthor() != null) {
                    bookMeta.setAuthor(questItem.getAuthor());
                } else {
                    bookMeta.setAuthor(ConfigHandler.getString("messages."
                        + ConfigHandler.getString("config.language") + ".unknown_author"));
                }
                if (questItem.getText() != null) {
                    bookMeta.setPages(Utils.pagesFromString(questItem.getText(), false));
                }
                if (questItem.getTitle() != null) {
                    bookMeta.setTitle(questItem.getTitle());
                } else {
                    bookMeta.setTitle(ConfigHandler.getString("messages."
                        + ConfigHandler.getString("config.language") + ".unknown_title"));
                }
                item.setItemMeta(bookMeta);
            }
            if (Material.matchMaterial(questItem.getMaterial()).equals(Material.POTION)) {
                PotionMeta potionMeta = (PotionMeta) meta;
                for (PotionEffect effect : questItem.getEffects()) {
                    potionMeta.addCustomEffect(effect, true);
                }
                item.setItemMeta(potionMeta);
            }
            item.setItemMeta(meta);
            PlayerConverter.getPlayer(playerID).getInventory().addItem(item);
            amount = amount - stackSize;
        }

    }

}
