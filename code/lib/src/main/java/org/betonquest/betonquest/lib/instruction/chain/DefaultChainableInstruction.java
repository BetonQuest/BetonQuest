package org.betonquest.betonquest.lib.instruction.chain;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.common.function.QuestSupplier;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.chain.ChainableInstruction;
import org.betonquest.betonquest.api.instruction.variable.ValueParser;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.lib.instruction.variable.DefaultVariable;
import org.betonquest.betonquest.lib.instruction.variable.VariableList;

import java.util.List;
import java.util.Optional;

/**
 * A default implementation for {@link ChainableInstruction}.
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
     * The related package.
     */
    private final QuestPackage pack;

    /**
     * A supplier for the next element in the chain.
     */
    private final QuestSupplier<String> nextElementSupplier;

    /**
     * A supplier (function) for the next key-value mapped element in the chain returning just the value.
     */
    private final QuestFunction<String, String> nextOptionalElementSupplier;

    /**
     * Sole constructor.
     *
     * @param variables                   the variable processor
     * @param packManager                 the package manager
     * @param pack                        the related package
     * @param nextElementSupplier         the next element supplier
     * @param nextOptionalElementSupplier the next key-value element supplier
     */
    public DefaultChainableInstruction(final Variables variables, final QuestPackageManager packManager,
                                       final QuestPackage pack, final QuestSupplier<String> nextElementSupplier,
                                       final QuestFunction<String, String> nextOptionalElementSupplier) {
        this.variables = variables;
        this.packManager = packManager;
        this.pack = pack;
        this.nextElementSupplier = nextElementSupplier;
        this.nextOptionalElementSupplier = nextOptionalElementSupplier;
    }

    @Override
    public <T> Variable<T> getNext(final InstructionArgumentParser<T> argument) throws QuestException {
        return new DefaultVariable<>(variables, pack, nextElementSupplier.get(),
                value -> argument.apply(variables, packManager, pack, value));
    }

    @Override
    public <T> Variable<List<T>> getNextList(final InstructionArgumentParser<T> argument) throws QuestException {
        return new VariableList<>(variables, pack, nextElementSupplier.get(),
                value -> argument.apply(variables, packManager, pack, value));
    }

    @Override
    public <T> Optional<Variable<T>> getOptional(final String argumentKey, final InstructionArgumentParser<T> argument) throws QuestException {
        final String argumentValue = nextOptionalElementSupplier.apply(argumentKey);
        if (argumentValue == null) {
            return Optional.empty();
        } else {
            final ValueParser<T> valueParser = value -> argument.apply(variables, packManager, pack, value);
            return Optional.of(new DefaultVariable<>(variables, pack, argumentValue, valueParser));
        }
    }

    @Override
    public <T> Variable<T> getOptional(final String argumentKey, final InstructionArgumentParser<T> argument, final T defaultValue) throws QuestException {
        final String argumentValue = nextOptionalElementSupplier.apply(argumentKey);
        if (argumentValue == null) {
            return new DefaultVariable<>(defaultValue);
        } else {
            final ValueParser<T> valueParser = value -> argument.apply(variables, packManager, pack, value);
            return new DefaultVariable<>(variables, pack, argumentValue, valueParser);
        }
    }

    @Override
    public <T> Optional<Variable<List<T>>> getOptionalList(final String argumentKey, final InstructionArgumentParser<T> argument) throws QuestException {
        final String argumentValue = nextOptionalElementSupplier.apply(argumentKey);
        if (argumentValue == null) {
            return Optional.empty();
        } else {
            final ValueParser<T> valueParser = value -> argument.apply(variables, packManager, pack, value);
            return Optional.of(new VariableList<>(variables, pack, argumentValue, valueParser));
        }
    }

    @Override
    public <T> Variable<List<T>> getOptionalList(final String argumentKey, final InstructionArgumentParser<T> argument, final List<T> defaultList) throws QuestException {
        final String argumentValue = nextOptionalElementSupplier.apply(argumentKey);
        if (argumentValue == null) {
            return new VariableList<>(defaultList);
        } else {
            final ValueParser<T> valueParser = value -> argument.apply(variables, packManager, pack, value);
            return new VariableList<>(variables, pack, argumentValue, valueParser);
        }
    }
}
