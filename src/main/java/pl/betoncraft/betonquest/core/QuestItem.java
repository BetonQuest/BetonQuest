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

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Represents an item handled by the configuration
 * 
 * @author co0sh
 */
public class QuestItem {

    private String material = null;
    private int data = -1;
    private Map<String, Integer> enchants = new HashMap<>();
    private String name = null;
    private List<String> lore = new ArrayList<>();
    private String title = null;
    private String author = null;
    private String text = null;
    private List<PotionEffect> effects = new ArrayList<>();

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
        this.material = material;
        this.data = data;
        this.enchants = enchants;
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
        material = parts[0];
        for (String part : parts) {
            if (part.contains("data:")) {
                // get data if exists
                data = Integer.parseInt(part.substring(5));
            } else if (part.contains("enchants:")) {
                // get enchantments
                for (String enchant : part.substring(9).split(",")) {
                    String ID = enchant.split(":")[0];
                    Integer level = new Integer(enchant.split(":")[1]);
                    enchants.put(ID, level);
                }
            } else if (part.contains("name:")) {
                // get name
                name = part.substring(5).replace("_", " ").replaceAll("&", "§");
            } else if (part.contains("lore:")) {
                // get lore
                for (String line : part.substring(5).split(";")) {
                    lore.add(line.replaceAll("_", " ").replaceAll("&", "§"));
                }
            } else if (part.contains("title:")) {
                // get title
                title = part.substring(6).replace("_", " ").replaceAll("&", "§");
            } else if (part.contains("author:")) {
                // get author
                author = part.substring(7).replace("_", " ");
            } else if (part.contains("text:")) {
                // get text
                text = part.substring(5).replace("_", " ");
            } else if (part.contains("effects:")) {
                // get potion effects
                for (String effect : part.substring(8).split(",")) {
                    PotionEffectType ID = PotionEffectType.getByName(effect.split(":")[0]);
                    int power = Integer.parseInt(effect.split(":")[1]) - 1;
                    int duration = Integer.parseInt(effect.split(":")[2]) * 20;
                    effects.add(new PotionEffect(ID, power, duration));
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
    public boolean equalsToItem(QuestItem item) {
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
            && material != null && item.getMaterial().equalsIgnoreCase(material)))) {
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
     * Returns the material of this item.
     * 
     * @return the material
     */
    public String getMaterial() {
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
    public Map<String, Integer> getEnchants() {
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
