package org.betonquest.betonquest.lib.instruction.argument;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.common.function.QuestSupplier;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.FlagState;
import org.betonquest.betonquest.api.instruction.ValueParser;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.chain.ChainableInstruction;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;
import org.betonquest.betonquest.lib.instruction.reader.InstructionReaderStrategy;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * The default implementation for {@link ChainableInstruction}.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class DefaultChainableInstruction implements ChainableInstruction {

    /**
     * The {@link PlaceholderManager} to create and resolve placeholders.
     */
    private final PlaceholderManager placeholders;

    /**
     * The package manager.
     */
    private final QuestPackageManager packManager;

    /**
     * The package this instruction is related to.
     */
    private final QuestPackage pack;

    /**
     * The supplier providing the next element to parse.
     */
    private final InstructionReaderStrategy<String> instructionReaderStrategy;

    /**
     * Deprecated constructor.
     * <p>
     * See {@link InstructionReaderStrategy} to use the new constructor.
     *
     * @param placeholders          the {@link PlaceholderManager} to create and resolve placeholders
     * @param packManager           the package manager
     * @param pack                  the related package
     * @param nextElementSupplier   the provider for the next element
     * @param nextOptionalFunction  the provider for the next element by key
     * @param nextFlagFunction      the provider for the next flag by key
     * @param namedElementsFunction the provider for the next named elements by a key filter
     * @deprecated Use {@link #DefaultChainableInstruction(PlaceholderManager,
     * QuestPackageManager, QuestPackage, InstructionReaderStrategy)} instead.
     */
    @Deprecated
    public DefaultChainableInstruction(final PlaceholderManager placeholders, final QuestPackageManager packManager,
                                       final QuestPackage pack, final QuestSupplier<String> nextElementSupplier,
                                       final QuestFunction<String, String> nextOptionalFunction,
                                       final QuestFunction<String, Map.Entry<FlagState, String>> nextFlagFunction,
                                       final QuestFunction<Predicate<String>, Map<String, String>> namedElementsFunction) {
        this(placeholders, packManager, pack, new InstructionReaderStrategy<>() {
            @Override
            public String getNext() throws QuestException {
                return nextElementSupplier.get();
            }

            @Override
            public @Nullable String getOptional(final String prefix) throws QuestException {
                return nextOptionalFunction.apply(prefix);
            }

            @Override
            public Map.Entry<FlagState, String> getFlag(final String prefix) throws QuestException {
                return nextFlagFunction.apply(prefix);
            }

            @Override
            public Map<String, String> getNamed(final Predicate<String> keyFilter) throws QuestException {
                return namedElementsFunction.apply(keyFilter);
            }
        });
    }

    /**
     * Creates a new {@link DefaultChainableInstruction} instance.
     *
     * @param placeholders              the {@link PlaceholderManager} to create and resolve placeholders
     * @param packManager               the package manager
     * @param pack                      the related package
     * @param instructionReaderStrategy the strategy to read the arguments from the instruction
     */
    public DefaultChainableInstruction(final PlaceholderManager placeholders, final QuestPackageManager packManager,
                                       final QuestPackage pack, final InstructionReaderStrategy<String> instructionReaderStrategy) {
        this.placeholders = placeholders;
        this.packManager = packManager;
        this.pack = pack;
        this.instructionReaderStrategy = instructionReaderStrategy;
    }

    @Override
    public <T> Argument<T> getNext(final InstructionArgumentParser<T> argumentParser) throws QuestException {
        return new DefaultArgument<>(placeholders, pack, instructionReaderStrategy.getNext(),
                value -> argumentParser.apply(placeholders, packManager, pack, value));
    }

    @Override
    public <T> Optional<Argument<T>> getOptional(final String argumentKey, final InstructionArgumentParser<T> argumentParser) throws QuestException {
        final String argumentValue = instructionReaderStrategy.getOptional(argumentKey);
        if (argumentValue == null) {
            return Optional.empty();
        }
        final ValueParser<T> valueParser = value -> argumentParser.apply(placeholders, packManager, pack, value);
        return Optional.of(new DefaultArgument<>(placeholders, pack, argumentValue, valueParser));
    }

    @Override
    public <T> Argument<T> getOptional(final String argumentKey, final InstructionArgumentParser<T> argument, final T defaultValue) throws QuestException {
        final String argumentValue = instructionReaderStrategy.getOptional(argumentKey);
        if (argumentValue == null) {
            return new DefaultArgument<>(defaultValue);
        }
        final ValueParser<T> valueParser = value -> argument.apply(placeholders, packManager, pack, value);
        return new DefaultArgument<>(placeholders, pack, argumentValue, valueParser);
    }

    @Override
    public <T> FlagArgument<T> getFlag(final String argumentKey, final InstructionArgumentParser<T> argumentParser, final T presenceDefault) throws QuestException {
        final Map.Entry<FlagState, String> flag = instructionReaderStrategy.getFlag(argumentKey);
        return switch (flag.getKey()) {
            case ABSENT -> new DefaultFlagArgument<>();
            case UNDEFINED -> new DefaultFlagArgument<>(presenceDefault, FlagState.UNDEFINED);
            case DEFINED -> new DefaultFlagArgument<>(placeholders, pack, flag.getValue(),
                    value -> Optional.of(argumentParser.apply(placeholders, packManager, pack, value)));
        };
    }

    @Override
    public <T> Map<String, Argument<T>> getNamed(final InstructionArgumentParser<T> argumentParser, final Predicate<String> keyFilter) throws QuestException {
        final Map<String, String> map = instructionReaderStrategy.getNamed(keyFilter);
        final Map<String, Argument<T>> result = new HashMap<>();
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            result.put(entry.getKey(), new DefaultArgument<>(placeholders, pack, entry.getValue(),
                    value -> argumentParser.apply(placeholders, packManager, pack, value)));
        }
        return result;
    }
}
