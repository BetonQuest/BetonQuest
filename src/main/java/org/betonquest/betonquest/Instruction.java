package org.betonquest.betonquest;

import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.id.NoID;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.instruction.tokenizer.QuotingTokenizer;
import org.betonquest.betonquest.instruction.tokenizer.Tokenizer;
import org.betonquest.betonquest.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.BlockSelector;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessivePublicCount", "PMD.GodClass", "PMD.CommentRequired",
        "PMD.AvoidFieldNameMatchingTypeName", "PMD.AvoidLiteralsInIfCondition", "PMD.TooManyMethods",
        "PMD.CouplingBetweenObjects"})
public class Instruction {
    /**
     * Contract: Returns null when the parameter is null, otherwise the expected object.
     */
    private static final String NULL_NOT_NULL_CONTRACT = "null -> null; !null -> !null";

    /**
     * The raw instruction string.
     */
    protected final String instruction;

    /**
     * The quest package that this instruction belongs to.
     */
    private final QuestPackage pack;

    /**
     * The identifier for this instruction.
     */
    private final ID identifier;

    /**
     * The parts of the instruction. This is the result after tokenizing the raw instruction string.
     */
    private final String[] parts;

    private int nextIndex = 1;

    private int currentIndex = 1;

    @Nullable
    private String lastOptional;

    public Instruction(final BetonQuestLogger log, final QuestPackage pack, @Nullable final ID identifier, final String instruction) {
        this(new QuotingTokenizer(), log, pack, useFallbackIdIfNecessary(pack, identifier), instruction);
    }

    /**
     * Create an instruction using the given tokenizer.
     *
     * @param tokenizer   Tokenizer that can split on spaces but interpret quotes and escapes.
     * @param log         logger to log failures when parsing the instruction string
     * @param pack        quest package the instruction belongs to
     * @param identifier  identifier of the instruction
     * @param instruction instruction string to parse
     */
    public Instruction(final Tokenizer tokenizer, final BetonQuestLogger log, final QuestPackage pack, final ID identifier, final String instruction) {
        this.pack = pack;
        this.identifier = identifier;
        this.instruction = instruction;
        this.parts = tokenizeInstruction(tokenizer, pack, instruction, log);
    }

    /**
     * Create an instruction using the given tokenizer.
     *
     * @param pack        quest package the instruction belongs to
     * @param identifier  identifier of the instruction
     * @param instruction raw instruction string
     * @param parts       parts that the instruction consists of
     */
    public Instruction(final QuestPackage pack, final ID identifier, final String instruction, final String... parts) {
        this.pack = pack;
        this.identifier = identifier;
        this.instruction = instruction;
        this.parts = Arrays.copyOf(parts, parts.length);
    }

    private static ID useFallbackIdIfNecessary(final QuestPackage pack, @Nullable final ID identifier) {
        if (identifier != null) {
            return identifier;
        }
        try {
            return new NoID(pack);
        } catch (final ObjectNotFoundException e) {
            throw new IllegalStateException("Could not find instruction: " + e.getMessage(), e);
        }
    }

    private String[] tokenizeInstruction(final Tokenizer tokenizer, final QuestPackage pack, final String instruction, final BetonQuestLogger log) {
        try {
            return tokenizer.tokens(instruction);
        } catch (final TokenizerException e) {
            log.warn(pack, "Could not tokenize instruction '" + instruction + "': " + e.getMessage(), e);
            return new String[0];
        }
    }

    @Override
    public String toString() {
        return instruction;
    }

    /**
     * Get the original raw instruction string that was used to tokenize the parts of this instruction.
     *
     * @return the raw instruction string that defined this instruction
     * @deprecated try not to implement your own parsing and use other API of this class instead if possible
     */
    @Deprecated
    public String getInstruction() {
        return toString();
    }

    /**
     * Get all parts of the instruction. The instruction type is omitted.
     *
     * @return all arguments
     */
    public String[] getAllParts() {
        return Arrays.copyOfRange(parts, 1, parts.length);
    }

    /**
     * Get remaining parts of the instruction. The instruction type is omitted, even if no parts have been consumed yet.
     *
     * @return all arguments joined together
     */
    public String[] getRemainingParts() {
        final String[] remainingParts = Arrays.copyOfRange(parts, nextIndex, parts.length);
        nextIndex = parts.length;
        currentIndex = parts.length - 1;
        return remainingParts;
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

    protected String[] getParts() {
        return Arrays.copyOf(parts, parts.length);
    }

    /**
     * Copy this instruction. The copy has no consumed arguments.
     *
     * @return a copy of this instruction
     */
    public Instruction copy() {
        return copy(identifier);
    }

    /**
     * Copy this instruction but overwrite the ID of the copy. The copy has no consumed arguments.
     *
     * @param newID the ID to identify the copied instruction with
     * @return copy of this instruction with the new ID
     */
    public Instruction copy(final ID newID) {
        return new Instruction(getPackage(), newID, instruction, getParts());
    }

    public boolean hasNext() {
        return currentIndex < parts.length - 1;
    }

    public String next() throws QuestException {
        lastOptional = null;
        currentIndex = nextIndex;
        return getPart(nextIndex++);
    }

    public String current() throws QuestException {
        lastOptional = null;
        currentIndex = nextIndex - 1;
        return getPart(currentIndex);
    }

    public String getPart(final int index) throws QuestException {
        if (parts.length <= index) {
            throw new QuestException("Not enough arguments");
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
    @Nullable
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
    @Contract("_, !null -> !null")
    @Nullable
    public String getOptional(final String prefix, @Nullable final String defaultString) {
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

    public VariableLocation getLocation() throws QuestException {
        return getLocation(next());
    }

    /**
     * Gets a location from an (optional) argument.
     *
     * @param prefix argument prefix
     * @return the location if it was defined in the instruction
     * @throws QuestException if the location format is invalid
     */
    public Optional<VariableLocation> getLocationArgument(final String prefix) throws QuestException {
        final Optional<String> argument = getOptionalArgument(prefix);
        if (argument.isPresent()) {
            return Optional.of(getLocation(argument.get()));
        }
        return Optional.empty();
    }

    @Contract(NULL_NOT_NULL_CONTRACT)
    @Nullable
    public VariableLocation getLocation(@Nullable final String string) throws QuestException {
        if (string == null) {
            return null;
        }
        try {
            return new VariableLocation(BetonQuest.getInstance().getVariableProcessor(), pack, string);
        } catch (final QuestException e) {
            throw new PartParseException("Error while parsing location: " + e.getMessage(), e);
        }
    }

    public VariableNumber getVarNum() throws QuestException {
        return getVarNum(next(), (value) -> {
        });
    }

    public VariableNumber getVarNum(final Variable.ValueChecker<Number> valueChecker) throws QuestException {
        return getVarNum(next(), valueChecker);
    }

    @Contract(NULL_NOT_NULL_CONTRACT)
    @Nullable
    public VariableNumber getVarNum(@Nullable final String string) throws QuestException {
        return getVarNum(string, (value) -> {
        });
    }

    @Contract("null, _ -> null; !null, _ -> !null")
    @Nullable
    public VariableNumber getVarNum(@Nullable final String string, final Variable.ValueChecker<Number> valueChecker) throws QuestException {
        if (string == null) {
            return null;
        }
        try {
            return new VariableNumber(pack, string, valueChecker);
        } catch (final QuestException e) {
            throw new PartParseException("Could not parse a number: " + e.getMessage(), e);
        }
    }

    public QuestItem getQuestItem() throws QuestException {
        return getQuestItem(next());
    }

    @Contract(NULL_NOT_NULL_CONTRACT)
    @Nullable
    public QuestItem getQuestItem(@Nullable final String string) throws QuestException {
        if (string == null) {
            return null;
        }
        try {
            return new QuestItem(new ItemID(pack, string));
        } catch (final ObjectNotFoundException | QuestException e) {
            throw new PartParseException("Could not load '" + string + "' item: " + e.getMessage(), e);
        }
    }

    public Item[] getItemList() throws QuestException {
        return getItemList(next());
    }

    /**
     * Gets a list of items from an (optional) argument.
     * If the argument is not given then an empty list will be returned.
     *
     * @param prefix argument prefix
     * @return array of items given; or empty list if there is no such argument
     * @throws QuestException if the item definitions contain errors
     */
    public Item[] getItemListArgument(final String prefix) throws QuestException {
        return getItemList(getOptionalArgument(prefix).orElse(null));
    }

    public Item[] getItemList(@Nullable final String string) throws QuestException {
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
                    number = getVarNum("1");
                }
                items[i] = new Item(item, number);
            } catch (final QuestException | NumberFormatException e) {
                throw new PartParseException("Error while parsing '" + array[i] + "' item: " + e.getMessage(), e);
            }
        }
        return items;
    }

    public Map<Enchantment, Integer> getEnchantments() throws QuestException {
        return getEnchantments(next());
    }

    @SuppressWarnings({"deprecation", "PMD.ReturnEmptyCollectionRatherThanNull"})
    @Contract(NULL_NOT_NULL_CONTRACT)
    @Nullable
    public Map<Enchantment, Integer> getEnchantments(@Nullable final String string) throws QuestException {
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

    public List<PotionEffect> getEffects() throws QuestException {
        return getEffects(next());
    }

    @SuppressWarnings("PMD.ReturnEmptyCollectionRatherThanNull")
    @Contract(NULL_NOT_NULL_CONTRACT)
    @Nullable
    public List<PotionEffect> getEffects(@Nullable final String string) throws QuestException {
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

    public <T extends Enum<T>> T getEnum(final Class<T> clazz) throws QuestException {
        return getEnum(next(), clazz);
    }

    @Contract("null, _ -> null; !null, _ -> !null")
    @Nullable
    public <T extends Enum<T>> T getEnum(@Nullable final String string, final Class<T> clazz) throws QuestException {
        return getEnum(string, clazz, null);
    }

    @Contract("_, _, !null -> !null")
    @Nullable
    public <T extends Enum<T>> T getEnum(@Nullable final String string, final Class<T> clazz, @Nullable final T defaultValue) throws QuestException {
        if (string == null) {
            return defaultValue;
        }
        try {
            return Enum.valueOf(clazz, string.toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException e) {
            throw new PartParseException("There is no such " + clazz.getSimpleName() + ": " + string, e);
        }
    }

    public Material getMaterial() throws QuestException {
        return getMaterial(next());
    }

    @Contract(NULL_NOT_NULL_CONTRACT)
    @Nullable
    public Material getMaterial(@Nullable final String string) {
        if (string == null) {
            return null;
        }
        return Material.matchMaterial(string);
    }

    public BlockSelector getBlockSelector() throws QuestException {
        return getBlockSelector(next());
    }

    @Contract(NULL_NOT_NULL_CONTRACT)
    @Nullable
    public BlockSelector getBlockSelector(@Nullable final String string) throws QuestException {
        return string == null ? null : new BlockSelector(string);
    }

    public EntityType getEntity() throws QuestException {
        return getEnum(next(), EntityType.class);
    }

    public EntityType getEntity(final String string) throws QuestException {
        return getEnum(string, EntityType.class);
    }

    public PotionType getPotion() throws QuestException {
        return getEnum(next(), PotionType.class);
    }

    public PotionType getPotion(final String string) throws QuestException {
        return getEnum(string, PotionType.class);
    }

    public EventID getEvent() throws QuestException {
        return getEvent(next());
    }

    @Contract(NULL_NOT_NULL_CONTRACT)
    @Nullable
    public EventID getEvent(@Nullable final String string) throws QuestException {
        if (string == null) {
            return null;
        }
        try {
            return new EventID(pack, string);
        } catch (final ObjectNotFoundException e) {
            throw new PartParseException("Error while loading event: " + e.getMessage(), e);
        }
    }

    public ConditionID getCondition() throws QuestException {
        return getCondition(next());
    }

    @Contract(NULL_NOT_NULL_CONTRACT)
    @Nullable
    public ConditionID getCondition(@Nullable final String string) throws QuestException {
        if (string == null) {
            return null;
        }
        try {
            return new ConditionID(pack, string);
        } catch (final ObjectNotFoundException e) {
            throw new PartParseException("Error while loading condition: " + e.getMessage(), e);
        }
    }

    public ObjectiveID getObjective() throws QuestException {
        return getObjective(next());
    }

    @Contract(NULL_NOT_NULL_CONTRACT)
    @Nullable
    public ObjectiveID getObjective(@Nullable final String string) throws QuestException {
        if (string == null) {
            return null;
        }
        try {
            return new ObjectiveID(pack, string);
        } catch (final ObjectNotFoundException e) {
            throw new PartParseException("Error while loading objective: " + e.getMessage(), e);
        }
    }

    public ItemID getItem() throws QuestException {
        return getItem(next());
    }

    @Contract(NULL_NOT_NULL_CONTRACT)
    @Nullable
    public ItemID getItem(@Nullable final String string) throws QuestException {
        if (string == null) {
            return null;
        }
        try {
            return new ItemID(pack, string);
        } catch (final ObjectNotFoundException e) {
            throw new PartParseException("Error while loading item: " + e.getMessage(), e);
        }
    }

    public byte getByte() throws QuestException {
        return getByte(next(), (byte) 0);
    }

    public byte getByte(@Nullable final String string, final byte def) throws QuestException {
        if (string == null) {
            return def;
        }
        try {
            return Byte.parseByte(string);
        } catch (final NumberFormatException e) {
            throw new PartParseException("Could not parse byte value: " + string, e);
        }
    }

    public int getPositive() throws QuestException {
        return getPositive(next(), 0);
    }

    public int getPositive(@Nullable final String string, final int def) throws QuestException {
        final int number = getInt(string, def);
        if (number <= 0) {
            throw new QuestException("Number cannot be less than 1");
        }
        return number;
    }

    public int getInt() throws QuestException {
        return getInt(next(), 0);
    }

    public int getInt(@Nullable final String string, final int def) throws QuestException {
        if (string == null) {
            return def;
        }
        try {
            return Integer.parseInt(string);
        } catch (final NumberFormatException e) {
            throw new PartParseException("Could not parse a number: " + string, e);
        }
    }

    public long getLong() throws QuestException {
        return getLong(next(), 0);
    }

    public long getLong(@Nullable final String string, final long def) throws QuestException {
        if (string == null) {
            return def;
        }
        try {
            return Long.parseLong(string);
        } catch (final NumberFormatException e) {
            throw new PartParseException("Could not parse a number: " + string, e);
        }
    }

    public double getDouble() throws QuestException {
        return getDouble(next(), 0.0);
    }

    public double getDouble(@Nullable final String string, final double def) throws QuestException {
        if (string == null) {
            return def;
        }
        try {
            return Double.parseDouble(string);
        } catch (final NumberFormatException e) {
            throw new PartParseException("Could not parse decimal value: " + string, e);
        }
    }

    public String[] getArray() throws QuestException {
        return getArray(next());
    }

    public String[] getArray(@Nullable final String string) {
        if (string == null) {
            return new String[0];
        }
        return StringUtils.split(string, ",");
    }

    public <T> List<T> getList(final Converter<T> converter) throws QuestException {
        return getList(next(), converter);
    }

    public <T> List<T> getList(@Nullable final String string, final Converter<T> converter) throws QuestException {
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

    public interface Converter<T> {
        @Contract(NULL_NOT_NULL_CONTRACT)
        @Nullable
        T convert(@Nullable String string) throws QuestException;
    }

    @SuppressWarnings("PMD.ShortClassName")
    public static class Item {
        private final ItemID itemID;

        private final QuestItem questItem;

        private final VariableNumber amount;

        public Item(final ItemID itemID, final VariableNumber amount) throws QuestException {
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

    public class PartParseException extends QuestException {
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
