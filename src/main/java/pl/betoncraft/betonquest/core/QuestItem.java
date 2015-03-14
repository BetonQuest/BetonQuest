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
package pl.betoncraft.betonquest.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.betoncraft.betonquest.config.ConfigHandler;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Represents an item handled by the configuration
 * 
 * @author co0sh
 */
public class QuestItem {

    private Material material = null;
    private short data = -1;
    private Map<Enchantment, Integer> enchants = null;
    private String name = null;
    private List<String> lore = null;
    private String title = null;
    private String author = null;
    private String text = null;
    private List<PotionEffect> effects = null;

    /**
     * Legacy method for the updater, don't use for anything else
     * 
     * @param material
     * @param data
     * @param enchants
     * @param name
     * @param lore
     */
    public QuestItem(String material, int data, Map<String, Integer> enchants, String name,
            List<String> lore) {
        this.material = Material.matchMaterial(material);
        this.data = (short) data;
        if (enchants != null) {
            this.enchants = new HashMap<>();
            for (String key : enchants.keySet()) {
                this.enchants.put(Enchantment.getByName(key), enchants.get(key));
            }
        }
        this.name = name;
        this.lore = lore;
    }

    /**
     * Creates new instance of the quest item using the itemID in items.yml
     * 
     * @param itemID
     *            ID of the item from items.yml
     */
    public QuestItem(String instruction) {
        String[] parts = instruction.split(" ");
        // get material type
        material = Material.matchMaterial(parts[0]);
        for (String part : parts) {
            if (part.startsWith("data:")) {
                // get data if exists
                data = Short.parseShort(part.substring(5));
            } else if (part.startsWith("enchants:")) {
                // get enchantments: if it is set, then enchantments should
                // be considered in checks
                enchants = new HashMap<>();
                if (part.equals("enchants:none")) {
                    // none means that map is empty - the item must not have
                    // enchantments
                    continue;
                }
                for (String enchant : part.substring(9).split(",")) {
                    Enchantment ID = Enchantment.getByName(enchant.split(":")[0]);
                    Integer level = new Integer(enchant.split(":")[1]);
                    enchants.put(ID, level);
                }
            } else if (part.startsWith("name:")) {
                // get name
                name = part.substring(5).replace("_", " ").replaceAll("&", "§");
                // if name is "none", then item must not have any name - ""
                if (name.equals("none")) name = "";
            } else if (part.startsWith("lore:")) {
                // get lore
                lore = new ArrayList<>();
                if (part.equals("lore:none")) {
                    // if lore is "none", then map is empty - item must not
                    // have lore
                    continue;
                }
                for (String line : part.substring(5).split(";")) {
                    lore.add(line.replaceAll("_", " ").replaceAll("&", "§"));
                }
            } else if (part.startsWith("title:")) {
                // get title
                title = part.substring(6).replace("_", " ").replaceAll("&", "§");
            } else if (part.startsWith("author:")) {
                // get author
                author = part.substring(7).replace("_", " ");
            } else if (part.startsWith("text:")) {
                // get text
                text = part.substring(5).replace("_", " ");
            } else if (part.startsWith("effects:")) {
                // get potion effects
                effects = new ArrayList<>();
                if (part.equals("effects:none")) {
                    // none means that potion must not have any effects
                    continue;
                }
                for (String effect : part.substring(8).split(",")) {
                    PotionEffectType ID = PotionEffectType.getByName(effect.split(":")[0]);
                    int power = Integer.parseInt(effect.split(":")[1]) - 1;
                    int duration = Integer.parseInt(effect.split(":")[2]) * 20;
                    effects.add(new PotionEffect(ID, duration, power));
                }
            }
        }
    }

    /**
     * Checks if it's equal to other quest item
     * 
     * @param item
     *            item to check against
     * @return if both items are equal
     */
    public boolean equalsQ(QuestItem item) {
        if (!((item.getAuthor() == null && author == null) || (item.getAuthor() != null
            && author != null && item.getAuthor().equals(author)))) {
            return false;
        }
        if (item.getData() != data) {
            return false;
        }
        if (!((item.getEffects() == null && effects == null) || (item.getEffects() != null
            && effects != null && item.getEffects().equals(effects)))) {
            return false;
        }
        if (!((item.getEnchants() == null && enchants == null) || (item.getEnchants() != null
            && enchants != null && item.getEnchants().equals(enchants)))) {
            return false;
        }
        if (!((item.getLore() == null && lore == null) || (item.getLore() != null && lore != null && item
                .getLore().equals(lore)))) {
            return false;
        }
        if (!((item.getMaterial() == null && material == null) || (item.getMaterial() != null
            && material != null && item.getMaterial() == material))) {
            return false;
        }
        if (!((item.getName() == null && name == null) || (item.getName() != null && name != null && item
                .getName().equals(name)))) {
            return false;
        }
        if (!((item.getText() == null && text == null) || (item.getText() != null && text != null && item
                .getText().equals(text)))) {
            return false;
        }
        if (!((item.getTitle() == null && title == null) || (item.getTitle() != null
            && title != null && item.getTitle().equals(title)))) {
            return false;
        }
        return true;
    }
    
    /**
     * Compares ItemStack to the quest item from items.yml
     * 
     * @param item
     *            ItemStack to compare
     * @return true if the item matches
     */
    @SuppressWarnings("deprecation")
    public boolean equalsI(ItemStack item) {
        if (item == null) {
            return false;
        }
        if (item.getType() != material) {
            return false;
        }
        if (data >= 0 && item.getData().getData() != data) {
            return false;
        }
        if (name != null) {
            if (name.equals("")) {
                if (item.getItemMeta().hasDisplayName()) {
                    return false;
                }
            } else {
                if (item.getItemMeta().hasDisplayName()) {
                    if (!item.getItemMeta().getDisplayName().equals(name)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        if (lore != null) {
            if (lore.isEmpty()) {
                if (item.getItemMeta().hasLore()) {
                    return false;
                }
            } else {
                if (!item.getItemMeta().hasLore()) {
                    return false;
                } else {
                    if (!item.getItemMeta().getLore().equals(lore)) {
                        return false;
                    }
                }
            }
        }
        if (enchants != null) {
            if (enchants.isEmpty()) {
                if (item.getItemMeta().hasEnchants()) {
                    return false;
                }
            } else {
                if (!item.getItemMeta().hasEnchants()) {
                    return false;
                } else {
                    if (!item.getEnchantments().equals(enchants)) {
                        return false;
                    }
                }
            }
        }
        if (item.getType().equals(Material.WRITTEN_BOOK)) {
            BookMeta bookMeta = (BookMeta) item.getItemMeta();
            if (author != null && (!bookMeta.hasAuthor() || !bookMeta.getAuthor().equals(author))) {
                return false;
            }
            if (title != null && (!bookMeta.hasTitle() || !bookMeta.getTitle().equals(title))) {
                return false;
            }
            if (text != null  && (!bookMeta.hasPages() || !bookMeta.getPages().equals(
                        Utils.pagesFromString(text, false)))) {
                return false;
            }
        } else if (item.getType().equals(Material.POTION)) {
            if (effects != null) {
              PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
                if (effects.isEmpty()) {
                    if (potionMeta.hasCustomEffects()) {
                        return false;
                    }
                } else {
                    if (!potionMeta.hasCustomEffects()) {
                        return false;
                    } else {
                        if (!potionMeta.getCustomEffects().equals(effects)) {
                            return false;
                        }
                    }
                }
            }
        } // TODO other item types
        return true;
    }
    
    /**
     * Generates this quest item as ItemStack with given amount
     * 
     * @param stackSize
     *          size of generated stack
     * @return the ItemStack equal to this quest item
     */
    public ItemStack generateItem(int stackSize) {
        if (data < 0) {
            data = 0;
        }
        ItemStack item = new ItemStack(material, stackSize, data);
        ItemMeta meta = item.getItemMeta();
        if (name != null && !name.equals("")) {
            meta.setDisplayName(name);
        }
        if (lore != null && !lore.isEmpty()) {
            meta.setLore(lore);
        }
        if (enchants != null && !enchants.isEmpty()) {
            for (Enchantment enchant : enchants.keySet()) {
                meta.addEnchant(enchant, enchants.get(enchant), true);
            }
        }
        if (material.equals(Material.WRITTEN_BOOK)) {
            BookMeta bookMeta = (BookMeta) meta;
            if (author != null) {
                bookMeta.setAuthor(author);
            } else {
                bookMeta.setAuthor(ConfigHandler.getString("messages."
                    + ConfigHandler.getString("config.language") + ".unknown_author"));
            }
            if (text != null) {
                bookMeta.setPages(Utils.pagesFromString(text, false));
            }
            if (title != null) {
                bookMeta.setTitle(title);
            } else {
                bookMeta.setTitle(ConfigHandler.getString("messages."
                    + ConfigHandler.getString("config.language") + ".unknown_title"));
            }
            item.setItemMeta(bookMeta);
            return item;
        } else if (material.equals(Material.POTION) && effects != null && !effects.isEmpty()) {
            PotionMeta potionMeta = (PotionMeta) meta;
            for (int i = 0; i < effects.size(); i++) {
                potionMeta.addCustomEffect(effects.get(i), true);
            }
            item.setItemMeta(potionMeta);
            return item;
        }
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Returns the material of this item.
     * 
     * @return the material
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Return the data of this item.
     * 
     * @return the data of -1 if any data is allowed
     */
    public int getData() {
        return data;
    }

    /**
     * Return the list of enchantments.
     * 
     * @return the list of enchantments
     */
    public Map<Enchantment, Integer> getEnchants() {
        return enchants;
    }

    /**
     * Returns the name of the item.
     * 
     * @return the name or null if there is no name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the lore of the item.
     * 
     * @return the lore
     */
    public List<String> getLore() {
        return lore;
    }

    /**
     * Returns the title of the book.
     * 
     * @return the title or null if it's not a book
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the author of the book.
     * 
     * @return the author or null if it's nor a book
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Returns the text from the book.
     * 
     * @return the pages from the book or null if it's not a bug
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the list of potion effects.
     * 
     * @return the effects
     */
    public List<PotionEffect> getEffects() {
        return effects;
    }
}
