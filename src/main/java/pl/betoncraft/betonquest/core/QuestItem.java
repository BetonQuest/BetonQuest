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

import pl.betoncraft.betonquest.inout.ConfigInput;

/**
 * @author co0sh
 */
public class QuestItem {
	
	private String material = null;
	private int data = -1;
	private Map<String,Integer> enchants = new HashMap<>();
	private String name = null;
	private List<String> lore = new ArrayList<>();
	private String title = null;
	private String author = null;
	private String text = null;
	private List<EffectContainer> effects = new ArrayList<>();
	
	/**
	 * Legacy method for the updater, don't use for anything else
	 * @param material
	 * @param data
	 * @param enchants
	 * @param name
	 * @param lore
	 */
	public QuestItem(String material, int data, Map<String,Integer> enchants, String name, List<String> lore) {
		this.material = material;
		this.data = data;
		this.enchants = enchants;
		this.name = name;
		this.lore = lore;
	}
	
	/**
	 * Represents an item from items.yml
	 * @param itemID
	 */
	public QuestItem(String itemID) {
		String[] parts = ConfigInput.getString("items." + itemID).split(" ");
		material = parts[0];
		for (String part : parts) {
			if (part.contains("data:")) {
				data = Integer.parseInt(part.substring(5));
			} else if (part.contains("enchants:")) {
				for (String enchant : part.substring(9).split(",")) {
					String ID = enchant.split(":")[0];
					Integer level = new Integer(enchant.split(":")[1]);
					enchants.put(ID, level);
				}
			} else if (part.contains("name:")) {
				name = part.substring(5).replace("_", " ").replaceAll("&", "§");
			} else if (part.contains("lore:")) {
				for (String line : part.substring(5).split(";")) {
					lore.add(line.replaceAll("_", " ").replaceAll("&", "§"));
				}
			} else if (part.contains("title:")) {
				title = part.substring(6).replace("_", " ").replaceAll("&", "§");
			} else if (part.contains("author:")) {
				author = part.substring(7).replace("_", " ");
			} else if (part.contains("text:")) {
				text = part.substring(5).replace("_", " ");
			} else if (part.contains("effects:")) {
				for (String effect : part.substring(8).split(",")) {
					String ID = effect.split(":")[0];
					int power = Integer.parseInt(effect.split(":")[1]) - 1;
					int duration = Integer.parseInt(effect.split(":")[2]) * 20;
					effects.add(new EffectContainer(ID, power, duration));
				}
			}
		}
	}
	
	public boolean equalsToItem(QuestItem item) {
		if (!((item.getAuthor() == null && author == null) || (item.getAuthor() != null && author != null && item.getAuthor().equals(author)))) {
			return false;
		}
		if (item.getData() != data) {
			return false;
		}
		if (!((item.getEffects() == null && effects == null) || (item.getEffects() != null && effects != null && item.getEffects().equals(effects)))) {
			return false;
		}
		if (!((item.getEnchants() == null && enchants == null) || (item.getEnchants() != null && enchants != null && item.getEnchants().equals(enchants)))) {
			return false;
		}
		if (!((item.getLore() == null && lore == null) || (item.getLore() != null && lore != null && item.getLore().equals(lore)))) {
			return false;
		}
		if (!((item.getMaterial() == null && material == null) || (item.getMaterial() != null && material != null && item.getMaterial().equalsIgnoreCase(material)))) {
			return false;
		}
		if (!((item.getName() == null && name == null) || (item.getName() != null && name != null && item.getName().equals(name)))) {
			return false;
		}
		if (!((item.getText() == null && text == null) || (item.getText() != null && text != null && item.getText().equals(text)))) {
			return false;
		}
		if (!((item.getTitle() == null && title == null) || (item.getTitle() != null && title != null && item.getTitle().equals(title)))) {
			return false;
		}
		return true;
	}

	/**
	 * @return the material
	 */
	public String getMaterial() {
		return material;
	}

	/**
	 * @return the data
	 */
	public int getData() {
		return data;
	}

	/**
	 * @return the enchants
	 */
	public Map<String, Integer> getEnchants() {
		return enchants;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the lore
	 */
	public List<String> getLore() {
		return lore;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @return the pages
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return the effects
	 */
	public List<EffectContainer> getEffects() {
		return effects;
	}

}
