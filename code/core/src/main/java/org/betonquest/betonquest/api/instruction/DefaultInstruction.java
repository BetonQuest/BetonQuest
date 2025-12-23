package org.betonquest.betonquest.api.instruction;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.identifier.NoID;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.IdentifierArgument;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.InstructionIdentifierArgument;
import org.betonquest.betonquest.api.instruction.chain.DecoratableChainRetriever;
import org.betonquest.betonquest.api.instruction.chain.NumberChainRetriever;
import org.betonquest.betonquest.api.instruction.tokenizer.QuotingTokenizer;
import org.betonquest.betonquest.api.instruction.tokenizer.Tokenizer;
import org.betonquest.betonquest.api.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.api.instruction.type.QuestItemWrapper;
import org.betonquest.betonquest.api.instruction.variable.DefaultVariable;
import org.betonquest.betonquest.api.instruction.variable.ValueParser;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.instruction.variable.VariableList;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.lib.instruction.chain.DefaultDecoratableChainRetriever;
import org.betonquest.betonquest.lib.instruction.chain.DefaultNumberChainRetriever;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

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
     * The default {@link Argument} parsers.
     */
    private final ArgumentParsers argumentParsers;

    /**
     * Create an instruction using the quoting tokenizer.
     *
     * @param variables   the variable processor to create and resolve variables
     * @param packManager the quest package manager to get quest packages from
     * @param pack        quest package the instruction belongs to
     * @param identifier  identifier of the instruction
     * @param parsers     The parsers to use for parsing the instruction's arguments.
     * @param instruction instruction string to parse
     * @throws QuestException if the instruction could not be tokenized
     */
    public DefaultInstruction(final Variables variables, final QuestPackageManager packManager, final QuestPackage pack,
                              @Nullable final Identifier identifier, final ArgumentParsers parsers, final String instruction) throws QuestException {
        this(variables, packManager, new QuotingTokenizer(), pack, useFallbackIdIfNecessary(packManager, pack, identifier), parsers, instruction);
    }

    /**
     * Create an instruction using the given tokenizer.
     *
     * @param variables   the variable processor to create and resolve variables
     * @param packManager the quest package manager to get quest packages from
     * @param tokenizer   Tokenizer that can split on spaces but interpret quotes and escapes.
     * @param pack        quest package the instruction belongs to
     * @param identifier  identifier of the instruction
     * @param parsers     The parsers to use for parsing the instruction's arguments.
     * @param instruction instruction string to parse
     * @throws QuestException if the instruction could not be tokenized
     */
    public DefaultInstruction(final Variables variables, final QuestPackageManager packManager, final Tokenizer tokenizer,
                              final QuestPackage pack, final Identifier identifier, final ArgumentParsers parsers, final String instruction) throws QuestException {
        this.variables = variables;
        this.packManager = packManager;
        this.pack = pack;
        this.identifier = identifier;
        this.instructionString = instruction;
        this.argumentParsers = parsers;
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
        this.argumentParsers = instruction.argumentParsers;
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

    @Override
    public QuestPackage getPackage() {
        return pack;
    }

    @Override
    public ArgumentParsers getParsers() {
        return argumentParsers;
    }

    @Override
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
    public String nextElement() throws QuestException {
        return instructionParts.nextElement();
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

    @Override
    public DefaultInstruction copy() {
        return copy(identifier);
    }

    @Override
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

    @Override
    public boolean hasArgument(final String argument) {
        return getParts().stream().anyMatch(part -> part.equalsIgnoreCase(argument));
    }

    @Override
    @Contract("!null, _, _ -> !null; _, _, !null -> !null")
    @Nullable
    public <T> Variable<T> get(@Nullable final String string, final InstructionArgumentParser<T> argument, @Nullable final T defaultValue) throws QuestException {
        if (string == null) {
            if (defaultValue != null) {
                return new DefaultVariable<>(defaultValue);
            }
            return null;
        }
        return new DefaultVariable<>(variables, pack, string, value -> argument.apply(variables, packManager, pack, value));
    }

    @Override
    public <T> Variable<List<T>> getList(@Nullable final String string, final InstructionArgumentParser<T> argument, final ValueChecker<List<T>> valueChecker) throws QuestException {
        if (string == null) {
            return new VariableList<>();
        }
        return new VariableList<>(variables, pack, string, value -> argument.apply(variables, packManager, pack, value), valueChecker);
    }

    @Override
    @Nullable
    public <T> Variable<T> get(@Nullable final String string, final IdentifierArgument<T> argument, @Nullable final T defaultValue) throws QuestException {
        if (string == null) {
            if (defaultValue != null) {
                return new DefaultVariable<>(defaultValue);
            }
            return null;
        }
        return new DefaultVariable<>(variables, pack, string, value -> argument.apply(packManager, pack, value));
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
                return new DefaultVariable<>(defaultValue);
            }
            return null;
        }
        return new DefaultVariable<>(variables, pack, string, value -> argument.apply(variables, packManager, pack, value));
    }

    @Override
    public <T> Variable<List<T>> getList(@Nullable final String string, final InstructionIdentifierArgument<T> argument, final ValueChecker<List<T>> valueChecker) throws QuestException {
        if (string == null) {
            return new VariableList<>();
        }
        return new VariableList<>(variables, pack, string, value -> argument.apply(variables, packManager, pack, value), valueChecker);
    }

    @Override
    public <T> Variable<T> getNext(final InstructionArgumentParser<T> argument) throws QuestException {
        return new DefaultVariable<>(variables, pack, nextElement(),
                value -> argument.apply(variables, packManager, pack, value));
    }

    @Override
    public <T> Variable<List<T>> getNextList(final InstructionArgumentParser<T> argument) throws QuestException {
        return new VariableList<>(variables, pack, nextElement(),
                value -> argument.apply(variables, packManager, pack, value));
    }

    @Override
    public <T> Optional<Variable<T>> getOptional(final String argumentKey, final InstructionArgumentParser<T> argument) throws QuestException {
        final String argumentValue = getValue(argumentKey);
        if (argumentValue == null) {
            return Optional.empty();
        } else {
            final ValueParser<T> valueParser = value -> argument.apply(variables, packManager, pack, value);
            return Optional.of(new DefaultVariable<>(variables, pack, argumentValue, valueParser));
        }
    }

    @Override
    public <T> Variable<T> getOptional(final String argumentKey, final InstructionArgumentParser<T> argument, final T defaultValue) throws QuestException {
        final String argumentValue = getValue(argumentKey);
        if (argumentValue == null) {
            return new DefaultVariable<>(defaultValue);
        } else {
            final ValueParser<T> valueParser = value -> argument.apply(variables, packManager, pack, value);
            return new DefaultVariable<>(variables, pack, argumentValue, valueParser);
        }
    }

    @Override
    public <T> Optional<Variable<List<T>>> getOptionalList(final String argumentKey, final InstructionArgumentParser<T> argument) throws QuestException {
        final String argumentValue = getValue(argumentKey);
        if (argumentValue == null) {
            return Optional.empty();
        } else {
            final ValueParser<T> valueParser = value -> argument.apply(variables, packManager, pack, value);
            return Optional.of(new VariableList<>(variables, pack, argumentValue, valueParser));
        }
    }

    @Override
    public <T> Variable<List<T>> getOptionalList(final String argumentKey, final InstructionArgumentParser<T> argument, final List<T> defaultList) throws QuestException {
        final String argumentValue = getValue(argumentKey);
        if (argumentValue == null) {
            return new VariableList<>(defaultList);
        } else {
            final ValueParser<T> valueParser = value -> argument.apply(variables, packManager, pack, value);
            return new VariableList<>(variables, pack, argumentValue, valueParser);
        }
    }

    @Override
    public <T> DecoratableChainRetriever<T> parse(final InstructionArgumentParser<T> argument) {
        return new DefaultDecoratableChainRetriever<>(this, argument);
    }

    @Override
    public DecoratableChainRetriever<String> string() {
        return new DefaultDecoratableChainRetriever<>(this, argumentParsers.string());
    }

    @Override
    public DecoratableChainRetriever<Boolean> bool() {
        return new DefaultDecoratableChainRetriever<>(this, argumentParsers.bool());
    }

    @Override
    public DecoratableChainRetriever<Vector> vector() {
        return new DefaultDecoratableChainRetriever<>(this, argumentParsers.vector());
    }

    @Override
    public DecoratableChainRetriever<World> world() {
        return new DefaultDecoratableChainRetriever<>(this, argumentParsers.world());
    }

    @Override
    public DecoratableChainRetriever<Location> location() {
        return new DefaultDecoratableChainRetriever<>(this, argumentParsers.location());
    }

    @Override
    public DecoratableChainRetriever<QuestItemWrapper> item() {
        return new DefaultDecoratableChainRetriever<>(this, argumentParsers.item());
    }

    @Override
    public DecoratableChainRetriever<String> packageIdentifier() {
        return new DefaultDecoratableChainRetriever<>(this, argumentParsers.packageIdentifier());
    }

    @Override
    public DecoratableChainRetriever<Component> component() {
        return new DefaultDecoratableChainRetriever<>(this, argumentParsers.component());
    }

    @Override
    public DecoratableChainRetriever<UUID> uuid() {
        return new DefaultDecoratableChainRetriever<>(this, argumentParsers.uuid());
    }

    @Override
    public NumberChainRetriever number() {
        return new DefaultNumberChainRetriever(this, argumentParsers.number());
    }

    @Override
    public <E extends Enum<E>> DecoratableChainRetriever<E> enumeration(final Class<E> enumType) {
        return new DefaultDecoratableChainRetriever<>(this, argumentParsers.forEnum(enumType));
    }
}
