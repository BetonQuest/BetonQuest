package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.api.function.FunctionProvider;
import org.betonquest.betonquest.api.function.MathFunction;
import org.betonquest.betonquest.api.identifier.FunctionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.service.function.Functions;
import org.betonquest.betonquest.id.function.DefaultFunctionIdentifier;
import org.betonquest.betonquest.kernel.processor.QuestProcessor;
import org.betonquest.betonquest.lib.function.FunctionParser;
import org.betonquest.betonquest.lib.function.token.FunctionToken;
import org.betonquest.betonquest.lib.function.token.FunctionTokenizer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores and evaluates {@link MathFunction}s.
 */
public class FunctionProcessor extends QuestProcessor<FunctionIdentifier, MathFunction> implements Functions {

    /**
     * Available subroutines for execution inside other functions.
     */
    private final Map<String, MathFunction> subroutines;

    /**
     * Tokenizer for strings into {@link FunctionToken}s.
     */
    private final FunctionTokenizer functionTokenizer;

    /**
     * Parser for {@link FunctionToken}s into {@link MathFunction}s.
     */
    private final FunctionParser functionParser;

    /**
     * Create a new QuestProcessor to store and evaluate {@link MathFunction}s.
     *
     * @param log               the custom logger for this class
     * @param identifierFactory the identifier factory to create {@link FunctionIdentifier}s for this type
     */
    public FunctionProcessor(final BetonQuestLogger log, final IdentifierFactory<FunctionIdentifier> identifierFactory) {
        super(log, identifierFactory, "Function", DefaultFunctionIdentifier.FUNCTION_SECTION);
        this.subroutines = new HashMap<>();
        this.functionTokenizer = new FunctionTokenizer();
        this.functionParser = new FunctionParser();
    }

    @Override
    public void load(final QuestPackage pack) {
        final ConfigurationSection functionSection = pack.getConfig().getConfigurationSection(internal);
        if (functionSection == null) {
            return;
        }
        for (final String key : functionSection.getKeys(false)) {
            final String rawFunction = functionSection.getString(key);
            if (rawFunction == null) {
                log.warn("Could not load function '%s' with null value in pack '%s'".formatted(key, pack.getQuestPath()));
                continue;
            }
            final FunctionIdentifier functionIdentifier;
            try {
                functionIdentifier = getIdentifier(pack, key);
            } catch (final QuestException e) {
                log.warn("Could not load function '%s' with invalid identifier in pack '%s': %s".formatted(key, pack.getQuestPath(), e.getMessage()), e);
                continue;
            }
            final List<FunctionToken> tokens = functionTokenizer.tokenize(rawFunction);
            final MathFunction mathFunction;
            try {
                mathFunction = functionParser.parseMathFunction(tokens);
            } catch (final QuestException e) {
                log.warn("Could not load function '%s' in pack '%s'. Invalid function definition could not be parsed: %s".formatted(key, pack.getQuestPath(), e.getMessage()), e);
                continue;
            }
            values.put(functionIdentifier, mathFunction);
        }
    }

    @Override
    public void registerSubRoutine(final String name, final MathFunction function) {
        if (subroutines.containsKey(name)) {
            log.warn("Subroutine '%s' overwritten with new implementation.".formatted(name));
        }
        subroutines.put(name, function);
    }

    @Override
    public MathFunction getFunction(final FunctionIdentifier identifier) throws QuestException {
        return get(identifier);
    }

    @Override
    public FunctionAssignment evaluate(final FunctionIdentifier identifier, final List<FunctionAssignment> arguments) throws QuestException {
        return get(identifier).evaluate(getFunctionProvider(identifier.getPackage()), arguments);
    }

    @Override
    public FunctionProvider getFunctionProvider(@Nullable final QuestPackage questPackage) {
        return new FunctionProvider() {
            @Override
            public MathFunction getFunction(final String identifier) throws QuestException {
                return get(getIdentifier(questPackage, identifier));
            }

            @Override
            public MathFunction getSubRoutine(final String name) throws QuestException {
                if (!subroutines.containsKey(name)) {
                    throw new QuestException("Subroutine '%s' does not exist".formatted(name));
                }
                return subroutines.get(name);
            }
        };
    }
}
