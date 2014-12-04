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
				name = part.substring(5).replace("_", " ");
			} else if (part.contains("lore:")) {
				for (String line : part.substring(5).split(";")) {
					lore.add(line.replaceAll("_", " "));
				}
			} else if (part.contains("title:")) {
				title = part.substring(6).replace("_", " ");
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
