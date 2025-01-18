package org.betonquest.betonquest.item;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.item.typehandler.BookHandler;
import org.betonquest.betonquest.item.typehandler.ColorHandler;
import org.betonquest.betonquest.item.typehandler.CustomModelDataHandler;
import org.betonquest.betonquest.item.typehandler.DurabilityHandler;
import org.betonquest.betonquest.item.typehandler.EnchantmentsHandler;
import org.betonquest.betonquest.item.typehandler.FireworkHandler;
import org.betonquest.betonquest.item.typehandler.FlagHandler;
import org.betonquest.betonquest.item.typehandler.HandlerUtil;
import org.betonquest.betonquest.item.typehandler.HeadHandler;
import org.betonquest.betonquest.item.typehandler.ItemMetaHandler;
import org.betonquest.betonquest.item.typehandler.LoreHandler;
import org.betonquest.betonquest.item.typehandler.NameHandler;
import org.betonquest.betonquest.item.typehandler.PotionHandler;
import org.betonquest.betonquest.item.typehandler.UnbreakableHandler;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Represents an item handled by the configuration.
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.CouplingBetweenObjects"})
public class QuestItem {
    /**
     * Static Handlers for the {@link #itemToString(ItemStack)} method.
     */
    private static final List<ItemMetaHandler<? extends ItemMeta>> STATIC_HANDLERS = List.of(
            new DurabilityHandler(), new NameHandler(), new LoreHandler(), new EnchantmentsHandler(),
            new BookHandler(), new PotionHandler(), new ColorHandler(), HeadHandler.getServerInstance(),
            new FireworkHandler(), new UnbreakableHandler(), new CustomModelDataHandler(), new FlagHandler()
    );

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
     * Handler in the order of {@link #itemToString(ItemStack)}.
     */
    private final List<ItemMetaHandler<? extends ItemMeta>> handlers = List.of(
            durability, name, lore, enchants, book, potion,
            color, head, firework, unbreakable, customModelData, flags);

    /**
     * Creates new instance of the quest item using the ID.
     *
     * @param itemID ID of the item
     * @throws QuestException when item parsing goes wrong
     */
    public QuestItem(final ItemID itemID) throws QuestException {
        this(itemID.getInstruction());
    }

    /**
     * Creates new instance of the quest item using the Instruction object.
     *
     * @param instruction Instruction object
     * @throws QuestException when item parsing goes wrong
     */
    public QuestItem(final Instruction instruction) throws QuestException {
        this(instruction.toString());
    }

    /**
     * Creates new instance of the quest item using the instruction string.
     *
     * @param instruction instruction String
     * @throws QuestException when item parsing goes wrong
     */
    public QuestItem(final String instruction) throws QuestException {
        final String[] parts = HandlerUtil.getNNSplit(instruction, "Item instruction is null", " ");
        selector = new BlockSelector(parts[0]);

        final Map<String, ItemMetaHandler<?>> keyToHandler = new HashMap<>();
        for (final ItemMetaHandler<?> handler : handlers) {
            for (final String key : handler.keys()) {
                keyToHandler.put(key, handler);
            }
        }

        // Skip the block selector part to process remaining arguments
        for (int i = 1; i < parts.length; i++) {
            final String part = parts[i];
            if (part.isEmpty()) {
                continue; //catch empty string caused by multiple whitespaces in instruction split
            }

            final String argumentName = getArgumentName(part.toLowerCase(Locale.ROOT));
            final String data = getArgumentData(part);

            final ItemMetaHandler<?> handler = Utils.getNN(keyToHandler.get(argumentName), "Unknown argument: " + argumentName);
            handler.set(argumentName, data);
        }
    }

    /**
     * Converts ItemStack to string, which can be later parsed by QuestItem.
     *
     * @param item ItemStack to convert
     * @return converted string
     */
    public static String itemToString(final ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item.getType().toString();
        }

        final StringBuilder builder = new StringBuilder();
        for (final ItemMetaHandler<? extends ItemMeta> staticHandler : STATIC_HANDLERS) {
            final String serialize = staticHandler.rawSerializeToString(meta);
            if (serialize != null) {
                builder.append(' ').append(serialize);
            }
        }

        return item.getType() + builder.toString();
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
        return other instanceof final QuestItem item && item.handlers.equals(handlers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selector, handlers);
    }

    /**
     * Compares ItemStack to the quest item.
     *
     * @param item ItemStack to compare
     * @return true if the item matches
     */
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

        final List<ItemMetaHandler<? extends ItemMeta>> orderedCompare = List.of(
                durability, customModelData, unbreakable, flags, name, lore,
                enchants, potion, book, head, color, firework
        );

        for (final ItemMetaHandler<? extends ItemMeta> handler : orderedCompare) {
            if (!handler.rawCheck(meta)) {
                return false;
            }
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
    public ItemStack generate(final int stackSize, @Nullable final Profile profile) {
        // Try resolve material directly
        final Material material = selector.getRandomMaterial();

        final ItemStack item = new ItemStack(material, stackSize);
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        for (final ItemMetaHandler<? extends ItemMeta> handler : handlers) {
            handler.rawPopulate(meta, profile);
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
     * @return the display name or null if there is no name
     */
    @Nullable
    public String getName() {
        return name.get();
    }

    /**
     * Gets the lore.
     *
     * @return the list of lore lines, can be empty
     */
    public List<String> getLore() {
        return lore.get();
    }
}
