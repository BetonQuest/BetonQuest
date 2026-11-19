package org.betonquest.betonquest.particle.deltafunction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.function.FunctionProvider;
import org.betonquest.betonquest.api.function.MathFunction;
import org.betonquest.betonquest.api.identifier.FunctionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.service.function.Functions;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;

/**
 * Parses a delta function.
 * Produces either a {@link ConstantDeltaFunction} or a {@link DeltaMathFunction} from a {@link FunctionIdentifier}.
 *
 * @param functionIdentifierFactory the identifier factory to create {@link FunctionIdentifier}s
 * @param functions                 the function service
 */
public record DeltaFunctionParser(IdentifierFactory<FunctionIdentifier> functionIdentifierFactory,
                                  Functions functions) implements InstructionArgumentParser<DeltaFunction> {

    @Override
    public DeltaFunction apply(final PlaceholderManager placeholders, final QuestPackageManager packManager, final QuestPackage pack, final String string) throws QuestException {
        try {
            final double value = Double.parseDouble(string);
            return new ConstantDeltaFunction(value);
        } catch (final NumberFormatException e) {
            final FunctionIdentifier functionIdentifier = functionIdentifierFactory.parseIdentifier(pack, string);
            final MathFunction function = functions.getFunction(functionIdentifier);
            final FunctionProvider functionProvider = functions.getFunctionProvider(functionIdentifier.getPackage());
            return new DeltaMathFunction(functionProvider, function);
        }
    }
}
