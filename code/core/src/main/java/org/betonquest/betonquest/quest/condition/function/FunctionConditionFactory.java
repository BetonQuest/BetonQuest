package org.betonquest.betonquest.quest.condition.function;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.api.identifier.FunctionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.NullableConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.service.function.Functions;
import org.betonquest.betonquest.lib.function.assignment.StringSourceAssignment;

import java.util.List;

/**
 * Creates new {@link FunctionCondition}s from {@link Instruction}s.
 */
public class FunctionConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * The function source to use.
     */
    private final Functions functions;

    /**
     * Creates a new function condition factory.
     *
     * @param functions the function source to use
     */
    public FunctionConditionFactory(final Functions functions) {
        this.functions = functions;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return parse(instruction);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return parse(instruction);
    }

    private NullableConditionAdapter parse(final Instruction instruction) throws QuestException {
        final Argument<FunctionIdentifier> functionIdentifier = instruction.identifier(FunctionIdentifier.class).get();
        final Argument<List<FunctionAssignment>> arguments = instruction.string()
                .map(StringSourceAssignment::new)
                .map(FunctionAssignment.class::cast)
                .list()
                .get();
        return new NullableConditionAdapter(new FunctionCondition(functions, functionIdentifier, arguments));
    }
}
