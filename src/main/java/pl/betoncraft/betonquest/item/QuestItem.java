/*
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

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.id.ItemID;
import pl.betoncraft.betonquest.item.typehandler.*;
import pl.betoncraft.betonquest.utils.BlockSelector;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents an item handled by the configuration.
 *
 * @author Jakub Sapalski
 */
public class QuestItem {

    private BlockSelector selector;
    private DurabilityHandler durability = new DurabilityHandler();
    private NameHandler name = new NameHandler();
    private LoreHandler lore = new LoreHandler();
    private EnchantmentsHandler enchants = new EnchantmentsHandler();
    private UnbreakableHandler unbreakable = new UnbreakableHandler();
    private PotionHandler potion = new PotionHandler();
    private BookHandler book = new BookHandler();
    private HeadOwnerHandler head = new HeadOwnerHandler();
    private ColorHandler color = new ColorHandler();
    private FireworkHandler firework = new FireworkHandler();

    /**
     * Creates new instance of the quest item using the ID from items.yml file.
     *
     * @param itemID ID of the item
     * @throws InstructionParseException when item parsing goes wrong
     */
    public QuestItem(final ItemID itemID) throws InstructionParseException {
        this(itemID.generateInstruction());
    }

    /**
     * Creates new instance of the quest item using the Instruction object.
     *
     * @param instruction Instruction object
     * @throws InstructionParseException when item parsing goes wrong
     */
    public QuestItem(final Instruction instruction) throws InstructionParseException {
        this(instruction.getInstruction());
    }

    /**
     * Creates new instance of the quest item using the instruction string.
     *
     * @param instruction instruction String
     * @throws InstructionParseException when item parsing goes wrong
     */
    public QuestItem(final String instruction) throws InstructionParseException {
        if (instruction == null) {
            throw new NullPointerException("Item instruction is null");
        }
        final String[] parts = instruction.split(" ");
        if (parts.length < 1) {
            throw new InstructionParseException("Not enough arguments");
        }

        selector = new BlockSelector(parts[0]);
        if (!selector.isValid()) {
            throw new InstructionParseException("Invalid selector: " + selector.toString());
        }

        for (final String part : parts) {
            if (part.toLowerCase().startsWith("durability:")) {
                durability.set(cut(part));
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
            } else if (part.toLowerCase().startsWith("firework:")) {
                firework.setEffects(cut(part));
            } else if (part.toLowerCase().startsWith("power:")) {
                firework.setPower(cut(part));
            } else if (part.toLowerCase().equals("firework-containing")) {
                firework.setNotExact();
            }
        }
    }

    private static String cut(final String uncut) {
        return uncut.substring(uncut.indexOf(':') + 1);
    }

    /**
     * Converts ItemStack to string, which can be later parsed by QuestItem
     *
     * @param item ItemStack to convert
     * @return converted string
     */
    @SuppressWarnings("deprecation")
    public static String itemToString(final ItemStack item) {
        String name = "";
        String lore = "";
        String enchants = "";
        String title = "";
        String text = "";
        String author = "";
        String effects = "";
        String color = "";
        String owner = "";
        String firework = "";
        String unbreakable = "";
        final ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName()) {
            name = " name:" + meta.getDisplayName().replace(" ", "_");
        }
        if (meta.hasLore()) {
            final StringBuilder string = new StringBuilder();
            for (final String line : meta.getLore()) {
                string.append(line + ";");
            }
            lore = " lore:" + string.substring(0, string.length() - 1).replace(" ", "_").replace("§", "&");
        }
        if (meta.hasEnchants()) {
            final StringBuilder string = new StringBuilder();
            for (final Enchantment enchant : meta.getEnchants().keySet()) {
                string.append(enchant.getName() + ":" + meta.getEnchants().get(enchant) + ",");
            }
            enchants = " enchants:" + string.substring(0, string.length() - 1);
        }
        if (meta.isUnbreakable()) {
            unbreakable = " unbreakable";
        }
        if (meta instanceof BookMeta) {
            final BookMeta bookMeta = (BookMeta) meta;
            if (bookMeta.hasAuthor()) {
                author = " author:" + bookMeta.getAuthor().replace(" ", "_");
            }
            if (bookMeta.hasTitle()) {
                title = " title:" + bookMeta.getTitle().replace(" ", "_");
            }
            if (bookMeta.hasPages()) {
                final StringBuilder strBldr = new StringBuilder();
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
            final PotionMeta potionMeta = (PotionMeta) meta;
            final PotionData pData = potionMeta.getBasePotionData();
            effects = " type:" + pData.getType().toString() + (pData.isExtended() ? " extended" : "")
                    + (pData.isUpgraded() ? " upgraded" : "");
            if (potionMeta.hasCustomEffects()) {
                final StringBuilder string = new StringBuilder();
                for (final PotionEffect effect : potionMeta.getCustomEffects()) {
                    final int power = effect.getAmplifier() + 1;
                    final int duration = (effect.getDuration() - (effect.getDuration() % 20)) / 20;
                    string.append(effect.getType().getName() + ":" + power + ":" + duration + ",");
                }
                effects += " effects:" + string.substring(0, string.length() - 1);
            }
        }
        if (meta instanceof LeatherArmorMeta) {
            final LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
            if (!armorMeta.getColor().equals(Bukkit.getServer().getItemFactory().getDefaultLeatherColor())) {
                final DyeColor dyeColor = DyeColor.getByColor(armorMeta.getColor());
                color = " color:" + (dyeColor == null ? '#' + Integer.toHexString(armorMeta.getColor().asRGB()) : dyeColor.toString());
            }
        }
        if (meta instanceof EnchantmentStorageMeta) {
            final EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) meta;
            if (storageMeta.hasStoredEnchants()) {
                final StringBuilder string = new StringBuilder();
                for (final Enchantment enchant : storageMeta.getStoredEnchants().keySet()) {
                    string.append(enchant.getName() + ":" + storageMeta.getStoredEnchants().get(enchant) + ",");
                }
                enchants = " enchants:" + string.substring(0, string.length() - 1);
            }
        }
        if (meta instanceof SkullMeta) {
            final SkullMeta skullMeta = (SkullMeta) meta;
            if (skullMeta.hasOwner()) {
                owner = " owner:" + skullMeta.getOwner();
            }
        }
        if (meta instanceof FireworkMeta) {
            final FireworkMeta fireworkMeta = (FireworkMeta) meta;
            if (fireworkMeta.hasEffects()) {
                final StringBuilder builder = new StringBuilder();
                builder.append(" firework:");
                for (final FireworkEffect effect : fireworkMeta.getEffects()) {
                    builder.append(effect.getType() + ":");
                    for (final Color c : effect.getColors()) {
                        final DyeColor dye = DyeColor.getByFireworkColor(c);
                        builder.append((dye == null ? '#' + Integer.toHexString(c.asRGB()) : dye) + ";");
                    }
                    // remove last semicolon
                    builder.setLength(Math.max(builder.length() - 1, 0));
                    builder.append(":");
                    for (final Color c : effect.getFadeColors()) {
                        final DyeColor dye = DyeColor.getByFireworkColor(c);
                        builder.append((dye == null ? '#' + Integer.toHexString(c.asRGB()) : dye) + ";");
                    }
                    builder.setLength(Math.max(builder.length() - 1, 0));
                    builder.append(":" + effect.hasTrail() + ":" + effect.hasFlicker() + ",");
                }
                builder.setLength(Math.max(builder.length() - 1, 0));
                builder.append(" power:" + fireworkMeta.getPower());
                firework = builder.toString();
            }
        }
        if (meta instanceof FireworkEffectMeta) {
            final FireworkEffectMeta fireworkMeta = (FireworkEffectMeta) meta;
            if (fireworkMeta.hasEffect()) {
                final FireworkEffect effect = fireworkMeta.getEffect();
                final StringBuilder builder = new StringBuilder();
                builder.append(" firework:");
                builder.append(effect.getType() + ":");
                for (final Color c : effect.getColors()) {
                    final DyeColor dye = DyeColor.getByFireworkColor(c);
                    builder.append((dye == null ? '#' + Integer.toHexString(c.asRGB()) : dye) + ";");
                }
                // remove last semicolon
                builder.setLength(Math.max(builder.length() - 1, 0));
                builder.append(":");
                for (final Color c : effect.getFadeColors()) {
                    final DyeColor dye = DyeColor.getByFireworkColor(c);
                    builder.append((dye == null ? '#' + Integer.toHexString(c.asRGB()) : dye) + ";");
                }
                builder.setLength(Math.max(builder.length() - 1, 0));
                builder.append(":" + effect.hasTrail() + ":" + effect.hasFlicker());
            }
        }
        // put it all together in a single string
        return item.getType() + " durability:" + item.getDurability() + name + lore + enchants + title + author + text
                + effects + color + owner + firework + unbreakable;
    }

    @Override
    public boolean equals(final Object other) {
        final QuestItem item;
        if (other instanceof QuestItem) {
            item = (QuestItem) other;
        } else {
            return false;
        }
        if (item.selector != selector) {
            return false;
        }
        if (!item.durability.equals(durability)) {
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
        return item.firework.equals(firework);
    }

    /**
     * Compares ItemStack to the quest item.
     *
     * @param item ItemStack to compare
     * @return true if the item matches
     */
    @SuppressWarnings("deprecation")
    public boolean compare(final ItemStack item) {
        // basic item checks
        if (item == null) {
            return false;
        }
        if (!selector.match(item.getType())) {
            return false;
        }
        // basic meta checks
        if (!durability.check(item.getDurability())) {
            return false;
        }
        final ItemMeta meta = item.getItemMeta();
        if (!name.check(meta.getDisplayName())) {
            return false;
        }
        if (!lore.check(meta.getLore())) {
            return false;
        }
        if (!unbreakable.check(meta.isUnbreakable())) {
            return false;
        }
        // advanced meta checks
        if (meta instanceof EnchantmentStorageMeta) {
            final EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) meta;
            if (!enchants.check(enchantMeta.getStoredEnchants())) {
                return false;
            }
        } else {
            if (!enchants.check(item.getEnchantments())) {
                return false;
            }
        }
        if (meta instanceof PotionMeta) {
            final PotionMeta potionMeta = (PotionMeta) meta;
            if (!potion.checkBase(potionMeta.getBasePotionData())) {
                return false;
            }
            if (!potion.checkCustom(potionMeta.getCustomEffects())) {
                return false;
            }
        }
        if (meta instanceof BookMeta) {
            final BookMeta bookMeta = (BookMeta) item.getItemMeta();
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
            final SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
            if (!head.check(skullMeta.getOwner())) {
                return false;
            }
        }
        if (meta instanceof LeatherArmorMeta) {
            final LeatherArmorMeta armorMeta = (LeatherArmorMeta) item.getItemMeta();
            if (!color.check(armorMeta.getColor())) {
                return false;
            }
        }
        if (meta instanceof FireworkMeta) {
            final FireworkMeta fireworkMeta = (FireworkMeta) item.getItemMeta();
            if (!firework.checkEffects(fireworkMeta.getEffects())) {
                return false;
            }
            if (!firework.checkPower(fireworkMeta.getPower())) {
                return false;
            }
        }
        if (meta instanceof FireworkEffectMeta) {
            final FireworkEffectMeta fireworkMeta = (FireworkEffectMeta) item.getItemMeta();
            return firework.checkSingleEffect(fireworkMeta.getEffect());
        }
        return true;
    }

    /**
     * Generates this quest item as ItemStack with given amount.
     *
     * @param stackSize size of generated stack
     * @return the ItemStack equal to this quest item
     */
    @SuppressWarnings("deprecation")
    public ItemStack generate(final int stackSize) {
        // Try resolve material directly
        Material material = selector.getMaterial();
        if (material == null) {
            material = Material.ARROW;
        }

        final ItemStack item = new ItemStack(material, stackSize);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name.get());
        meta.setLore(lore.get());
        meta.setUnbreakable(unbreakable.isUnbreakable());
        if (meta instanceof EnchantmentStorageMeta) {
            final EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) meta;
            // why no bulk adding method?!
            final Map<Enchantment, Integer> map = enchants.get();
            for (final Entry<Enchantment, Integer> e : map.entrySet()) {
                enchantMeta.addStoredEnchant(e.getKey(), e.getValue(), true);
            }
        } else {
            final Map<Enchantment, Integer> map = enchants.get();
            for (final Entry<Enchantment, Integer> e : map.entrySet()) {
                meta.addEnchant(e.getKey(), e.getValue(), true);
            }
        }
        if (meta instanceof PotionMeta) {
            final PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.setBasePotionData(potion.getBase());
            for (final PotionEffect effect : potion.getCustom()) {
                potionMeta.addCustomEffect(effect, true);
            }
        }
        if (meta instanceof BookMeta) {
            final BookMeta bookMeta = (BookMeta) meta;
            bookMeta.setTitle(book.getTitle());
            bookMeta.setAuthor(book.getAuthor());
            bookMeta.setPages(book.getText());
        }
        if (meta instanceof SkullMeta) {
            final SkullMeta skullMeta = (SkullMeta) meta;
            skullMeta.setOwner(head.get());
        }
        if (meta instanceof LeatherArmorMeta) {
            final LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
            armorMeta.setColor(color.get());
        }
        if (meta instanceof FireworkMeta) {
            final FireworkMeta fireworkMeta = (FireworkMeta) meta;
            fireworkMeta.addEffects(firework.getEffects());
            fireworkMeta.setPower(firework.getPower());
        }
        if (meta instanceof FireworkEffectMeta) {
            final FireworkEffectMeta fireworkMeta = (FireworkEffectMeta) meta;
            final List<FireworkEffect> list = firework.getEffects();
            fireworkMeta.setEffect(list.isEmpty() ? null : list.get(0));
        }
        if (meta instanceof Damageable) {
            final Damageable damageableMeta = (Damageable) meta;
            damageableMeta.setDamage(getDurability());
        }
        item.setItemMeta(meta);
        return item;
    }

    /**
     * @return the material
     */
    public Material getMaterial() {
        final Material material = selector.getMaterial();
        return material == null ? Material.ARROW : material;
    }

    /**
     * @return the durability value
     */
    public short getDurability() {
        return durability.get();
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
        return unbreakable.isUnbreakable();
    }

    /**
     * @return the list of firework effects
     */
    public List<FireworkEffect> getFireworkEffects() {
        return firework.getEffects();
    }

    /**
     * @return power of the firework
     */
    public int getPower() {
        return firework.getPower();
    }

    public enum Existence {
        REQUIRED, FORBIDDEN, WHATEVER
    }

    public enum Number {
        EQUAL, MORE, LESS, WHATEVER
    }
}
