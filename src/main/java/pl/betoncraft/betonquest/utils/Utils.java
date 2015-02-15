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
package pl.betoncraft.betonquest.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import pl.betoncraft.betonquest.core.QuestItem;

/**
 * Various utilities.
 * 
 * @author Coosh
 */
public class Utils {

    /**
     * Converts string to list of pages for a book. SingleString defines if you
     * passed a string separated by "|" for every page. False means that it is
     * separated, true that it isn't.
     * 
     * @param string
     *            text to convert
     * @param singleString
     *            if it's a single string or it has characters splitting it to
     *            pages
     * @return the list of pages for a book
     */
    public static List<String> pagesFromString(String string, boolean singleString) {
        List<String> pages = new ArrayList<>();
        if (singleString) {
            StringBuilder page = new StringBuilder();
            for (String word : string.split(" ")) {
                if (page.length() + word.length() + 1 > 245) {
                    pages.add(page.toString().trim());
                    page = new StringBuilder();
                }
                page.append(word + " ");
            }
            pages.add(page.toString().trim());
        } else {
            pages = Arrays.asList(string.replaceAll("\\\\n", "\n").split("\\|"));
        }
        return pages;
    }

    /**
     * Compares ItemStack to the quest item from items.yml
     * 
     * @param item
     *            ItemStack to compare
     * @param questItem
     *            instance of the QuestItem
     * @return true if the item matches
     */
    @SuppressWarnings("deprecation")
    public static boolean isItemEqual(ItemStack item, QuestItem questItem) {
        if (item == null) {
            return false;
        }
        if (item.getType() != Material.matchMaterial(questItem.getMaterial())) {
            return false;
        }
        if (questItem.getData() >= 0 && item.getData().getData() != questItem.getData()) {
            return false;
        }
        if (questItem.getName() != null
            && (!item.getItemMeta().hasDisplayName() || !item.getItemMeta().getDisplayName()
                    .equals(questItem.getName()))) {
            return false;
        }
        if (!questItem.getLore().isEmpty()
            && (!item.getItemMeta().hasLore() || !item.getItemMeta().getLore()
                    .equals(questItem.getLore()))) {
            return false;
        }
        if (!questItem.getEnchants().isEmpty()) {
            Map<Enchantment, Integer> enchants = new HashMap<>();
            for (String enchant : questItem.getEnchants().keySet()) {
                enchants.put(Enchantment.getByName(enchant), questItem.getEnchants().get(enchant));
            }
            if (!item.getEnchantments().equals(enchants)) {
                return false;
            }
        }
        if (item.getType().equals(Material.WRITTEN_BOOK)) {
            BookMeta bookMeta = (BookMeta) item.getItemMeta();
            if (questItem.getAuthor() != null
                && (!bookMeta.hasAuthor() || !bookMeta.getAuthor().equals(questItem.getAuthor()))) {
                return false;
            }
            if (!questItem.getLore().isEmpty()
                && (!bookMeta.hasLore() || !bookMeta.getLore().equals(questItem.getLore()))) {
                return false;
            }
            if (questItem.getText() != null
                && (!bookMeta.hasPages() || !bookMeta.getPages().equals(
                        pagesFromString(questItem.getText(), false)))) {
                return false;
            }
        } else if (item.getType().equals(Material.POTION)) {
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
            List<PotionEffect> effects = questItem.getEffects();
            if (!questItem.getEffects().isEmpty()
                && (!potionMeta.hasCustomEffects() || !potionMeta.getCustomEffects()
                        .equals(effects))) {
                return false;
            }
        }
        return true;
    }
}
