package org.betonquest.betonquest.instruction;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.exception.ObjectNotFoundException;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.id.NoID;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.IDArgument;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.argument.parser.ArgumentParser;
import org.betonquest.betonquest.instruction.argument.parser.EnumParser;
import org.betonquest.betonquest.instruction.argument.parser.IDParser;
import org.betonquest.betonquest.instruction.argument.parser.ItemParser;
import org.betonquest.betonquest.instruction.argument.parser.ListParser;
import org.betonquest.betonquest.instruction.argument.parser.NumberParser;
import org.betonquest.betonquest.instruction.tokenizer.QuotingTokenizer;
import org.betonquest.betonquest.instruction.tokenizer.Tokenizer;
import org.betonquest.betonquest.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * The Instruction. Primary object for input parsing.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.GodClass"})
public class Instruction implements ArgumentParser, EnumParser, ListParser, IDParser, ItemParser, NumberParser {
    /**
     * The raw instruction string.
     */
    protected final String instructionString;

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

    /**
     * The index pointer for {@link #next()} method.
     */
    private int nextIndex = 1;

    /**
     * The position used for a more detailed exception message with {@link PartParseException}.
     */
    private int currentIndex = 1;

    /**
     * Key for the last requested {@link #getOptionalArgument(String)}.
     */
    @Nullable
    private String lastOptional;

    /**
     * Create an instruction using the quoting tokenizer.
     *
     * @param log         logger to log failures when parsing the instruction string
     * @param pack        quest package the instruction belongs to
     * @param identifier  identifier of the instruction
     * @param instruction instruction string to parse
     */
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
        this.instructionString = instruction;
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
        this.instructionString = instruction;
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
        return instructionString;
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

    /**
     * Get the instruction size.
     *
     * @return amount of arguments
     */
    public int size() {
        return parts.length;
    }

    /**
     * Get the source QuestPackage.
     *
     * @return the package containing this instruction
     */
    public QuestPackage getPackage() {
        return pack;
    }

    /**
     * Get the id.
     *
     * @return the instruction identifier
     */
    public ID getID() {
        return identifier;
    }

    /**
     * Get all instruction parts.
     *
     * @return parts inclusive first argument
     */
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
        return new Instruction(getPackage(), newID, instructionString, getParts());
    }

    /**
     * Check if there is a {@link #next()} argument.
     *
     * @return true if there are arguments left
     */
    public boolean hasNext() {
        return currentIndex < parts.length - 1;
    }

    @Override
    public String next() throws QuestException {
        lastOptional = null;
        currentIndex = nextIndex;
        return getPart(nextIndex++);
    }

    /**
     * Gets the current string.
     *
     * @return the current string
     * @throws QuestException when there are no parts
     */
    public String current() throws QuestException {
        lastOptional = null;
        currentIndex = nextIndex - 1;
        return getPart(currentIndex);
    }

    /**
     * Get the instruction part at the specified position.
     *
     * @param index the position to get
     * @return the argument at the position
     * @throws QuestException when index greater or equal to {@link #size}
     */
    public String getPart(final int index) throws QuestException {
        if (parts.length <= index) {
            throw new QuestException("Not enough arguments");
        }
        lastOptional = null;
        currentIndex = index;
        return parts[index];
    }

    @Override
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

    /**
     * Checks if the instruction contains the argument.
     *
     * @param argument the argument to check
     * @return if the instruction contains that argument, ignoring cases
     */
    public boolean hasArgument(final String argument) {
        for (final String part : parts) {
            if (part.equalsIgnoreCase(argument)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Contract("!null, _ -> !null")
    @Nullable
    public <T> T get(@Nullable final String string, final VariableArgument<T> argument) throws QuestException {
        if (string == null) {
            return null;
        }
        return argument.convert(BetonQuest.getInstance().getVariableProcessor(), pack, string);
    }

    @Override
    @Contract("!null, _ -> !null")
    @Nullable
    public <T extends ID> T getID(@Nullable final String string, final IDArgument<T> argument) throws QuestException {
        if (string == null) {
            return null;
        }
        try {
            return argument.convert(pack, string);
        } catch (final ObjectNotFoundException e) {
            throw new PartParseException("Error while loading id: " + e.getMessage(), e);
        }
    }

    @Override
    public <T extends ID> List<T> getIDList(@Nullable final String string, final IDArgument<T> argument) throws QuestException {
        return getList(string, value -> getID(value, argument));
    }

    @Override
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

    @Override
    @Contract("null -> null; !null -> !null")
    @Nullable
    public Item getItem(@Nullable final String string) throws QuestException {
        if (string == null) {
            return null;
        }
        try {
            final ItemID item;
            final VariableNumber number;
            if (string.contains(":")) {
                final String[] parts = string.split(":", 2);
                item = getID(parts[0], ItemID::new);
                number = get(parts[1], VariableNumber::new);
            } else {
                item = getID(string, ItemID::new);
                number = get("1", VariableNumber::new);
            }
            return new Item(item, number);
        } catch (final QuestException | NumberFormatException e) {
            throw new Instruction.PartParseException("Error while parsing '" + string + "' item: " + e.getMessage(), e);
        }
    }

    @Override
    public Number parseNumber(final String string, final Argument<Number> argument) throws QuestException {
        try {
            return argument.apply(string);
        } catch (final NumberFormatException e) {
            throw new PartParseException("Could not parse a number: " + string, e);
        }
    }

    /**
     * {@link QuestException} with detailed part arguments for instruction parsing.
     */
    public class PartParseException extends QuestException {
        @Serial
        private static final long serialVersionUID = 2007556828888605511L;

        /**
         * Create a new Exception with a message.
         *
         * @param message The message
         * @see Exception#Exception(String)
         */
        public PartParseException(final String message) {
            super("Error while parsing " + (lastOptional == null ? currentIndex : lastOptional + " optional") + " argument: " + message);
        }

        /**
         * Create a new exception with a message and cause.
         *
         * @param message The message
         * @param cause   The Throwable
         * @see Exception#Exception(String, Throwable)
         */
        public PartParseException(final String message, final Throwable cause) {
            super("Error while parsing " + (lastOptional == null ? currentIndex : lastOptional + " optional") + " argument: " + message, cause);
        }
    }
}
