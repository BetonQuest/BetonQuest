package org.betonquest.betonquest.instruction;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.identifier.NoID;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.PackageArgument;
import org.betonquest.betonquest.instruction.argument.parser.ArgumentConverter;
import org.betonquest.betonquest.instruction.argument.parser.PackageArgumentConverter;
import org.betonquest.betonquest.instruction.tokenizer.QuotingTokenizer;
import org.betonquest.betonquest.instruction.tokenizer.Tokenizer;
import org.betonquest.betonquest.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/**
 * The Instruction. Primary object for input parsing.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class Instruction implements InstructionParts, ArgumentConverter, PackageArgumentConverter {
    /**
     * The quest package that this instruction belongs to.
     */
    private final QuestPackage pack;

    /**
     * The identifier for this instruction.
     */
    private final Identifier identifier;

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
    public Instruction(final QuestPackage pack, @Nullable final Identifier identifier, final String instruction) throws QuestException {
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
    public Instruction(final Tokenizer tokenizer, final QuestPackage pack, final Identifier identifier, final String instruction) throws QuestException {
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
    public Instruction(final Instruction instruction, final Identifier identifier) {
        this.pack = instruction.pack;
        this.identifier = identifier;
        this.instructionString = instruction.instructionString;
        this.instructionParts = new InstructionPartsArray(instruction.instructionParts);
    }

    private static Identifier useFallbackIdIfNecessary(final QuestPackage pack, @Nullable final Identifier identifier) {
        if (identifier != null) {
            return identifier;
        }
        try {
            return new NoID(pack);
        } catch (final QuestException e) {
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
     * Get the {@link Identifier} of this instruction.
     *
     * @return the instruction identifier
     */
    public Identifier getID() {
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
    public Instruction copy(final Identifier newID) {
        return new Instruction(this, newID);
    }

    @Override
    @Contract("_, !null -> !null")
    @Nullable
    public String getValue(final String prefix, @Nullable final String defaultValue) {
        return getParts().stream()
                .filter(part -> part.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT) + ":"))
                .findFirst()
                .map(part -> part.substring(prefix.length() + 1)).orElse(defaultValue);
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
    @Contract("!null, _, _ -> !null; _, _, !null -> !null")
    @Nullable
    public <T> Variable<T> get(@Nullable final String string, final Argument<T> argument, @Nullable final T defaultValue) throws QuestException {
        if (string == null) {
            if (defaultValue != null) {
                return new Variable<>(defaultValue);
            }
            return null;
        }
        return new Variable<>(BetonQuest.getInstance().getVariableProcessor(), pack, string, argument);
    }

    @Override
    public <T> Variable<List<T>> getList(@Nullable final String string, final Argument<T> argument, final ValueChecker<List<T>> valueChecker) throws QuestException {
        if (string == null) {
            return new VariableList<>();
        }
        return new VariableList<>(BetonQuest.getInstance().getVariableProcessor(), pack, string, argument, valueChecker);
    }

    @Override
    @Contract("!null, _, _ -> !null; _, _, !null -> !null")
    @Nullable
    public <T> Variable<T> get(@Nullable final String string, final PackageArgument<T> argument, @Nullable final T defaultValue) throws QuestException {
        if (string == null) {
            if (defaultValue != null) {
                return new Variable<>(defaultValue);
            }
            return null;
        }
        return new Variable<>(BetonQuest.getInstance().getVariableProcessor(), pack, string, value -> argument.apply(pack, value));
    }

    @Override
    public <T> Variable<List<T>> getList(@Nullable final String string, final PackageArgument<T> argument, final ValueChecker<List<T>> valueChecker) throws QuestException {
        if (string == null) {
            return new VariableList<>();
        }
        return new VariableList<>(BetonQuest.getInstance().getVariableProcessor(), pack, string, value -> argument.apply(pack, value), valueChecker);
    }
}
