package org.betonquest.betonquest.quest.placeholder.function;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.FunctionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.NullablePlaceholderAdapter;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholderFactory;
import org.betonquest.betonquest.api.service.function.Functions;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for creating {@link FunctionPlaceholder} instances from {@link Instruction}s.
 */
public class FunctionPlaceholderFactory implements PlayerlessPlaceholderFactory, PlayerPlaceholderFactory {

    /**
     * The {@link Functions} service used to retrieve and evaluate functions.
     */
    private final Functions functions;

    /**
     * Creates a new function placeholder factory.
     *
     * @param functions the {@link Functions} service used to retrieve and evaluate functions
     */
    public FunctionPlaceholderFactory(final Functions functions) {
        this.functions = functions;
    }

    @Override
    public PlayerlessPlaceholder parsePlayerless(final Instruction instruction) throws QuestException {
        return parseInstruction(instruction);
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) throws QuestException {
        return parseInstruction(instruction);
    }

    private NullablePlaceholderAdapter parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<FunctionIdentifier> functionIdentifier = instruction.identifier(FunctionIdentifier.class).get();
        final List<Argument<String>> assignments = new ArrayList<>();
        while (instruction.hasNext()) {
            assignments.add(readValue(instruction));
        }
        return new NullablePlaceholderAdapter(new FunctionPlaceholder(functions, functionIdentifier, assignments));
    }

    private Argument<String> readValue(final Instruction instruction) throws QuestException {
        final String start = instruction.nextElement();
        if (!start.startsWith("{")) {
            return new DefaultArgument<>(start);
        }
        final StringBuilder builder = new StringBuilder();
        builder.append(start.substring(1)).append('.');
        while (true) {
            final String current = instruction.nextElement();
            if (current.endsWith("}")) {
                builder.append(current, 0, current.length() - 1);
                return new DefaultArgument<>(builder.toString());
            }
            builder.append(current).append('.');
        }
    }
}
