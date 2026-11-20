package org.betonquest.betonquest.quest.placeholder.function;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.api.identifier.FunctionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.placeholder.NullablePlaceholder;
import org.betonquest.betonquest.api.service.function.Functions;
import org.betonquest.betonquest.lib.function.assignment.StringSourceAssignment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder that evaluates a function and returns its result as a string.
 * <p>
 * This placeholder resolves a function by its identifier, evaluates it with the provided assignments,
 * and returns the result. It can work with or without a profile context.
 */
public class FunctionPlaceholder implements NullablePlaceholder {

    /**
     * The {@link Functions} service used to retrieve and evaluate functions.
     */
    private final Functions functions;

    /**
     * The argument representing the function identifier to be resolved.
     */
    private final Argument<FunctionIdentifier> functionIdentifier;

    /**
     * The list of arguments representing function assignments to be passed to the function.
     */
    private final List<Argument<String>> assignments;

    /**
     * Creates a new function placeholder.
     *
     * @param functions          the {@link Functions} service used to retrieve and evaluate functions
     * @param functionIdentifier the argument representing the function identifier to be resolved
     * @param assignments        the list of arguments representing function assignments to be passed to the function
     */
    public FunctionPlaceholder(final Functions functions, final Argument<FunctionIdentifier> functionIdentifier,
                               final List<Argument<String>> assignments) {
        this.functions = functions;
        this.functionIdentifier = functionIdentifier;
        this.assignments = assignments;
    }

    @Override
    public String getValue(@Nullable final Profile profile) throws QuestException {
        final FunctionIdentifier identifier = functionIdentifier.getValue(profile);
        final List<FunctionAssignment> functionAssignments = new ArrayList<>(assignments.size());
        for (final Argument<String> assignment : assignments) {
            final String value = assignment.getValue(profile);
            functionAssignments.add(new StringSourceAssignment(value));
        }
        final FunctionAssignment evaluate = functions.evaluate(identifier, functionAssignments);
        return evaluate.asString();
    }
}
