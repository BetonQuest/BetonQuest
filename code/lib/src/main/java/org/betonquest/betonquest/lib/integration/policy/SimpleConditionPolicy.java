package org.betonquest.betonquest.lib.integration.policy;

import org.betonquest.betonquest.api.integration.policy.Policy;

import java.util.function.Supplier;

/**
 * Represents a simple implementation of the {@link Policy} interface based on a given condition and description.
 * The validation logic is determined by the provided {@code Supplier<Boolean>} condition.
 *
 * @param condition   the condition that determines the validation logic
 * @param description the description of the policy used for logging or debugging purposes
 */
public record SimpleConditionPolicy(Supplier<Boolean> condition, String description) implements Policy {

    @Override
    public boolean validate() {
        return condition.get();
    }
}
