package org.betonquest.betonquest.instruction;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
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

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * The Instruction. Primary object for input parsing.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class Instruction implements InstructionParts, ArgumentParser, EnumParser, ListParser, IDParser, ItemParser, NumberParser {
    /**
     * The quest package that this instruction belongs to.
     */
    private final QuestPackage pack;

    /**
     * The identifier for this instruction.
     */
    private final ID identifier;

    /**
     * The raw instruction string.
     */
    private final String instructionString;

    /**
     * The parts of the instruction.
     */
    private final InstructionParts instructionParts;

    /**
     * Create an instruction using the quoting tokenizer.
     *
     * @param pack        quest package the instruction belongs to
     * @param identifier  identifier of the instruction
     * @param instruction instruction string to parse
     * @throws QuestException if the instruction could not be tokenized
     */
    public Instruction(final QuestPackage pack, @Nullable final ID identifier, final String instruction) throws QuestException {
        this(new QuotingTokenizer(), pack, useFallbackIdIfNecessary(pack, identifier), instruction);
    }

    /**
     * Create an instruction using the given tokenizer.
     *
     * @param tokenizer   Tokenizer that can split on spaces but interpret quotes and escapes.
     * @param pack        quest package the instruction belongs to
     * @param identifier  identifier of the instruction
     * @param instruction instruction string to parse
     * @throws QuestException if the instruction could not be tokenized
     */
    public Instruction(final Tokenizer tokenizer, final QuestPackage pack, final ID identifier, final String instruction) throws QuestException {
        this.pack = pack;
        this.identifier = identifier;
        this.instructionString = instruction;
        try {
            this.instructionParts = new InstructionPartsArray(tokenizer, instruction);
        } catch (final TokenizerException e) {
            throw new QuestException("Could not tokenize instruction '" + instruction + "': " + e.getMessage(), e);
        }
    }

    /**
     * Copies an instruction using the given instruction and a new identifier.
     *
     * @param instruction instruction to copy
     * @param identifier  identifier of the new instruction
     */
    public Instruction(final Instruction instruction, final ID identifier) {
        this.pack = instruction.pack;
        this.identifier = identifier;
        this.instructionString = instruction.instructionString;
        this.instructionParts = new InstructionPartsArray(instruction.instructionParts);
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

    /**
     * Get the source QuestPackage.
     *
     * @return the package containing this instruction
     */
    public QuestPackage getPackage() {
        return pack;
    }

    /**
     * Get the {@link ID} of this instruction.
     *
     * @return the instruction identifier
     */
    public ID getID() {
        return identifier;
    }

    /**
     * Get the raw instruction string as it was passed to the constructor.
     *
     * @return the instruction string
     */
    @Override
    public String toString() {
        return instructionString;
    }

    @Override
    public String next() throws QuestException {
        return instructionParts.next();
    }

    @Override
    public String current() {
        return instructionParts.current();
    }

    @Override
    public boolean hasNext() {
        return instructionParts.hasNext();
    }

    @Override
    public int size() {
        return instructionParts.size();
    }

    @Override
    public String getPart(final int index) throws QuestException {
        return instructionParts.getPart(index);
    }

    @Override
    public List<String> getParts() {
        return instructionParts.getParts();
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
        return new Instruction(this, newID);
    }

    @Override
    public Optional<String> getOptionalArgument(final String prefix) {
        return getParts().stream()
                .filter(part -> part.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT) + ":"))
                .findFirst()
                .map(part -> part.substring(prefix.length() + 1));
    }

    /**
     * Checks if the instruction contains the argument.
     *
     * @param argument the argument to check
     * @return if the instruction contains that argument, ignoring cases
     */
    public boolean hasArgument(final String argument) {
        return getParts().stream().anyMatch(part -> part.equalsIgnoreCase(argument));
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
            throw new QuestException("Error while loading '" + string + "' id: " + e.getMessage(), e);
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
            throw new QuestException("Error while parsing '" + string + "' enum class '" + clazz.getSimpleName() + "': " + e.getMessage(), e);
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
            throw new QuestException("Error while parsing '" + string + "' item: " + e.getMessage(), e);
        }
    }

    @Override
    public Number parseNumber(final String string, final Argument<Number> argument) throws QuestException {
        try {
            return argument.apply(string);
        } catch (final NumberFormatException e) {
            throw new QuestException("Could not parsing '" + string + "' number: " + e.getMessage(), e);
        }
    }
}
