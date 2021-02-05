package org.betonquest.betonquest.item;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.item.typehandler.*;
import org.betonquest.betonquest.utils.BlockSelector;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * Represents an item handled by the configuration.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.GodClass", "PMD.CommentRequired"})
public class QuestItem {

    private final BlockSelector selector;
    private final DurabilityHandler durability = new DurabilityHandler();
    private final NameHandler name = new NameHandler();
    private final LoreHandler lore = new LoreHandler();
    private final EnchantmentsHandler enchants = new EnchantmentsHandler();
    private final UnbreakableHandler unbreakable = new UnbreakableHandler();
    private final PotionHandler potion = new PotionHandler();
    private final BookHandler book = new BookHandler();
    private final HeadOwnerHandler head = new HeadOwnerHandler();
    private final ColorHandler color = new ColorHandler();
    private final FireworkHandler firework = new FireworkHandler();
    private final CustomModelDataHandler customModelData = new CustomModelDataHandler();

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
    @SuppressWarnings("PMD.NcssCount")
    public QuestItem(final String instruction) throws InstructionParseException {
        if (instruction == null) {
            throw new InstructionParseException("Item instruction is null");
        }
        final String[] parts = instruction.split(" ");
        if (parts.length <= 0) {
            throw new InstructionParseException("Not enough arguments");
        }

        selector = new BlockSelector(parts[0]);

        for (final String part : parts) {
            if (part.toLowerCase(Locale.ROOT).startsWith("durability:")) {
                durability.set(cut(part));
            } else if (part.toLowerCase(Locale.ROOT).startsWith("enchants:")) {
                enchants.set(cut(part));
            } else if ("enchants-containing".equals(part.toLowerCase(Locale.ROOT))) {
                enchants.setNotExact();
            } else if (part.toLowerCase(Locale.ROOT).startsWith("name:")) {
                name.set(cut(part));
            } else if (part.toLowerCase(Locale.ROOT).startsWith("lore:")) {
                lore.set(cut(part));
            } else if ("lore-containing".equals(part.toLowerCase(Locale.ROOT))) {
                lore.setNotExact();
            } else if (part.toLowerCase(Locale.ROOT).startsWith("unbreakable:")) {
                unbreakable.set(cut(part));
            } else if ("unbreakable".equals(part.toLowerCase(Locale.ROOT))) {
                unbreakable.set("true");
            } else if (part.toLowerCase(Locale.ROOT).startsWith("custom-model-data:")) {
                customModelData.parse(cut(part));
            } else if (part.toLowerCase(Locale.ROOT).startsWith("no-custom-model-data")) {
                customModelData.forbid();
            } else if (part.toLowerCase(Locale.ROOT).startsWith("title:")) {
                book.setTitle(cut(part));
            } else if (part.toLowerCase(Locale.ROOT).startsWith("author:")) {
                book.setAuthor(cut(part));
            } else if (part.toLowerCase(Locale.ROOT).startsWith("text:")) {
                book.setText(cut(part));
            } else if (part.toLowerCase(Locale.ROOT).startsWith("type:")) {
                potion.setType(cut(part));
            } else if ("extended".equals(part.toLowerCase(Locale.ROOT))) {
                potion.setExtended("true");
            } else if (part.toLowerCase(Locale.ROOT).startsWith("extended:")) {
                potion.setExtended(cut(part));
            } else if ("upgraded".equals(part.toLowerCase(Locale.ROOT))) {
                potion.setUpgraded("true");
            } else if (part.toLowerCase(Locale.ROOT).startsWith("upgraded:")) {
                potion.setUpgraded(cut(part));
            } else if (part.toLowerCase(Locale.ROOT).startsWith("effects:")) {
                potion.setCustom(cut(part));
            } else if ("effects-containing".equals(part.toLowerCase(Locale.ROOT))) {
                potion.setNotExact();
            } else if (part.toLowerCase(Locale.ROOT).startsWith("owner:")) {
                head.set(cut(part));
            } else if (part.toLowerCase(Locale.ROOT).startsWith("color:")) {
                color.set(cut(part));
            } else if (part.toLowerCase(Locale.ROOT).startsWith("firework:")) {
                firework.setEffects(cut(part));
            } else if (part.toLowerCase(Locale.ROOT).startsWith("power:")) {
                firework.setPower(cut(part));
            } else if ("firework-containing".equals(part.toLowerCase(Locale.ROOT))) {
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
    @SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NcssCount", "PMD.NPathComplexity"})
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public static String itemToString(final ItemStack item) {
        String durability = "";
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
        String customModelData = "";
        final ItemMeta meta = item.getItemMeta();
        if (item.getDurability() != 0) {
            durability = " durability:" + item.getDurability();
        }
        if (meta.hasDisplayName()) {
            name = " name:" + meta.getDisplayName().replace(" ", "_");
        }
        if (meta.hasLore()) {
            final StringBuilder string = new StringBuilder();
            for (final String line : meta.getLore()) {
                string.append(line).append(";");
            }
            lore = " lore:" + string.substring(0, string.length() - 1).replace(" ", "_").replace("§", "&");
        }
        if (meta.hasEnchants()) {
            final StringBuilder string = new StringBuilder();
            for (final Enchantment enchant : meta.getEnchants().keySet()) {
                string.append(enchant.getName()).append(":").append(meta.getEnchants().get(enchant)).append(",");
            }
            enchants = " enchants:" + string.substring(0, string.length() - 1);
        }
        if (meta.isUnbreakable()) {
            unbreakable = " unbreakable";
        }
        if (meta.hasCustomModelData()) {
            customModelData = " custom-model-data:" + meta.getCustomModelData();
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
                for (final String page : bookMeta.getPages()) {
                    String processedPage = page;
                    if (processedPage.startsWith("\"") && processedPage.endsWith("\"")) {
                        processedPage = processedPage.substring(1, processedPage.length() - 1);
                    }
                    // this will remove black color code between lines
                    // Bukkit is adding it for some reason (probably to mess people's code)
                    strBldr.append(processedPage.replace(" ", "_").replaceAll("(§0)?\\n(§0)?", "\\\\n")).append("|");
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
                    string.append(effect.getType().getName()).append(":").append(power).append(":").append(duration).append(",");
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
                    string.append(enchant.getName()).append(":").append(storageMeta.getStoredEnchants().get(enchant)).append(",");
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
                    builder.append(effect.getType()).append(":");
                    for (final Color c : effect.getColors()) {
                        final DyeColor dye = DyeColor.getByFireworkColor(c);
                        builder.append(dye == null ? '#' + Integer.toHexString(c.asRGB()) : dye).append(";");
                    }
                    // remove last semicolon
                    builder.setLength(Math.max(builder.length() - 1, 0));
                    builder.append(":");
                    for (final Color c : effect.getFadeColors()) {
                        final DyeColor dye = DyeColor.getByFireworkColor(c);
                        builder.append(dye == null ? '#' + Integer.toHexString(c.asRGB()) : dye).append(";");
                    }
                    builder.setLength(Math.max(builder.length() - 1, 0));
                    builder.append(":").append(effect.hasTrail()).append(":").append(effect.hasFlicker()).append(",");
                }
                builder.setLength(Math.max(builder.length() - 1, 0));
                builder.append(" power:").append(fireworkMeta.getPower());
                firework = builder.toString();
            }
        }
        if (meta instanceof FireworkEffectMeta) {
            final FireworkEffectMeta fireworkMeta = (FireworkEffectMeta) meta;
            if (fireworkMeta.hasEffect()) {
                final FireworkEffect effect = fireworkMeta.getEffect();
                final StringBuilder builder = new StringBuilder();
                builder.append(" firework:").append(effect.getType()).append(":");
                for (final Color c : effect.getColors()) {
                    final DyeColor dye = DyeColor.getByFireworkColor(c);
                    builder.append(dye == null ? '#' + Integer.toHexString(c.asRGB()) : dye).append(";");
                }
                // remove last semicolon
                builder.setLength(Math.max(builder.length() - 1, 0));
                builder.append(":");
                for (final Color c : effect.getFadeColors()) {
                    final DyeColor dye = DyeColor.getByFireworkColor(c);
                    builder.append(dye == null ? '#' + Integer.toHexString(c.asRGB()) : dye).append(";");
                }
                builder.setLength(Math.max(builder.length() - 1, 0));
                builder.append(":").append(effect.hasTrail()).append(":").append(effect.hasFlicker());
            }
        }
        // put it all together in a single string
        return item.getType() + durability + name + lore + enchants + title + author + text
                + effects + color + owner + firework + unbreakable + customModelData;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof QuestItem)) {
            return false;
        }
        final QuestItem item = (QuestItem) other;
        return item.selector == selector
                && item.durability.equals(durability)
                && item.unbreakable.equals(unbreakable)
                && item.enchants.equals(enchants)
                && item.lore.equals(lore)
                && item.name.equals(name)
                && item.potion.equals(potion)
                && item.book.equals(book)
                && item.head.equals(head)
                && item.color.equals(color)
                && item.firework.equals(firework)
                && item.customModelData.equals(customModelData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selector, durability, name, lore, enchants, unbreakable, potion, book, head, color, firework, customModelData);
    }

    /**
     * Compares ItemStack to the quest item.
     *
     * @param item ItemStack to compare
     * @return true if the item matches
     */
    @SuppressWarnings("PMD.NPathComplexity")
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
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
        if (!customModelData.check(meta)) {
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
    @SuppressWarnings("PMD.NPathComplexity")
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public ItemStack generate(final int stackSize) {
        // Try resolve material directly
        final Material material = selector.getRandomMaterial();

        final ItemStack item = new ItemStack(material, stackSize);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name.get());
        meta.setLore(lore.get());
        meta.setUnbreakable(unbreakable.isUnbreakable());
        if (customModelData.getExistence() == Existence.REQUIRED) {
            meta.setCustomModelData(customModelData.get());
        }
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
        return selector.getRandomMaterial();
    }

    /**
     * @return the durability value
     */
    @SuppressWarnings("PMD.AvoidUsingShortType")
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
     * @return if the item has custom model data
     */
    public boolean hasCustomModelData() {
        return customModelData.has();
    }

    /**
     * @return the custom model data (check {@link #hasCustomModelData()} before)
     */
    public int getCustomModelData() {
        return customModelData.get();
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
