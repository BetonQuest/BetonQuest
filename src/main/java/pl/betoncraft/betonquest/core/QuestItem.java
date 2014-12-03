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
	
	private String material;
	private int data = 0;
	private Map<String,Integer> enchants;
	private String name;
	private List<String> lore;
	private String title;
	private String author;
	private List<String> pages;
	private Map<String,Integer> effects;
	
	/**
	 * Represents an item from items.yml
	 * @param itemID
	 */
	public QuestItem(String itemID) {
		String[] parts = ConfigInput.getString("items.itemID").split(" ");
		material = parts[0];
		for (String part : parts) {
			if (part.contains("data:")) {
				data = Integer.parseInt(part.substring(5));
			} else if (part.contains("enchants:")) {
				enchants = new HashMap<>();
				for (String enchant : part.substring(9).split(",")) {
					String ID = enchant.split(":")[0];
					Integer level;
					if (enchant.split(":").length == 2) {
						level = new Integer(enchant.split(":")[1]);
					} else {
						level = new Integer(0);
					}
					enchants.put(ID, level);
				}
			} else if (part.contains("name:")) {
				name = part.substring(5);
			} else if (part.contains("lore:")) {
				lore = new ArrayList<>();
				for (String line : part.substring(5).split(";")) {
					lore.add(line);
				}
			} else if (part.contains("title:")) {
				title = part.substring(6);
			} else if (part.contains("author:")) {
				author = part.substring(7);
			} else if (part.contains("pages:")) {
				pages = new ArrayList<>();
				for (String page : part.substring(6).split(";")) {
					pages.add(page);
				}
			} else if (part.contains("effects:")) {
				effects = new HashMap<>();
				for (String effect : part.substring(8).split(",")) {
					String ID = effect.split(":")[0];
					Integer power;
					if (effect.split(":").length == 2) {
						power = new Integer(effect.split(":")[1]);
					} else {
						power = new Integer(0);
					}
					effects.put(ID, power);
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
	public List<String> getPages() {
		return pages;
	}

	/**
	 * @return the effects
	 */
	public Map<String, Integer> getEffects() {
		return effects;
	}

}
