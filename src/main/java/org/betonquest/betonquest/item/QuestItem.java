package org.betonquest.betonquest.item;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.item.typehandler.BookHandler;
import org.betonquest.betonquest.item.typehandler.ColorHandler;
import org.betonquest.betonquest.item.typehandler.CustomModelDataHandler;
import org.betonquest.betonquest.item.typehandler.DurabilityHandler;
import org.betonquest.betonquest.item.typehandler.EnchantmentsHandler;
import org.betonquest.betonquest.item.typehandler.FireworkHandler;
import org.betonquest.betonquest.item.typehandler.FlagHandler;
import org.betonquest.betonquest.item.typehandler.HandlerUtil;
import org.betonquest.betonquest.item.typehandler.HeadHandler;
import org.betonquest.betonquest.item.typehandler.LoreHandler;
import org.betonquest.betonquest.item.typehandler.NameHandler;
import org.betonquest.betonquest.item.typehandler.PotionHandler;
import org.betonquest.betonquest.item.typehandler.UnbreakableHandler;
import org.betonquest.betonquest.utils.BlockSelector;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Represents an item handled by the configuration.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.GodClass", "PMD.CommentRequired", "PMD.CognitiveComplexity",
        "PMD.CouplingBetweenObjects"})
public class QuestItem {
    public static final String NONE_KEY = "none";

    private final BlockSelector selector;

    private final DurabilityHandler durability = new DurabilityHandler();

    private final NameHandler name = new NameHandler();

    private final LoreHandler lore = new LoreHandler();

    private final EnchantmentsHandler enchants = new EnchantmentsHandler();

    private final UnbreakableHandler unbreakable = new UnbreakableHandler();

    private final PotionHandler potion = new PotionHandler();

    private final BookHandler book = new BookHandler();

    private final HeadHandler head = HeadHandler.getServerInstance();

    private final ColorHandler color = new ColorHandler();

    private final FireworkHandler firework = new FireworkHandler();

    private final CustomModelDataHandler customModelData = new CustomModelDataHandler();

    private final FlagHandler flags = new FlagHandler();

    /**
     * Creates new instance of the quest item using the ID
     *
     * @param itemID ID of the item
     * @throws InstructionParseException when item parsing goes wrong
     */
    public QuestItem(final ItemID itemID) throws InstructionParseException {
        this(itemID.getInstruction());
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
    @SuppressWarnings({"PMD.NcssCount", "PMD.AvoidLiteralsInIfCondition"})
    public QuestItem(final String instruction) throws InstructionParseException {
        final String[] parts = HandlerUtil.getNNSplit(instruction, "Item instruction is null", " ");
        selector = new BlockSelector(parts[0]);

        // Skip the block selector part to process remaining arguments
        for (int i = 1; i < parts.length; i++) {
            final String part = parts[i];
            final String argumentName = getArgumentName(part.toLowerCase(Locale.ROOT));
            final String data = getArgumentData(part);

            switch (argumentName) {
                case "durability" -> durability.set(data);
                case "enchants" -> enchants.set(data);
                case "enchants-containing" -> enchants.setNotExact();
                case "name" -> name.set(data);
                case "lore" -> lore.set(data);
                case "lore-containing" -> lore.setNotExact();
                case "unbreakable" -> {
                    if ("unbreakable".equals(data)) {
                        unbreakable.set("true");
                    } else {
                        unbreakable.set(data);
                    }
                }
                case "custom-model-data" -> customModelData.parse(data);
                case "no-custom-model-data" -> customModelData.forbid();
                case "title" -> book.setTitle(data);
                case "author" -> book.setAuthor(data);
                case "text" -> book.setText(data);
                case "type" -> potion.setType(data);
                case "extended" -> {
                    if ("extended".equals(data)) {
                        potion.setExtended("true");
                    } else {
                        potion.setExtended(data);
                    }
                }
                case "upgraded" -> {
                    if ("upgraded".equals(data)) {
                        potion.setUpgraded("true");
                    } else {
                        potion.setUpgraded(data);
                    }
                }
                case "effects" -> potion.setCustom(data);
                case "effects-containing" -> potion.setNotExact();
                case HeadHandler.META_OWNER -> head.setOwner(data);
                case HeadHandler.META_PLAYER_ID -> head.setPlayerId(data);
                case HeadHandler.META_TEXTURE -> head.setTexture(data);
                case "color" -> color.set(data);
                case "firework" -> firework.setEffects(data);
                case "power" -> firework.setPower(data);
                case "firework-containing" -> firework.setNotExact();
                case "flags" -> flags.parse(data);
                //catch empty string caused by multiple whitespaces in instruction split
                case "" -> {
                }
                default -> throw new InstructionParseException("Unknown argument: " + argumentName);
            }
        }
    }

    /**
     * Converts ItemStack to string, which can be later parsed by QuestItem
     *
     * @param item ItemStack to convert
     * @return converted string
     */
    @SuppressWarnings({"PMD.NcssCount", "PMD.NPathComplexity"})
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
        String skull = "";
        String firework = "";
        String unbreakable = "";
        String customModelData = "";
        String flags = "";
        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (meta instanceof final Damageable damageable) {
                durability = " durability:" + damageable.getDamage();
            }
            if (meta.hasDisplayName()) {
                name = " name:" + meta.getDisplayName().replace(" ", "_");
            }
            if (meta.hasLore()) {
                final StringBuilder string = new StringBuilder();
                for (final String line : meta.getLore()) {
                    string.append(line).append(';');
                }
                lore = " lore:" + string.substring(0, string.length() - 1).replace(" ", "_").replace("§", "&");
            }
            if (meta.hasEnchants()) {
                final StringBuilder string = new StringBuilder();
                for (final Enchantment enchant : meta.getEnchants().keySet()) {
                    string.append(enchant.getName()).append(':').append(meta.getEnchants().get(enchant)).append(',');
                }
                enchants = " enchants:" + string.substring(0, string.length() - 1);
            }
            if (meta.isUnbreakable()) {
                unbreakable = " unbreakable";
            }
            if (meta.hasCustomModelData()) {
                customModelData = " custom-model-data:" + meta.getCustomModelData();
            }
            if (meta instanceof final BookMeta bookMeta) {
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
                        strBldr.append(processedPage.replace(" ", "_").replaceAll("(§0)?\\n(§0)?", "\\\\n")).append('|');
                    }
                    text = " text:" + strBldr.substring(0, strBldr.length() - 1);
                }
            }
            if (meta instanceof final PotionMeta potionMeta) {
                final PotionData pData = potionMeta.getBasePotionData();
                effects = " type:" + pData.getType() + (pData.isExtended() ? " extended" : "")
                        + (pData.isUpgraded() ? " upgraded" : "");
                if (potionMeta.hasCustomEffects()) {
                    final StringBuilder string = new StringBuilder();
                    for (final PotionEffect effect : potionMeta.getCustomEffects()) {
                        final int power = effect.getAmplifier() + 1;
                        final int duration = (effect.getDuration() - (effect.getDuration() % 20)) / 20;
                        string.append(effect.getType().getName()).append(':').append(power).append(':').append(duration).append(',');
                    }
                    effects += " effects:" + string.substring(0, string.length() - 1);
                }
            }
            if (meta instanceof final LeatherArmorMeta armorMeta
                    && !armorMeta.getColor().equals(Bukkit.getServer().getItemFactory().getDefaultLeatherColor())) {
                final DyeColor dyeColor = DyeColor.getByColor(armorMeta.getColor());
                color = " color:" + (dyeColor == null ? '#' + Integer.toHexString(armorMeta.getColor().asRGB()) : dyeColor.toString());
            }
            if (meta instanceof final EnchantmentStorageMeta storageMeta && storageMeta.hasStoredEnchants()) {
                final StringBuilder string = new StringBuilder();
                for (final Enchantment enchant : storageMeta.getStoredEnchants().keySet()) {
                    string.append(enchant.getName()).append(':').append(storageMeta.getStoredEnchants().get(enchant)).append(',');
                }
                enchants = " enchants:" + string.substring(0, string.length() - 1);
            }
            if (meta instanceof SkullMeta) {
                skull = HeadHandler.serializeSkullMeta((SkullMeta) meta);
            }
            if (meta instanceof final FireworkMeta fireworkMeta && fireworkMeta.hasEffects()) {
                final StringBuilder builder = new StringBuilder(17);
                builder.append(" firework:");
                for (final FireworkEffect effect : fireworkMeta.getEffects()) {
                    appendFireworkEffect(builder, effect);
                    builder.append(',');
                }
                builder.setLength(Math.max(builder.length() - 1, 0));
                builder.append(" power:").append(fireworkMeta.getPower());
                firework = builder.toString();
            }
            if (meta instanceof final FireworkEffectMeta fireworkMeta && fireworkMeta.hasEffect()) {
                final FireworkEffect effect = fireworkMeta.getEffect();
                final StringBuilder builder = new StringBuilder();
                appendFireworkEffect(builder, effect);
                builder.append(" firework:");
                firework = builder.toString();
            }
            if (!meta.getItemFlags().isEmpty()) {
                flags = " flags:" + String.join(",", meta.getItemFlags().stream().map(ItemFlag::name).sorted().toList());
            }
        }
        // put it all together in a single string
        return item.getType() + durability + name + lore + enchants + title + author + text
                + effects + color + skull + firework + unbreakable + customModelData + flags;
    }

    private static void appendFireworkEffect(final StringBuilder builder, final FireworkEffect effect) {
        builder.append(effect.getType()).append(':');
        for (final Color c : effect.getColors()) {
            final DyeColor dye = DyeColor.getByFireworkColor(c);
            builder.append(dye == null ? '#' + Integer.toHexString(c.asRGB()) : dye).append(';');
        }
        // remove last semicolon
        builder.setLength(Math.max(builder.length() - 1, 0));
        builder.append(':');
        for (final Color c : effect.getFadeColors()) {
            final DyeColor dye = DyeColor.getByFireworkColor(c);
            builder.append(dye == null ? '#' + Integer.toHexString(c.asRGB()) : dye).append(';');
        }
        builder.setLength(Math.max(builder.length() - 1, 0));
        builder.append(':').append(effect.hasTrail()).append(':').append(effect.hasFlicker());
    }

    /**
     * Returns the data behind the argument name.
     * If the argument does not contain a colon, it returns the full argument.
     *
     * @param argument the full argument
     * @return the data behind the argument name
     */
    private String getArgumentData(final String argument) {
        return argument.substring(argument.indexOf(':') + 1);
    }

    /**
     * Returns the argument name.
     * If the argument does not contain a colon, it returns the full argument.
     *
     * @param argument the full argument
     * @return the argument name
     */
    private String getArgumentName(final String argument) {
        if (argument.contains(":")) {
            return argument.substring(0, argument.indexOf(':'));
        }
        return argument;
    }

    @Override
    public boolean equals(@Nullable final Object other) {
        if (!(other instanceof final QuestItem item)) {
            return false;
        }
        return item.selector.equals(selector)
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
                && item.customModelData.equals(customModelData)
                && item.flags.equals(flags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selector, durability, name, lore, enchants, unbreakable, potion, book, head, color, firework, customModelData, flags);
    }

    /**
     * Compares ItemStack to the quest item.
     *
     * @param item ItemStack to compare
     * @return true if the item matches
     */
    @SuppressWarnings("PMD.NPathComplexity")
    public boolean compare(@Nullable final ItemStack item) {
        // basic item checks
        if (item == null) {
            return false;
        }
        if (!selector.match(item.getType())) {
            return false;
        }
        // basic meta checks
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return true;
        }
        if (!durability.check(meta instanceof Damageable ? ((Damageable) meta).getDamage() : 0)) {
            return false;
        }
        final String displayName = meta.hasDisplayName() ? meta.getDisplayName() : null;
        if (!name.check(displayName)) {
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
        if (!flags.check(meta)) {
            return false;
        }
        // advanced meta checks
        if (meta instanceof final EnchantmentStorageMeta enchantMeta) {
            if (!enchants.check(enchantMeta.getStoredEnchants())) {
                return false;
            }
        } else {
            if (!enchants.check(item.getEnchantments())) {
                return false;
            }
        }
        if (meta instanceof final PotionMeta potionMeta) {
            if (!potion.checkBase(potionMeta.getBasePotionData())) {
                return false;
            }
            if (!potion.checkCustom(potionMeta.getCustomEffects())) {
                return false;
            }
        }
        if (meta instanceof final BookMeta bookMeta) {
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
        if (meta instanceof final SkullMeta skullMeta && !head.check(skullMeta)) {
            return false;
        }
        if (meta instanceof final LeatherArmorMeta armorMeta && !color.check(armorMeta.getColor())) {
            return false;
        }
        if (meta instanceof final FireworkMeta fireworkMeta) {
            if (!firework.checkEffects(fireworkMeta.getEffects())) {
                return false;
            }
            if (!firework.checkPower(fireworkMeta.getPower())) {
                return false;
            }
        }
        if (meta instanceof final FireworkEffectMeta fireworkMeta) {
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
    public ItemStack generate(final int stackSize) {
        return generate(stackSize, null);
    }

    /**
     * Generates this quest item as ItemStack with given amount.
     *
     * @param stackSize size of generated stack
     * @param profile   profile parameter
     * @return the ItemStack equal to this quest item
     */
    @SuppressWarnings("PMD.NPathComplexity")
    public ItemStack generate(final int stackSize, @Nullable final Profile profile) {
        // Try resolve material directly
        final Material material = selector.getRandomMaterial();

        final ItemStack item = new ItemStack(material, stackSize);
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        meta.setDisplayName(name.get());
        meta.setLore(lore.get());
        meta.setUnbreakable(unbreakable.isUnbreakable());
        flags.get().forEach(meta::addItemFlags);
        if (customModelData.getExistence() == Existence.REQUIRED) {
            meta.setCustomModelData(customModelData.get());
        }
        if (meta instanceof final EnchantmentStorageMeta enchantMeta) {
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
        if (meta instanceof final PotionMeta potionMeta) {
            potionMeta.setBasePotionData(potion.getBase());
            for (final PotionEffect effect : potion.getCustom()) {
                potionMeta.addCustomEffect(effect, true);
            }
        }
        if (meta instanceof final BookMeta bookMeta) {
            bookMeta.setTitle(book.getTitle());
            bookMeta.setAuthor(book.getAuthor());
            bookMeta.setPages(book.getText());
        }
        if (meta instanceof SkullMeta) {
            head.populate((SkullMeta) meta, profile);
        }
        if (meta instanceof final LeatherArmorMeta armorMeta) {
            armorMeta.setColor(color.get());
        }
        if (meta instanceof final FireworkMeta fireworkMeta) {
            fireworkMeta.addEffects(firework.getEffects());
            fireworkMeta.setPower(firework.getPower());
        }
        if (meta instanceof final FireworkEffectMeta fireworkMeta) {
            final List<FireworkEffect> list = firework.getEffects();
            fireworkMeta.setEffect(list.isEmpty() ? null : list.get(0));
        }
        if (meta instanceof final Damageable damageableMeta) {
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
    public int getDurability() {
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
    @Nullable
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
     * @return owner of the head, used independently of player ID and texture
     */
    @Nullable
    public Profile getOwner() {
        return head.getOwner(null);
    }

    /**
     * @return playerId of the head, used in combination with the texture
     */
    @Nullable
    public UUID getPlayerId() {
        return head.getPlayerId();
    }

    /**
     * @return texture URL of the head, used in combination with the player ID
     */
    @Nullable
    public String getTexture() {
        return head.getTexture();
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

    /**
     * @return the set of ItemFlags
     */
    public Set<ItemFlag> getFlags() {
        return flags.get();
    }

    public enum Existence {
        REQUIRED, FORBIDDEN, WHATEVER
    }

    public enum Number {
        EQUAL, MORE, LESS, WHATEVER
    }
}
