package org.betonquest.betonquest;

import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.*;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.LogUtils;
import org.betonquest.betonquest.utils.Utils;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessivePublicCount", "PMD.GodClass", "PMD.CommentRequired",
        "PMD.AvoidFieldNameMatchingTypeName", "PMD.AvoidLiteralsInIfCondition"})
public class Instruction {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("(?:\\s|\\G|^)((\\+|-)?\\d+)(?:\\s|$)");
    private final ConfigPackage pack;
    protected String instruction;
    protected String[] parts;
    private ID identifier;
    private int nextIndex = 1;
    private int currentIndex = 1;
    private String lastOptional;

    public Instruction(final ConfigPackage pack, final ID identifier, final String instruction) {
        this.pack = pack;
        try {
            this.identifier = identifier == null ? new NoID(pack) : identifier;
        } catch (final ObjectNotFoundException e) {
            LogUtils.getLogger().log(Level.WARNING, "Could not find instruction: " + e.getMessage());
            LogUtils.logThrowable(e);
        }
        this.instruction = instruction;
        this.parts = Utils.split(instruction);
    }

    @Override
    public String toString() {
        return instruction;
    }

    public String getInstruction() {
        return instruction;
    }

    public int size() {
        return parts.length;
    }

    public ConfigPackage getPackage() {
        return pack;
    }

    public ID getID() {
        return identifier;
    }

    /////////////////////
    ///    GENERAL    ///
    /////////////////////

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

    public String getOptional(final String prefix) {
        for (final String part : parts) {
            if (part.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT) + ":")) {
                lastOptional = prefix;
                currentIndex = -1;
                return part.substring(prefix.length() + 1);
            }
        }
        return null;
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

    public CompoundLocation getLocation(final String string) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        try {
            return new CompoundLocation(pack.getName(), string);
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
            return new VariableNumber(pack.getName(), string);
        } catch (final InstructionParseException e) {
            throw new PartParseException("Could not parse a number: " + e.getMessage(), e);
        }
    }

    public QuestItem getQuestItem() throws InstructionParseException {
        return getQuestItem(next());
    }

    private QuestItem getQuestItem(final String string) throws InstructionParseException {
        if (string == null) {
            return null;
        }
        try {
            return new QuestItem(new ItemID(pack, string));
        } catch (ObjectNotFoundException | InstructionParseException e) {
            throw new PartParseException("Could not load '" + string + "' item: " + e.getMessage(), e);
        }
    }

    public Item[] getItemList() throws InstructionParseException {
        return getItemList(next());
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
            } catch (InstructionParseException | NumberFormatException e) {
                throw new PartParseException("Error while parsing '" + array[i] + "' item: " + e.getMessage(), e);
            }
        }
        return items;
    }

    public Map<Enchantment, Integer> getEnchantments() throws InstructionParseException {
        return getEnchantments(next());
    }

    @SuppressWarnings("deprecation")
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
        if (string == null) {
            return null;
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
        return new BlockSelector(string);
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

    public List<Integer> getAllNumbers() {
        final Matcher matcher = NUMBER_PATTERN.matcher(instruction);

        final ArrayList<Integer> result = new ArrayList<>();
        while (matcher.find()) {
            result.add(Integer.parseInt(matcher.group(1)));
        }
        return result;
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
            return new ArrayList<T>(0);
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

        private static final long serialVersionUID = 2007556828888605511L;

        /**
         * {@link Exception#Exception(String)}
         */
        public PartParseException(final String message) {
            super("Error while parsing " + (lastOptional == null ? currentIndex : lastOptional + " optional") + " argument: " + message);
        }

        /**
         * {@link Exception#Exception(String, Throwable)}
         */
        public PartParseException(final String message, final Throwable cause) {
            super("Error while parsing " + (lastOptional == null ? currentIndex : lastOptional + " optional") + " argument: " + message, cause);
        }
    }

}
