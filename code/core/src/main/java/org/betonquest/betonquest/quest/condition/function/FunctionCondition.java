package org.betonquest.betonquest.quest.condition.function;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.api.identifier.FunctionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.NullableCondition;
import org.betonquest.betonquest.api.service.function.Functions;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A condition that calls a function and interprets the result as a boolean condition.
 */
public class FunctionCondition implements NullableCondition {

    /**
     * The function provider to use.
     */
    private final Functions functions;

    /**
     * The function to call.
     */
    private final Argument<FunctionIdentifier> function;

    /**
     * The parameters to pass to the function.
     */
    private final Argument<List<FunctionAssignment>> parameters;

    /**
     * Create a new function condition.
     *
     * @param functions  the function provider
     * @param function   the function to call
     * @param parameters the parameters to pass to the function
     */
    public FunctionCondition(final Functions functions, final Argument<FunctionIdentifier> function, final Argument<List<FunctionAssignment>> parameters) {
        this.functions = functions;
        this.function = function;
        this.parameters = parameters;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final List<FunctionAssignment> values = parameters.getValue(profile);
        final FunctionIdentifier functionIdentifier = function.getValue(profile);
        return functions.evaluate(functionIdentifier, values).asBoolean();
    }
}
