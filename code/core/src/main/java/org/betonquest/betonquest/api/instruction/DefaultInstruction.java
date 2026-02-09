package org.betonquest.betonquest.api.instruction;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestSupplier;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.identifier.NoID;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;
import org.betonquest.betonquest.api.instruction.chain.ChainableInstruction;
import org.betonquest.betonquest.api.instruction.chain.DecoratableChainRetriever;
import org.betonquest.betonquest.api.instruction.chain.InstructionChainParser;
import org.betonquest.betonquest.api.instruction.chain.NumberChainRetriever;
import org.betonquest.betonquest.api.instruction.tokenizer.QuotingTokenizer;
import org.betonquest.betonquest.api.instruction.tokenizer.Tokenizer;
import org.betonquest.betonquest.api.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.lib.instruction.argument.DefaultChainableInstruction;
import org.betonquest.betonquest.lib.instruction.argument.DefaultInstructionChainParser;
import org.betonquest.betonquest.lib.instruction.chain.DefaultDecoratableChainRetriever;
import org.betonquest.betonquest.lib.instruction.chain.DefaultNumberChainRetriever;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * The Instruction. Primary object for input parsing.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.CouplingBetweenObjects"})
public class DefaultInstruction implements Instruction {

    /**
     * The {@link Placeholders} to create and resolve placeholders.
     */
    private final Placeholders placeholders;

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
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
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
     * The default {@link SimpleArgumentParser} parsers.
     */
    private final ArgumentParsers argumentParsers;

    /**
     * The default chainable instruction this is based on.
     */
    private final ChainableInstruction chainableInstruction;

    /**
     * Create an instruction using the quoting tokenizer.
     *
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     * @param packManager  the quest package manager to get quest packages from
     * @param pack         quest package the instruction belongs to
     * @param identifier   identifier of the instruction
     * @param parsers      The parsers to use for parsing the instruction's arguments.
     * @param instruction  instruction string to parse
     * @throws QuestException if the instruction could not be tokenized
     */
    public DefaultInstruction(final Placeholders placeholders, final QuestPackageManager packManager, final QuestPackage pack,
                              @Nullable final Identifier identifier, final ArgumentParsers parsers, final String instruction) throws QuestException {
        this(placeholders, packManager, new QuotingTokenizer(), pack, useFallbackIdIfNecessary(pack, identifier), parsers, instruction);
    }

    /**
     * Create an instruction using the given tokenizer.
     *
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     * @param packManager  the quest package manager to get quest packages from
     * @param tokenizer    Tokenizer that can split on spaces but interpret quotes and escapes.
     * @param pack         quest package the instruction belongs to
     * @param identifier   identifier of the instruction
     * @param parsers      The parsers to use for parsing the instruction's arguments.
     * @param instruction  instruction string to parse
     * @throws QuestException if the instruction could not be tokenized
     */
    public DefaultInstruction(final Placeholders placeholders, final QuestPackageManager packManager, final Tokenizer tokenizer,
                              final QuestPackage pack, final Identifier identifier, final ArgumentParsers parsers, final String instruction) throws QuestException {
        this.placeholders = placeholders;
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
        this.chainableInstruction = new DefaultChainableInstruction(placeholders, packManager, pack,
                this.instructionParts::nextElement, this::getValue, this::getFlag);
    }

    /**
     * Copies an instruction using the given instruction and a new identifier.
     *
     * @param instruction instruction to copy
     * @param identifier  identifier of the new instruction
     */
    public DefaultInstruction(final DefaultInstruction instruction, final Identifier identifier) {
        this.placeholders = instruction.placeholders;
        this.packManager = instruction.packManager;
        this.pack = instruction.pack;
        this.identifier = identifier;
        this.instructionString = instruction.instructionString;
        this.instructionParts = new InstructionPartsArray(instruction.instructionParts);
        this.argumentParsers = instruction.argumentParsers;
        this.chainableInstruction = new DefaultChainableInstruction(placeholders, packManager, pack,
                this.instructionParts::nextElement, this::getValue, this::getFlag);
    }

    private static Identifier useFallbackIdIfNecessary(final QuestPackage pack, @Nullable final Identifier identifier) {
        if (identifier != null) {
            return identifier;
        }
        return new NoID(pack);
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

    @Nullable
    private String getValue(final String prefix) {
        return instructionParts.getParts().stream()
                .filter(part -> part.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT) + ":"))
                .findFirst()
                .map(part -> part.substring(prefix.length() + 1)).orElse(null);
    }

    private Map.Entry<FlagState, String> getFlag(final String prefix) {
        return instructionParts.getParts().stream()
                .filter(part -> part.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT) + ":")
                        || part.equalsIgnoreCase(prefix))
                .findFirst()
                .map(part -> part.substring(prefix.length()))
                .map(part -> part.startsWith(":") ? Map.entry(FlagState.DEFINED, part.substring(1))
                        : Map.entry(FlagState.UNDEFINED, part))
                .orElse(Map.entry(FlagState.ABSENT, ""));
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
    public InstructionChainParser chainForArgument(final QuestSupplier<String> rawArgumentSupplier) {
        final ChainableInstruction instruction = new DefaultChainableInstruction(placeholders, packManager, pack,
                rawArgumentSupplier, key -> rawArgumentSupplier.get(), key -> Map.entry(FlagState.DEFINED, key));
        return new DefaultInstructionChainParser(instruction, argumentParsers);
    }

    @Override
    public <T> Argument<T> getNext(final InstructionArgumentParser<T> argumentParser) throws QuestException {
        return chainableInstruction.getNext(argumentParser);
    }

    @Override
    public <T> Optional<Argument<T>> getOptional(final String argumentKey, final InstructionArgumentParser<T> argumentParser) throws QuestException {
        return chainableInstruction.getOptional(argumentKey, argumentParser);
    }

    @Override
    public <T> Argument<T> getOptional(final String argumentKey, final InstructionArgumentParser<T> argument, final T defaultValue) throws QuestException {
        return chainableInstruction.getOptional(argumentKey, argument, defaultValue);
    }

    @Override
    public <T> FlagArgument<T> getFlag(final String argumentKey, final InstructionArgumentParser<T> argumentParser, final T presenceDefault) throws QuestException {
        return chainableInstruction.getFlag(argumentKey, argumentParser, presenceDefault);
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
    public DecoratableChainRetriever<ItemWrapper> item() {
        return new DefaultDecoratableChainRetriever<>(this, argumentParsers.item());
    }

    @Override
    public DecoratableChainRetriever<BlockSelector> blockSelector() {
        return new DefaultDecoratableChainRetriever<>(this, argumentParsers.blockSelector());
    }

    @Override
    public DecoratableChainRetriever<String> packageIdentifier() {
        return new DefaultDecoratableChainRetriever<>(this, argumentParsers.packageIdentifier());
    }

    @Override
    public DecoratableChainRetriever<NamespacedKey> namespacedKey() {
        return new DefaultDecoratableChainRetriever<>(this, argumentParsers.namespacedKey());
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

    @Override
    public <I extends Identifier> DecoratableChainRetriever<I> identifier(final Class<I> identifierClass) {
        return new DefaultDecoratableChainRetriever<>(this, argumentParsers.forIdentifier(identifierClass));
    }
}
