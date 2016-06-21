/**
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
package pl.betoncraft.betonquest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Represents an item handled by the configuration
 * 
 * @author Jakub Sapalski
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
	private PotionType baseEffect = null;
	private boolean extended = false;
	private boolean upgraded = false;
	private List<PotionEffect> effects = null;
	private Color color = null;
	private String owner = null;

	/**
	 * Legacy method for the updater, don't use for anything else
	 * 
	 * @param material
	 * @param data
	 * @param enchants
	 * @param name
	 * @param lore
	 */
	public QuestItem(String material, int data, Map<String, Integer> enchants, String name, List<String> lore) {
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
	 * Loads an item with given ID from the items.yml file.
	 * 
	 * @param packName
	 *            name of the package to load it from
	 * @param itemID
	 *            ID of the item
	 * @return the loaded item
	 * @throws InstructionParseException
	 *             when item parsing goes wrong
	 */
	public static QuestItem newQuestItem(String packName, String itemID) throws InstructionParseException {
		String pack, ID;
		if (itemID.contains(".")) {
			String[] parts = itemID.split("\\.");
			if (parts.length != 2) {
				throw new InstructionParseException("Incorrect item ID: " + itemID);
			}
			pack = parts[0];
			ID = parts[1];
		} else {
			pack = packName;
			ID = itemID;
		}
		String instruction = Config.getString(pack + ".items." + ID);
		if (instruction == null) {
			throw new InstructionParseException("Item is not defined");
		}
		return new QuestItem(instruction);
	}

	/**
	 * Creates new instance of the quest item using the instruction string
	 * 
	 * @param itemID
	 *            ID of the item from items.yml
	 */
	public QuestItem(String instruction) throws InstructionParseException {
		if (instruction == null)
			throw new NullPointerException("Item instruction is null");
		String[] parts = instruction.split(" ");
		if (parts.length < 1) {
			throw new InstructionParseException("Item type not defined");
		}
		// get material type
		material = Material.matchMaterial(parts[0]);
		if (material == null) {
			throw new InstructionParseException("Unknown item type: " + parts[0]);
		}
		for (String part : parts) {
			if (part.startsWith("data:")) {
				// get data if exists
				data = Short.parseShort(part.substring(5));
				if (data < 0) {
					throw new InstructionParseException("Item data cannot be negative");
				}
			} else if (part.startsWith("enchants:")) {
				// get enchantments: if it is set, then enchantments should
				// be considered in checks
				enchants = new HashMap<>();
				if (part.equalsIgnoreCase("enchants:none")) {
					// none means that map is empty - the item must not have
					// enchantments
					continue;
				}
				for (String enchant : part.substring(9).split(",")) {
					String[] enchParts = enchant.split(":");
					if (enchParts.length != 2) {
						throw new InstructionParseException("Wrong enchantment format: " + enchant);
					}
					Enchantment ID = Enchantment.getByName(enchParts[0]);
					if (ID == null) {
						throw new InstructionParseException("Unknown enchantment type: " + enchParts[0]);
					}
					Integer level;
					try {
						level = new Integer(enchParts[1]);
					} catch (NumberFormatException e) {
						throw new InstructionParseException("Could not parse level in enchant: " + enchant);
					}
					enchants.put(ID, level);
				}
			} else if (part.startsWith("name:")) {
				// get name
				name = part.substring(5).replace("_", " ").replaceAll("&", "§");
				// if name is "none", then item must not have any name - ""
				if (name.equals("none"))
					name = "";
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
					String[] effParts = effect.split(":");
					PotionEffectType ID = PotionEffectType.getByName(effParts[0]);
					if (ID == null) {
						throw new InstructionParseException("Unknown potion effect" + effParts[0]);
					}
					int power, duration;
					try {
						power = Integer.parseInt(effect.split(":")[1]) - 1;
						duration = Integer.parseInt(effect.split(":")[2]) * 20;
					} catch (NumberFormatException e) {
						throw new InstructionParseException("Could not parse potion power/duration: " + effect);
					}
					effects.add(new PotionEffect(ID, duration, power));
				}
			} else if (part.startsWith("color:")) {
				if (part.equals("color:none")) {
					color = Bukkit.getServer().getItemFactory().getDefaultLeatherColor();
				} else {
					try {
						color = Color.fromRGB(Integer.parseInt(part.substring(6)));
					} catch (NumberFormatException e) {
						throw new InstructionParseException("Could not parse leather armor color");
					}
				}
			} else if (part.startsWith("owner:")) {
				if (part.equals("owner:none")) {
					owner = "";
				} else {
					owner = part.substring(6);
				}
			} else if (part.startsWith("type:")) {
				try {
					baseEffect = PotionType.valueOf(part.substring(5));
				} catch (IllegalArgumentException e) {
					throw new InstructionParseException("Unknown potion type: " + part.substring(5));
				}
			} else if (part.equalsIgnoreCase("extended")) {
				extended = true;
			} else if (part.equalsIgnoreCase("upgraded")) {
				upgraded = true;
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
		if (!((item.getAuthor() == null && author == null)
				|| (item.getAuthor() != null && author != null && item.getAuthor().equals(author)))) {
			return false;
		}
		if (item.getData() != data) {
			return false;
		}
		if (!((item.getEffects() == null && effects == null)
				|| (item.getEffects() != null && effects != null && item.getEffects().equals(effects)))) {
			return false;
		}
		if (!((item.getEnchants() == null && enchants == null)
				|| (item.getEnchants() != null && enchants != null && item.getEnchants().equals(enchants)))) {
			return false;
		}
		if (!((item.getLore() == null && lore == null)
				|| (item.getLore() != null && lore != null && item.getLore().equals(lore)))) {
			return false;
		}
		if (!((item.getMaterial() == null && material == null)
				|| (item.getMaterial() != null && material != null && item.getMaterial() == material))) {
			return false;
		}
		if (!((item.getName() == null && name == null)
				|| (item.getName() != null && name != null && item.getName().equals(name)))) {
			return false;
		}
		if (!((item.getText() == null && text == null)
				|| (item.getText() != null && text != null && item.getText().equals(text)))) {
			return false;
		}
		if (!((item.getTitle() == null && title == null)
				|| (item.getTitle() != null && title != null && item.getTitle().equals(title)))) {
			return false;
		}
		if (!((item.getColor() == null && color == null)
				|| (item.getColor() != null && color != null && item.getColor().equals(color)))) {
			return false;
		}
		if (!((item.getOwner() == null && owner == null)
				|| (item.getOwner() != null && owner != null && item.getOwner().equals(owner)))) {
			return false;
		}
		if (!((item.getBaseEffect() == null && baseEffect == null)
				|| (item.getBaseEffect() != null && baseEffect != null && item.getBaseEffect().equals(baseEffect)))) {
			return false;
		}
		if (item.isExtended() != extended) {
			return false;
		}
		if (item.isUpgraded() != upgraded) {
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
			System.out.println("data not matching");
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
				if (material != Material.ENCHANTED_BOOK) {
					if (item.getItemMeta().hasEnchants()) {
						return false;
					}
				} else {
					EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) item.getItemMeta();
					if (!storageMeta.getStoredEnchants().equals(enchants)) {
						return false;
					}
				}
			} else {
				if (material != Material.ENCHANTED_BOOK) {
					if (!item.getItemMeta().hasEnchants()) {
						return false;
					} else {
						if (!item.getEnchantments().equals(enchants)) {
							return false;
						}
					}
				} else {
					EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) item.getItemMeta();
					if (!storageMeta.hasStoredEnchants()) {
						return false;
					} else {
						if (!storageMeta.getStoredEnchants().equals(enchants)) {
							return false;
						}
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
			if (text != null && (!bookMeta.hasPages() || !bookMeta.getPages().equals(
					Utils.pagesFromString(text, false)))) {
				return false;
			}
		} else if (item.getType().equals(Material.POTION)) {
			PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
			if (baseEffect != null) {
				PotionData pData = potionMeta.getBasePotionData();
				if (pData.getType() != baseEffect || pData.isExtended() != extended || pData.isUpgraded() != upgraded) {
					return false;
				}
			}
			if (effects != null) {
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
		} else if (item.getType().equals(Material.LEATHER_BOOTS) || item.getType().equals(Material.LEATHER_CHESTPLATE)
				|| item.getType().equals(Material.LEATHER_HELMET) || item.getType().equals(Material.LEATHER_LEGGINGS)) {
			LeatherArmorMeta armorMeta = (LeatherArmorMeta) item.getItemMeta();
			if (color != null) {
				if (!armorMeta.getColor().equals(color)) {
					return false;
				}
			}
		} else if (item.getType() == Material.SKULL_ITEM) {
			SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
			if (owner != null) {
				if (owner.equals("")) {
					if (skullMeta.hasOwner()) {
						return false;
					}
				} else {
					if (!skullMeta.hasOwner()) {
						return false;
					} else {
						if (!skullMeta.getOwner().equals(owner)) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * Generates this quest item as ItemStack with given amount
	 * 
	 * @param stackSize
	 *            size of generated stack
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
		if (enchants != null && material != Material.ENCHANTED_BOOK && !enchants.isEmpty()) {
			for (Enchantment enchant : enchants.keySet()) {
				meta.addEnchant(enchant, enchants.get(enchant), true);
			}
		}
		if (material.equals(Material.WRITTEN_BOOK)) {
			BookMeta bookMeta = (BookMeta) meta;
			if (author != null) {
				bookMeta.setAuthor(author);
			} else {
				bookMeta.setAuthor(Config.getMessage(Config.getLanguage(), "unknown_author"));
			}
			if (text != null) {
				bookMeta.setPages(Utils.pagesFromString(text, false));
			}
			if (title != null) {
				bookMeta.setTitle(title);
			} else {
				bookMeta.setTitle(Config.getMessage(Config.getLanguage(), "unknown_title"));
			}
			item.setItemMeta(bookMeta);
			return item;
		} else if (material.equals(Material.POTION)) {
			PotionMeta potionMeta = (PotionMeta) meta;
			if (effects != null && !effects.isEmpty()) {
				for (int i = 0; i < effects.size(); i++) {
					potionMeta.addCustomEffect(effects.get(i), true);
				}
			}
			if (baseEffect != null) {
				potionMeta.setBasePotionData(new PotionData(baseEffect, extended, upgraded));
			}
			item.setItemMeta(potionMeta);
			return item;
		} else if ((material.equals(Material.LEATHER_BOOTS) || material.equals(Material.LEATHER_CHESTPLATE)
				|| material.equals(Material.LEATHER_HELMET) || material.equals(Material.LEATHER_LEGGINGS))
				&& color != null && !color.equals(Bukkit.getServer().getItemFactory().getDefaultLeatherColor())) {
			LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
			armorMeta.setColor(color);
			item.setItemMeta(armorMeta);
			return item;
		} else if (enchants != null && material == Material.ENCHANTED_BOOK && !enchants.isEmpty()) {
			EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) item.getItemMeta();
			for (Enchantment enchant : enchants.keySet()) {
				storageMeta.addStoredEnchant(enchant, enchants.get(enchant), true);
			}
			item.setItemMeta(storageMeta);
			return item;
		} else if (material == Material.SKULL_ITEM && owner != null && !owner.equals("")) {
			SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
			skullMeta.setOwner(owner);
			item.setItemMeta(skullMeta);
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

	/**
	 * Returns color of leather armor
	 * 
	 * @return color of the leather armor
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Returns owner of the skull
	 * 
	 * @return owner of the skull
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @return the base effect of the potion
	 */
	public PotionType getBaseEffect() {
		return baseEffect;
	}

	/**
	 * @return if the potion is extended
	 */
	public boolean isExtended() {
		return extended;
	}

	/**
	 * @return if the potion is upgraded
	 */
	public boolean isUpgraded() {
		return upgraded;
	}
}
