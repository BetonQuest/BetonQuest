package org.betonquest.betonquest;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.id.NoID;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.Utils;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessivePublicCount", "PMD.GodClass", "PMD.CommentRequired",
        "PMD.AvoidFieldNameMatchingTypeName", "PMD.AvoidLiteralsInIfCondition", "PMD.TooManyMethods"})
public class Instruction {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    private final QuestPackage pack;

    protected String[] parts;

    private ID identifier;

    private int nextIndex = 1;

    private int currentIndex = 1;

    private String lastOptional;

    public Instruction(final BetonQuestLogger log, final QuestPackage pack, final ID identifier, final String instruction) {
        this.log = log;
        this.pack = pack;
        try {
            this.identifier = identifier == null ? new NoID(pack) : identifier;
        } catch (final ObjectNotFoundException e) {
            this.log.warn(pack, "Could not find instruction: " + e.getMessage(), e);
        }
        this.parts = Utils.split(instruction);
    }

    @Override
    public String toString() {
        return getInstruction();
    }

    public String getInstruction() {
        return String.join(" ", parts);
    }

    public int size() {
        return parts.length;
    }

    public QuestPackage getPackage() {
        return pack;
    }

    public ID getID() {
        return identifier;
    }

    /**
     * Copy the instruction. The copy has no consumed arguments.
     *
     * @return a new instruction
     */
    public Instruction copy() {
        return copy(identifier);
    }

    public Instruction copy(final ID newID) {
        return new Instruction(log, pack, newID, getInstruction());
    }

    /////////////////////
    ///    GENERAL    ///
    /////////////////////

    public boolean hasNext() {
        return currentIndex < parts.length - 1;
    }

    public String next() throws InstructionParseException {
        lastOptional = null;
        currentIndex = nextIndex;
        return getPart(nextIndex++);
    }

    public String current() throws InstructionParseException {
        lastOptional = null;
        currentIndex = nextIndex - 1;
        return getPart(currentIndex);
    }

    public String getPart(final int index) throws InstructionParseException {
        if (parts.length <= index) {
            throw new InstructionParseException("Not enough arguments");
        }
        lastOptional = null;
        currentIndex = index;
        return parts[index];
    }

    /**
     * Gets an optional key:value instruction argument or null if the key is not present.
     *
     * @param prefix the prefix of the optional value without ":"
     * @return the value or null
     */
    public String getOptional(final String prefix) {
        return getOptional(prefix, null);
    }

    /**
     * Gets an optional value or the default value if value is not present.
     *
     * @param prefix        the prefix of the optional value
     * @param defaultString the default value
     * @return the value or the default value
     */
    public String getOptional(final String prefix, final String defaultString) {
        return getOptionalArgument(prefix).orElse(defaultString);
    }

    /**
     * Gets an optional value with the given prefix.
     *
     * @param prefix the prefix of the optional value
     * @return an {@link Optional} containing the value or an empty {@link Optional} if the value is not present
     */
    public Optional<String> getOptionalArgument(final String prefix) {
        for (final String part : parts) {
            if (part.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT) + ":")) {
                lastOptional = prefix;
                currentIndex = -1;
                return Optional.of(part.substring(prefix.length() + 1));
            }
        }
        return Optional.empty();
    }

    public boolean hasArgument(final String argument) {
        for (final String part : parts) {
            if (part.equalsIgnoreCase(argument)) {
                return true;
            }
        }
        return false;
    }

    /////////////////////
    ///    OBJECTS    ///
    /////////////////////

    public CompoundLocation getLocation() throws InstructionParseException {
        return getLocation(next());
    }

    /**
     * Gets a location from an (optional) argument.
     *
     * @param prefix argument prefix
     * @return the location if it was defined in the instruction
     * @throws InstructionParseException if the location format is invalid
     */
    public Optional<CompoundLocation> getLocationArgument(final String prefix) throws InstructionParseException {
        final Optional<String> argument = getOptionalArgument(prefix);
        if (argument.isPresent()) {
            return Optional.of(getLocation(argument.get()));
        }
        return Optional.empty();
    }

    public CompoundLocation getLocation(final String string) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        try {
            return new CompoundLocation(pack, string);
        } catch (final InstructionParseException e) {
            throw new PartParseException("Error while parsing location: " + e.getMessage(), e);
        }
    }

    public VariableNumber getVarNum() throws InstructionParseException {
        return getVarNum(next());
    }

    public VariableNumber getVarNum(final String string) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        try {
            return new VariableNumber(pack, string);
        } catch (final InstructionParseException e) {
            throw new PartParseException("Could not parse a number: " + e.getMessage(), e);
        }
    }

    public QuestItem getQuestItem() throws InstructionParseException {
        return getQuestItem(next());
    }

    public QuestItem getQuestItem(final String string) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        try {
            return new QuestItem(new ItemID(pack, string));
        } catch (final ObjectNotFoundException | InstructionParseException e) {
            throw new PartParseException("Could not load '" + string + "' item: " + e.getMessage(), e);
        }
    }

    public Item[] getItemList() throws InstructionParseException {
        return getItemList(next());
    }

    /**
     * Gets a list of items from an (optional) argument.
     * If the argument is not given then an empty list will be returned.
     *
     * @param prefix argument prefix
     * @return array of items given; or empty list if there is no such argument
     * @throws InstructionParseException if the item definitions contain errors
     */
    public Item[] getItemListArgument(final String prefix) throws InstructionParseException {
        return getItemList(getOptionalArgument(prefix).orElse(null));
    }

    public Item[] getItemList(final String string) throws InstructionParseException {
        final String[] array = getArray(string);
        final Item[] items = new Item[array.length];
        for (int i = 0; i < items.length; i++) {
            try {
                final ItemID item;
                final VariableNumber number;
                if (array[i].contains(":")) {
                    final String[] parts = array[i].split(":", 2);
                    item = getItem(parts[0]);
                    number = getVarNum(parts[1]);
                } else {
                    item = getItem(array[i]);
                    number = new VariableNumber(1);
                }
                items[i] = new Item(item, number);
            } catch (final InstructionParseException | NumberFormatException e) {
                throw new PartParseException("Error while parsing '" + array[i] + "' item: " + e.getMessage(), e);
            }
        }
        return items;
    }

    public Map<Enchantment, Integer> getEnchantments() throws InstructionParseException {
        return getEnchantments(next());
    }

    @SuppressWarnings({"deprecation", "PMD.ReturnEmptyCollectionRatherThanNull"})
    public Map<Enchantment, Integer> getEnchantments(final String string) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        final Map<Enchantment, Integer> enchants = new HashMap<>();
        final String[] array = getArray(string);
        for (final String enchant : array) {
            final String[] enchParts = enchant.split(":");
            if (enchParts.length != 2) {
                throw new PartParseException("Wrong enchantment format: " + enchant);
            }
            final Enchantment enchantment = Enchantment.getByName(enchParts[0]);
            if (enchantment == null) {
                throw new PartParseException("Unknown enchantment type: " + enchParts[0]);
            }
            final int level;
            try {
                level = Integer.parseInt(enchParts[1]);
            } catch (final NumberFormatException e) {
                throw new PartParseException("Could not parse level in enchant: " + enchant, e);
            }
            enchants.put(enchantment, level);
        }
        return enchants;
    }

    public List<PotionEffect> getEffects() throws InstructionParseException {
        return getEffects(next());
    }

    @SuppressWarnings("PMD.ReturnEmptyCollectionRatherThanNull")
    public List<PotionEffect> getEffects(final String string) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        final List<PotionEffect> effects = new ArrayList<>();
        final String[] array = getArray(string);
        for (final String effect : array) {
            final String[] effParts = effect.split(":");
            final PotionEffectType potionEffectType = PotionEffectType.getByName(effParts[0]);
            if (potionEffectType == null) {
                throw new PartParseException("Unknown potion effect" + effParts[0]);
            }
            final int power;
            final int duration;
            try {
                power = Integer.parseInt(effect.split(":")[1]) - 1;
                duration = Integer.parseInt(effect.split(":")[2]) * 20;
            } catch (final NumberFormatException e) {
                throw new PartParseException("Could not parse potion power/duration: " + effect, e);
            }
            effects.add(new PotionEffect(potionEffectType, duration, power));
        }
        return effects;
    }

    ///////////////////
    ///    Enums    ///
    ///////////////////

    public <T extends Enum<T>> T getEnum(final Class<T> clazz) throws InstructionParseException {
        return getEnum(next(), clazz);
    }

    public <T extends Enum<T>> T getEnum(final String string, final Class<T> clazz) throws InstructionParseException {
        return getEnum(string, clazz, null);
    }

    public <T extends Enum<T>> T getEnum(final String string, final Class<T> clazz, final T defaultValue) throws InstructionParseException {
        if (string == null) {
            return defaultValue;
        }
        try {
            return Enum.valueOf(clazz, string.toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException e) {
            throw new PartParseException("There is no such " + clazz.getSimpleName() + ": " + string, e);
        }
    }

    public Material getMaterial() throws InstructionParseException {
        return getMaterial(next());
    }

    public Material getMaterial(final String string) {
        if (string == null) {
            return null;
        }
        return Material.matchMaterial(string);
    }

    public BlockSelector getBlockSelector() throws InstructionParseException {
        return getBlockSelector(next());
    }

    public BlockSelector getBlockSelector(final String string) throws InstructionParseException {
        return string == null ? null : new BlockSelector(string);
    }

    public EntityType getEntity() throws InstructionParseException {
        return getEnum(next(), EntityType.class);
    }

    public EntityType getEntity(final String string) throws InstructionParseException {
        return getEnum(string, EntityType.class);
    }

    public PotionType getPotion() throws InstructionParseException {
        return getEnum(next(), PotionType.class);
    }

    public PotionType getPotion(final String string) throws InstructionParseException {
        return getEnum(string, PotionType.class);
    }

    /////////////////
    ///    IDs    ///
    /////////////////

    public EventID getEvent() throws InstructionParseException {
        return getEvent(next());
    }

    public EventID getEvent(final String string) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        try {
            return new EventID(pack, string);
        } catch (final ObjectNotFoundException e) {
            throw new PartParseException("Error while loading event: " + e.getMessage(), e);
        }
    }

    public ConditionID getCondition() throws InstructionParseException {
        return getCondition(next());
    }

    public ConditionID getCondition(final String string) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        try {
            return new ConditionID(pack, string);
        } catch (final ObjectNotFoundException e) {
            throw new PartParseException("Error while loading condition: " + e.getMessage(), e);
        }
    }

    public ObjectiveID getObjective() throws InstructionParseException {
        return getObjective(next());
    }

    public ObjectiveID getObjective(final String string) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        try {
            return new ObjectiveID(pack, string);
        } catch (final ObjectNotFoundException e) {
            throw new PartParseException("Error while loading objective: " + e.getMessage(), e);
        }
    }

    public ItemID getItem() throws InstructionParseException {
        return getItem(next());
    }

    public ItemID getItem(final String string) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        try {
            return new ItemID(pack, string);
        } catch (final ObjectNotFoundException e) {
            throw new PartParseException("Error while loading item: " + e.getMessage(), e);
        }
    }

    /////////////////////
    ///    NUMBERS    ///
    /////////////////////

    public byte getByte() throws InstructionParseException {
        return getByte(next(), (byte) 0);
    }

    public byte getByte(final String string, final byte def) throws InstructionParseException {
        if (string == null) {
            return def;
        }
        try {
            return Byte.parseByte(string);
        } catch (final NumberFormatException e) {
            throw new PartParseException("Could not parse byte value: " + string, e);
        }
    }

    public int getPositive() throws InstructionParseException {
        return getPositive(next(), 0);
    }

    public int getPositive(final String string, final int def) throws InstructionParseException {
        final int number = getInt(string, def);
        if (number <= 0) {
            throw new InstructionParseException("Number cannot be less than 1");
        }
        return number;
    }

    public int getInt() throws InstructionParseException {
        return getInt(next(), 0);
    }

    public int getInt(final String string, final int def) throws InstructionParseException {
        if (string == null) {
            return def;
        }
        try {
            return Integer.parseInt(string);
        } catch (final NumberFormatException e) {
            throw new PartParseException("Could not parse a number: " + string, e);
        }
    }

    public long getLong() throws InstructionParseException {
        return getLong(next(), 0);
    }

    public long getLong(final String string, final long def) throws InstructionParseException {
        if (string == null) {
            return def;
        }
        try {
            return Long.parseLong(string);
        } catch (final NumberFormatException e) {
            throw new PartParseException("Could not parse a number: " + string, e);
        }
    }

    public double getDouble() throws InstructionParseException {
        return getDouble(next(), 0.0);
    }

    public double getDouble(final String string, final double def) throws InstructionParseException {
        if (string == null) {
            return def;
        }
        try {
            return Double.parseDouble(string);
        } catch (final NumberFormatException e) {
            throw new PartParseException("Could not parse decimal value: " + string, e);
        }
    }

    ////////////////////
    ///    ARRAYS    ///
    ////////////////////

    public String[] getArray() throws InstructionParseException {
        return getArray(next());
    }

    public String[] getArray(final String string) {
        if (string == null) {
            return new String[0];
        }
        return string.split(",");
    }

    public <T> List<T> getList(final Converter<T> converter) throws InstructionParseException {
        return getList(next(), converter);
    }

    public <T> List<T> getList(final String string, final Converter<T> converter) throws InstructionParseException {
        if (string == null) {
            return new ArrayList<>(0);
        }
        final String[] array = getArray(string);
        final List<T> list = new ArrayList<>(array.length);
        for (final String part : array) {
            list.add(converter.convert(part));
        }
        return list;
    }

    /////////////////////////
    ///    OTHER STUFF    ///
    /////////////////////////

    public interface Converter<T> {
        T convert(String string) throws InstructionParseException;
    }

    @SuppressWarnings("PMD.ShortClassName")
    public static class Item {
        private final ItemID itemID;

        private final QuestItem questItem;

        private final VariableNumber amount;

        public Item(final ItemID itemID, final VariableNumber amount) throws InstructionParseException {
            this.itemID = itemID;
            this.questItem = new QuestItem(itemID);
            this.amount = amount;
        }

        public ItemID getID() {
            return itemID;
        }

        public QuestItem getItem() {
            return questItem;
        }

        public boolean isItemEqual(final ItemStack item) {
            return questItem.compare(item);
        }

        public VariableNumber getAmount() {
            return amount;
        }
    }

    public class PartParseException extends InstructionParseException {
        @Serial
        private static final long serialVersionUID = 2007556828888605511L;

        /**
         * @param message The message
         * @see Exception#Exception(String)
         */
        public PartParseException(final String message) {
            super("Error while parsing " + (lastOptional == null ? currentIndex : lastOptional + " optional") + " argument: " + message);
        }

        /**
         * @param message The message
         * @param cause   The Throwable
         * @see Exception#Exception(String, Throwable)
         */
        public PartParseException(final String message, final Throwable cause) {
            super("Error while parsing " + (lastOptional == null ? currentIndex : lastOptional + " optional") + " argument: " + message, cause);
        }
    }
}
