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
package pl.betoncraft.betonquest.item;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.ItemID;
import pl.betoncraft.betonquest.item.typehandler.BookHandler;
import pl.betoncraft.betonquest.item.typehandler.ColorHandler;
import pl.betoncraft.betonquest.item.typehandler.DataHandler;
import pl.betoncraft.betonquest.item.typehandler.EnchantmentsHandler;
import pl.betoncraft.betonquest.item.typehandler.HeadOwnerHandler;
import pl.betoncraft.betonquest.item.typehandler.LoreHandler;
import pl.betoncraft.betonquest.item.typehandler.NameHandler;
import pl.betoncraft.betonquest.item.typehandler.PotionHandler;
import pl.betoncraft.betonquest.item.typehandler.UnbreakableHandler;

/**
 * Represents an item handled by the configuration.
 * 
 * @author Jakub Sapalski
 */
public class QuestItem {

	private Material material = null;
	private DataHandler data = new DataHandler();
	private NameHandler name = new NameHandler();
	private LoreHandler lore = new LoreHandler();
	private EnchantmentsHandler enchants = new EnchantmentsHandler();
	private UnbreakableHandler unbreakable = new UnbreakableHandler();
	private PotionHandler potion = new PotionHandler();
	private BookHandler book = new BookHandler();
	private HeadOwnerHandler head = new HeadOwnerHandler();
	private ColorHandler color = new ColorHandler();

	/**
	 * Legacy method for the updater, don't use for anything else.
	 * 
	 * @deprecated
	 * 
	 * @param material
	 * @param data
	 * @param enchants
	 * @param name
	 * @param lore
	 */
	public QuestItem(String material, int data, Map<String, Integer> enchants, String name, List<String> lore) {
		this.material = Material.matchMaterial(material);
		if (data >= 0) {
			try {
				this.data.set(String.valueOf(data));
			} catch (InstructionParseException e) {}
		}
		if (enchants != null && !enchants.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			for (String key : enchants.keySet()) {
				builder.append(key + ":" + enchants.get(key));
			}
			try {
				this.enchants.set(builder.substring(0, builder.length() - 1));
			} catch (InstructionParseException e) {}
		}
		if (name != null && !name.isEmpty()) {
			try {
				this.name.set(name);
			} catch (InstructionParseException e) {}
		}
		if (lore != null && !lore.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			for (String line : lore) {
				builder.append(line + ";");
			}
			try {
				this.lore.set(builder.substring(0, builder.length() - 1));
			} catch (InstructionParseException e) {}
		}
	}
	
	/**
	 * Creates new instance of the quest item using the ID from items.yml file.
	 * 
	 * @param packName
	 *            name of the package to load it from
	 * @param itemID
	 *            ID of the item
	 * @throws InstructionParseException
	 *             when item parsing goes wrong
	 */
	public QuestItem(ItemID itemID) throws InstructionParseException {
		this(itemID.generateInstruction());
	}
	
	/**
	 * Creates new instance of the quest item using the Instruction object.
	 * 
	 * @param instruction
	 *            Instruction object
	 * @throws InstructionParseException
	 *             when item parsing goes wrong
	 */
	public QuestItem(Instruction instruction) throws InstructionParseException {
		this(instruction.getInstruction());
	}

	/**
	 * Creates new instance of the quest item using the instruction string.
	 * 
	 * @param instruction
	 *            instruction String
	 * @throws InstructionParseException
	 *             when item parsing goes wrong
	 */
	public QuestItem(String instruction) throws InstructionParseException {
		if (instruction == null)
			throw new NullPointerException("Item instruction is null");
		String[] parts = instruction.split(" ");
		if (parts.length < 1) {
			throw new InstructionParseException("Not enough arguments");
		}
		material = Material.matchMaterial(parts[0]);
		if (material == null) {
			throw new InstructionParseException("Unknown item type: " + parts[0]);
		}
		for (String part : parts) {
			if (part.startsWith("data:")) {
				data.set(cut(part));
			} else if (part.toLowerCase().startsWith("enchants:")) {
				enchants.set(cut(part));
			} else if (part.toLowerCase().equals("enchants-containing")) {
				enchants.setNotExact();
			} else if (part.toLowerCase().startsWith("name:")) {
				name.set(cut(part));
			} else if (part.toLowerCase().startsWith("lore:")) {
				lore.set(cut(part));
			} else if (part.toLowerCase().equals("lore-containing")) {
				lore.setNotExact();
			} else if (part.toLowerCase().startsWith("unbreakable:")) {
				unbreakable.set(cut(part));
			} else if (part.toLowerCase().equals("unbreakable")) {
				unbreakable.set("true");
			} else if (part.toLowerCase().startsWith("title:")) {
				book.setTitle(cut(part));
			} else if (part.toLowerCase().startsWith("author:")) {
				book.setAuthor(cut(part));
			} else if (part.toLowerCase().startsWith("text:")) {
				book.setText(cut(part));
			} else if (part.toLowerCase().startsWith("type:")) {
				potion.setType(cut(part));
			} else if (part.toLowerCase().equals("extended")) {
				potion.setExtended("true");
			} else if (part.toLowerCase().startsWith("extended:")) {
				potion.setExtended(cut(part));
			} else if (part.toLowerCase().equals("upgraded")) {
				potion.setUpgraded("true");
			} else if (part.toLowerCase().startsWith("upgraded:")) {
				potion.setUpgraded(cut(part));
			} else if (part.toLowerCase().startsWith("effects:")) {
				potion.setCustom(cut(part));
			} else if (part.toLowerCase().equals("effects-containing")) {
				potion.setNotExact();
			} else if (part.toLowerCase().startsWith("owner:")) {
				head.set(cut(part));
			} else if (part.toLowerCase().startsWith("color:")) {
				color.set(cut(part));
			}
		}
	}
	
	private static String cut(String uncut) {
		return uncut.substring(uncut.indexOf(':') + 1);
	}

	@Override
	public boolean equals(Object o) {
		QuestItem item;
		if (o instanceof QuestItem) {
			item = (QuestItem) o;
		} else {
			return false;
		}
		if (item.material != material) {
			return false;
		}
		if (!item.data.equals(data)) {
			return false;
		}
		if (!item.unbreakable.equals(unbreakable)) {
			return false;
		}
		if (!item.enchants.equals(enchants)) {
			return false;
		}
		if (!item.lore.equals(lore)) {
			return false;
		}
		if (!item.name.equals(name)) {
			return false;
		}
		if (!item.potion.equals(potion)) {
			return false;
		}
		if (!item.book.equals(book)) {
			return false;
		}
		if (!item.head.equals(head)) {
			return false;
		}
		if (!item.color.equals(color)) {
			return false;
		}
		return true;
	}

	/**
	 * Compares ItemStack to the quest item.
	 * 
	 * @param item
	 *            ItemStack to compare
	 * @return true if the item matches
	 */
	@SuppressWarnings("deprecation")
	public boolean compare(ItemStack item) {
		// basic item checks
		if (item == null) {
			return false;
		}
		if (item.getType() != material) {
			return false;
		}
		// basic meta checks
		ItemMeta meta = item.getItemMeta();
		if (!data.check(item.getData().getData())) {
			return false;
		}
		if (!name.check(meta.getDisplayName())) {
			return false;
		}
		if (!lore.check(meta.getLore())) {
			return false;
		}
		if (!unbreakable.check(meta.spigot().isUnbreakable())) {
			return false;
		}
		// advanced meta checks
		if (meta instanceof EnchantmentStorageMeta) {
			EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) meta;
			if (!enchants.check(enchantMeta.getStoredEnchants())) {
				return false;
			}
		} else {
			if (!enchants.check(item.getEnchantments())) {
				return false;
			}
		}
		if (meta instanceof PotionMeta) {
			PotionMeta potionMeta = (PotionMeta) meta;
			if (!potion.checkBase(potionMeta.getBasePotionData())) {
				return false;
			}
			if (!potion.checkCustom(potionMeta.getCustomEffects())) {
				return false;
			}
		}
		if (meta instanceof BookMeta) {
			BookMeta bookMeta = (BookMeta) item.getItemMeta();
			if (!book.checkTitle(bookMeta.getTitle())) {
				return false;
			}
			if (!book.checkAuthor(bookMeta.getAuthor())) {
				return false;
			}
			if (!book.checkText(bookMeta.getPages())) {
				return false;
			}
		}
		if (meta instanceof SkullMeta) {
			SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
			if (!head.check(skullMeta.getOwner())) {
				return false;
			}
		}
		if (meta instanceof LeatherArmorMeta) {
			LeatherArmorMeta armorMeta = (LeatherArmorMeta) item.getItemMeta();
			if (!color.check(armorMeta.getColor())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Generates this quest item as ItemStack with given amount.
	 * 
	 * @param stackSize
	 *            size of generated stack
	 * @return the ItemStack equal to this quest item
	 */
	public ItemStack generate(int stackSize) {
		ItemStack item = new ItemStack(material, stackSize, data.get());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name.get());
		meta.setLore(lore.get());
		meta.spigot().setUnbreakable(unbreakable.get());
		if (meta instanceof EnchantmentStorageMeta) {
			EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) meta;
			// why no bulk adding method?!
			Map<Enchantment, Integer> map = enchants.get();
			for (Entry<Enchantment, Integer> e : map.entrySet()) {
				enchantMeta.addStoredEnchant(e.getKey(), e.getValue(), true);
			}
		} else {
			Map<Enchantment, Integer> map = enchants.get();
			for (Entry<Enchantment, Integer> e : map.entrySet()) {
				meta.addEnchant(e.getKey(), e.getValue(), true);
			}
		}
		if (meta instanceof PotionMeta) {
			PotionMeta potionMeta = (PotionMeta) meta;
			potionMeta.setBasePotionData(potion.getBase());
			// this is getting ridiculous
			for (PotionEffect effect : potion.getCustom()) {
				potionMeta.addCustomEffect(effect, true);
			}
		}
		if (meta instanceof BookMeta) {
			BookMeta bookMeta = (BookMeta) meta;
			bookMeta.setTitle(book.getTitle());
			bookMeta.setAuthor(book.getAuthor());
			bookMeta.setPages(book.getText());
		}
		if (meta instanceof SkullMeta) {
			SkullMeta skullMeta = (SkullMeta) meta;
			skullMeta.setOwner(head.get());
		}
		if (meta instanceof LeatherArmorMeta) {
			LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
			armorMeta.setColor(color.get());
		}
		item.setItemMeta(meta);
		return item;
	}
	
	public enum Existence {
		REQUIRED, FORBIDDEN, WHATEVER
	}
	
	public enum Number {
		EQUAL, MORE, LESS, WHATEVER
	}

	/**
	 * @return the material
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * @return the data value
	 */
	public short getData() {
		return data.get();
	}

	/**
	 * @return the map of enchantments and their levels
	 */
	public Map<Enchantment, Integer> getEnchants() {
		return enchants.get();
	}

	/**
	 * @return the display name or null if there is no name
	 */
	public String getName() {
		return name.get();
	}

	/**
	 * @return the list of lore lines, can be empty
	 */
	public List<String> getLore() {
		return lore.get();
	}

	/**
	 * @return the title of a book or null if it's not a book
	 */
	public String getTitle() {
		return book.getTitle();
	}

	/**
	 * @return the author of a book or null if it's not a book
	 */
	public String getAuthor() {
		return book.getAuthor();
	}

	/**
	 * @return the pages from the book or null if it's not a book
	 */
	public List<String> getText() {
		return book.getText();
	}

	/**
	 * @return the list of custom effects of the potion
	 */
	public List<PotionEffect> getEffects() {
		return potion.getCustom();
	}

	/**
	 * @return color of the leather armor
	 */
	public Color getColor() {
		return color.get();
	}

	/**
	 * @return owner of the head
	 */
	public String getOwner() {
		return head.get();
	}

	/**
	 * @return the base data of the potion
	 */
	public PotionData getBaseEffect() {
		return potion.getBase();
	}
	
	/**
	 * @return if the item has "Unbreakable" tag
	 */
	public boolean isUnbreakable() {
		return unbreakable.get();
	}
	
	/**
	 * Converts ItemStack to string, which can be later parsed by QuestItem
	 * 
	 * @param item
	 *            ItemStack to convert
	 * @return converted string
	 */
	@SuppressWarnings("deprecation")
	public static String itemToString(ItemStack item) {
		String name = "";
		String lore = "";
		String enchants = "";
		String title = "";
		String text = "";
		String author = "";
		String effects = "";
		String color = "";
		String owner = "";
		ItemMeta meta = item.getItemMeta();
		if (meta.hasDisplayName()) {
			name = " name:" + meta.getDisplayName().replace(" ", "_");
		}
		if (meta.hasLore()) {
			StringBuilder string = new StringBuilder();
			for (String line : meta.getLore()) {
				string.append(line + ";");
			}
			lore = " lore:" + string.substring(0, string.length() - 1).replace(" ", "_");
		}
		if (meta.hasEnchants()) {
			StringBuilder string = new StringBuilder();
			for (Enchantment enchant : meta.getEnchants().keySet()) {
				string.append(enchant.getName() + ":" + meta.getEnchants().get(enchant) + ",");
			}
			enchants = " enchants:" + string.substring(0, string.length() - 1);
		}
		if (meta instanceof BookMeta) {
			BookMeta bookMeta = (BookMeta) meta;
			if (bookMeta.hasAuthor()) {
				author = " author:" + bookMeta.getAuthor().replace(" ", "_");
			}
			if (bookMeta.hasTitle()) {
				title = " title:" + bookMeta.getTitle().replace(" ", "_");
			}
			if (bookMeta.hasPages()) {
				StringBuilder strBldr = new StringBuilder();
				for (String page : bookMeta.getPages()) {
					if (page.startsWith("\"") && page.endsWith("\"")) {
						page = page.substring(1, page.length() - 1);
					}
					// this will remove black color code between lines
					// Bukkit is adding it for some reason (probably to mess people's code)
					strBldr.append(page.replace(" ", "_").replaceAll("(§0)?\\n(§0)?", "\\\\n") + "|");
				}
				text = " text:" + strBldr.substring(0, strBldr.length() - 1);
			}
		}
		if (meta instanceof PotionMeta) {
			PotionMeta potionMeta = (PotionMeta) meta;
			PotionData pData = potionMeta.getBasePotionData();
			effects = " type:" + pData.getType().toString() + (pData.isExtended() ? " extended" : "")
					+ (pData.isUpgraded() ? " upgraded" : "");
			if (potionMeta.hasCustomEffects()) {
				StringBuilder string = new StringBuilder();
				for (PotionEffect effect : potionMeta.getCustomEffects()) {
					int power = effect.getAmplifier() + 1;
					int duration = (effect.getDuration() - (effect.getDuration() % 20)) / 20;
					string.append(effect.getType().getName() + ":" + power + ":" + duration + ",");
				}
				effects += " effects:" + string.substring(0, string.length() - 1);
			}
		}
		if (meta instanceof LeatherArmorMeta) {
			LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
			if (!armorMeta.getColor().equals(Bukkit.getServer().getItemFactory().getDefaultLeatherColor())) {
				color = " color:" + armorMeta.getColor().asRGB();
			}
		}
		if (meta instanceof EnchantmentStorageMeta) {
			EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) meta;
			if (storageMeta.hasStoredEnchants()) {
				StringBuilder string = new StringBuilder();
				for (Enchantment enchant : storageMeta.getStoredEnchants().keySet()) {
					string.append(enchant.getName() + ":" + storageMeta.getStoredEnchants().get(enchant) + ",");
				}
				enchants = " enchants:" + string.substring(0, string.length() - 1);
			}
		}
		if (meta instanceof SkullMeta) {
			SkullMeta skullMeta = (SkullMeta) meta;
			if (skullMeta.hasOwner()) {
				owner = " owner:" + skullMeta.getOwner();
			}
		}
		// put it all together in a single string
		return item.getType() + " data:" + item.getData().getData() + name + lore + enchants + title + author + text
				+ effects + color + owner;
	}
}
