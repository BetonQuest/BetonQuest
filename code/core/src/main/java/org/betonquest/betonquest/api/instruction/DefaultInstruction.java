package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.identifier.NoID;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.IdentifierArgument;
import org.betonquest.betonquest.api.instruction.argument.InstructionIdentifierArgument;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.instruction.tokenizer.QuotingTokenizer;
import org.betonquest.betonquest.api.instruction.tokenizer.Tokenizer;
import org.betonquest.betonquest.api.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.instruction.variable.VariableList;
import org.betonquest.betonquest.api.quest.Variables;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/**
 * The Instruction. Primary object for input parsing.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.CouplingBetweenObjects"})
public class DefaultInstruction implements Instruction {

    /**
     * Variable processor to create and resolve variables.
     */
    private final Variables variables;

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

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
     * @param variables   the variable processor to create and resolve variables
     * @param packManager the quest package manager to get quest packages from
     * @param pack        quest package the instruction belongs to
     * @param identifier  identifier of the instruction
     * @param instruction instruction string to parse
     * @throws QuestException if the instruction could not be tokenized
     */
    public DefaultInstruction(final Variables variables, final QuestPackageManager packManager, final QuestPackage pack,
                              @Nullable final Identifier identifier, final String instruction) throws QuestException {
        this(variables, packManager, new QuotingTokenizer(), pack, useFallbackIdIfNecessary(packManager, pack, identifier), instruction);
    }

    /**
     * Create an instruction using the given tokenizer.
     *
     * @param variables   the variable processor to create and resolve variables
     * @param packManager the quest package manager to get quest packages from
     * @param tokenizer   Tokenizer that can split on spaces but interpret quotes and escapes.
     * @param pack        quest package the instruction belongs to
     * @param identifier  identifier of the instruction
     * @param instruction instruction string to parse
     * @throws QuestException if the instruction could not be tokenized
     */
    public DefaultInstruction(final Variables variables, final QuestPackageManager packManager, final Tokenizer tokenizer, final QuestPackage pack, final Identifier identifier, final String instruction) throws QuestException {
        this.variables = variables;
        this.packManager = packManager;
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
    public DefaultInstruction(final DefaultInstruction instruction, final Identifier identifier) {
        this.variables = instruction.variables;
        this.packManager = instruction.packManager;
        this.pack = instruction.pack;
        this.identifier = identifier;
        this.instructionString = instruction.instructionString;
        this.instructionParts = new InstructionPartsArray(instruction.instructionParts);
    }

    private static Identifier useFallbackIdIfNecessary(final QuestPackageManager packManager, final QuestPackage pack, @Nullable final Identifier identifier) {
        if (identifier != null) {
            return identifier;
        }
        try {
            return new NoID(packManager, pack);
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
    public DefaultInstruction copy() {
        return copy(identifier);
    }

    /**
     * Copy this instruction but overwrite the ID of the copy. The copy has no consumed arguments.
     *
     * @param newID the ID to identify the copied instruction with
     * @return copy of this instruction with the new ID
     */
    public DefaultInstruction copy(final Identifier newID) {
        return new DefaultInstruction(this, newID);
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
        return new Variable<>(variables, pack, string, argument);
    }

    @Override
    public <T> Variable<List<T>> getList(@Nullable final String string, final Argument<T> argument, final ValueChecker<List<T>> valueChecker) throws QuestException {
        if (string == null) {
            return new VariableList<>();
        }
        return new VariableList<>(variables, pack, string, argument, valueChecker);
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
        return new Variable<>(variables, pack, string, value -> argument.apply(pack, value));
    }

    @Override
    public <T> Variable<List<T>> getList(@Nullable final String string, final PackageArgument<T> argument, final ValueChecker<List<T>> valueChecker) throws QuestException {
        if (string == null) {
            return new VariableList<>();
        }
        return new VariableList<>(variables, pack, string, value -> argument.apply(pack, value), valueChecker);
    }

    @Override
    @Nullable
    public <T> Variable<T> get(@Nullable final String string, final IdentifierArgument<T> argument, @Nullable final T defaultValue) throws QuestException {
        if (string == null) {
            if (defaultValue != null) {
                return new Variable<>(defaultValue);
            }
            return null;
        }
        return new Variable<>(variables, pack, string, value -> argument.apply(packManager, pack, value));
    }

    @Override
    public <T> Variable<List<T>> getList(@Nullable final String string, final IdentifierArgument<T> argument, final ValueChecker<List<T>> valueChecker) throws QuestException {
        if (string == null) {
            return new VariableList<>();
        }
        return new VariableList<>(variables, pack, string, value -> argument.apply(packManager, pack, value), valueChecker);
    }

    @Override
    @Nullable
    public <T> Variable<T> get(@Nullable final String string, final InstructionIdentifierArgument<T> argument, @Nullable final T defaultValue) throws QuestException {
        if (string == null) {
            if (defaultValue != null) {
                return new Variable<>(defaultValue);
            }
            return null;
        }
        return new Variable<>(variables, pack, string, value -> argument.apply(variables, packManager, pack, value));
    }

    @Override
    public <T> Variable<List<T>> getList(@Nullable final String string, final InstructionIdentifierArgument<T> argument, final ValueChecker<List<T>> valueChecker) throws QuestException {
        if (string == null) {
            return new VariableList<>();
        }
        return new VariableList<>(variables, pack, string, value -> argument.apply(variables, packManager, pack, value), valueChecker);
    }
}
