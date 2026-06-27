package org.betonquest.betonquest.api.service.function;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.api.function.FunctionProvider;
import org.betonquest.betonquest.api.function.MathFunction;
import org.betonquest.betonquest.api.identifier.FunctionIdentifier;

import java.util.List;

/**
 * Grants access to loaded functions as well as subroutines and enables to evaluate them.
 * New subroutines may be registered too.
 *
 * @since 3.1.0
 */
public interface Functions extends FunctionProvider {

    /**
     * Registers a subroutine for a given name.
     *
     * @param name     the name of the subroutine
     * @param function the subroutine
     * @since 3.1.0
     */
    void registerSubRoutine(String name, MathFunction function);

    /**
     * Get a function by its identifier.
     *
     * @param identifier the identifier of the function
     * @return the function
     * @throws QuestException if the function does not exist
     * @since 3.1.0
     */
    MathFunction getFunction(FunctionIdentifier identifier) throws QuestException;

    /**
     * Evaluate a function obtained using the given identifier for the given arguments.
     *
     * @param identifier the function to evaluate
     * @param arguments  the arguments to pass to the function
     * @return the result of the function
     * @throws QuestException if the function could not be evaluated
     * @since 3.1.0
     */
    FunctionAssignment evaluate(FunctionIdentifier identifier, List<FunctionAssignment> arguments) throws QuestException;
}
