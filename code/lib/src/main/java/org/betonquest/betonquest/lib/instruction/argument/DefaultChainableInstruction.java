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
import org.betonquest.betonquest.api.quest.Variables;

import java.util.Map;
import java.util.Optional;

/**
 * The default implementation for {@link ChainableInstruction}.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class DefaultChainableInstruction implements ChainableInstruction {

    /**
     * The variable processor.
     */
    private final Variables variables;

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
    private final QuestSupplier<String> nextElementSupplier;

    /**
     * The function to retrieve the next element by its key.
     */
    private final QuestFunction<String, String> nextOptionalFunction;

    /**
     * The function to retrieve the next flag by its key.
     */
    private final QuestFunction<String, Map.Entry<FlagState, String>> nextFlagFunction;

    /**
     * Sole constructor.
     *
     * @param variables            the variable processor
     * @param packManager          the package manager
     * @param pack                 the related package
     * @param nextElementSupplier  the provider for the next element
     * @param nextOptionalFunction the provider for the next element by key
     * @param nextFlagFunction     the provider for the next flag by key
     */
    public DefaultChainableInstruction(final Variables variables, final QuestPackageManager packManager,
                                       final QuestPackage pack, final QuestSupplier<String> nextElementSupplier,
                                       final QuestFunction<String, String> nextOptionalFunction,
                                       final QuestFunction<String, Map.Entry<FlagState, String>> nextFlagFunction) {
        this.variables = variables;
        this.packManager = packManager;
        this.pack = pack;
        this.nextElementSupplier = nextElementSupplier;
        this.nextOptionalFunction = nextOptionalFunction;
        this.nextFlagFunction = nextFlagFunction;
    }

    @Override
    public <T> Argument<T> getNext(final InstructionArgumentParser<T> argumentParser) throws QuestException {
        return new DefaultArgument<>(variables, pack, nextElementSupplier.get(),
                value -> argumentParser.apply(variables, packManager, pack, value));
    }

    @Override
    public <T> Optional<Argument<T>> getOptional(final String argumentKey, final InstructionArgumentParser<T> argumentParser) throws QuestException {
        final String argumentValue = nextOptionalFunction.apply(argumentKey);
        if (argumentValue == null) {
            return Optional.empty();
        }
        final ValueParser<T> valueParser = value -> argumentParser.apply(variables, packManager, pack, value);
        return Optional.of(new DefaultArgument<>(variables, pack, argumentValue, valueParser));
    }

    @Override
    public <T> Argument<T> getOptional(final String argumentKey, final InstructionArgumentParser<T> argument, final T defaultValue) throws QuestException {
        final String argumentValue = nextOptionalFunction.apply(argumentKey);
        if (argumentValue == null) {
            return new DefaultArgument<>(defaultValue);
        }
        final ValueParser<T> valueParser = value -> argument.apply(variables, packManager, pack, value);
        return new DefaultArgument<>(variables, pack, argumentValue, valueParser);
    }

    @Override
    public <T> FlagArgument<T> getFlag(final String argumentKey, final InstructionArgumentParser<T> argumentParser, final T presenceDefault) throws QuestException {
        final Map.Entry<FlagState, String> flag = nextFlagFunction.apply(argumentKey);
        return switch (flag.getKey()) {
            case ABSENT -> new DefaultFlagArgument<>();
            case UNDEFINED -> new DefaultFlagArgument<>(presenceDefault, FlagState.UNDEFINED);
            case DEFINED -> new DefaultFlagArgument<>(variables, pack, flag.getValue(),
                    value -> Optional.of(argumentParser.apply(variables, packManager, pack, value)));
        };
    }
}
