package org.betonquest.betonquest.api.function;

import java.util.List;
import java.util.Map;

/**
 * Represents a function definition that can assign values to variables.
 *
 * @since 3.1.0
 */
@FunctionalInterface
public interface FunctionDefinition {

    /**
     * Assigns the given values to variables of this function.
     * Also completes the assignment with the default values if necessary.
     *
     * @param assignments the assignments to map
     * @return the assigned values
     * @since 3.1.0
     */
    Map<String, FunctionAssignment> assign(List<FunctionAssignment> assignments);
}
