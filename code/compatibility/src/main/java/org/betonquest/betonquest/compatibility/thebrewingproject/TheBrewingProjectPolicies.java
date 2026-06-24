package org.betonquest.betonquest.compatibility.thebrewingproject;

import org.betonquest.betonquest.api.integration.policy.Policy;
import org.betonquest.betonquest.lib.integration.policy.Policies;

/**
 * Utility class for getting all policies for TheBrewingProject's integration.
 */
public final class TheBrewingProjectPolicies {

    private TheBrewingProjectPolicies() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * The requirements for the right version of TheBrewingProject to be run.
     *
     * @return all policies for TheBrewingProject to run
     */
    public static Policy[] policies() {
        return new Policy[]{
                Policies.requireClass("dev.jsinco.brewery.api.ingredient.ResolvedIngredientManager")
        };
    }
}
