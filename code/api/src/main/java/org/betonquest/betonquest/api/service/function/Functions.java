package org.betonquest.betonquest.api.service.function;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.api.function.FunctionProvider;
import org.betonquest.betonquest.api.function.MathFunction;
import org.betonquest.betonquest.api.identifier.FunctionIdentifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Provides access to loaded functions and subroutines, allowing their evaluation
 * and the registration of new subroutines.
 *
 * @since 3.1.0
 */
public interface Functions {

    /**
     * Registers a new subroutine with the given name.
     *
     * @param name     the name of the subroutine
     * @param function the function implementing the subroutine
     * @since 3.1.0
     */
    void registerSubRoutine(String name, MathFunction function);

    /**
     * Returns a function by its identifier.
     *
     * @param identifier the identifier of the function to retrieve
     * @return the function instance
     * @throws QuestException if the function does not exist
     * @since 3.1.0
     */
    MathFunction getFunction(FunctionIdentifier identifier) throws QuestException;

    /**
     * Evaluates a function identified by the given identifier using the provided arguments.
     *
     * @param identifier the identifier of the function to evaluate
     * @param arguments  the arguments to pass to the function
     * @return the result of the evaluation
     * @throws QuestException if the function cannot be evaluated
     * @since 3.1.0
     */
    FunctionAssignment evaluate(FunctionIdentifier identifier, List<FunctionAssignment> arguments) throws QuestException;

    /**
     * Returns the function provider for the specified quest package.
     * <p>
     * The quest package is used to resolve relative identifiers.
     *
     * @param questPackage the quest package for resolving relative identifiers, or {@code null}
     * @return the function provider for the given quest package
     * @since 3.1.0
     */
    FunctionProvider getFunctionProvider(@Nullable QuestPackage questPackage);
}
