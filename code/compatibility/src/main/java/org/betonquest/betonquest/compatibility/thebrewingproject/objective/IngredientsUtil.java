package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.api.ingredient.BaseIngredient;
import dev.jsinco.brewery.api.ingredient.Ingredient;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Utility class for evaluating ingredients.
 */
public final class IngredientsUtil {

    private IngredientsUtil() {
    }

    /**
     * Check if the actual ingredients are all greater than the expected ingredients.
     *
     * @param expected the expected ingredients
     * @param actual   the actual ingredients to compare
     * @return true if all actual ingredients has more or equal amount than expected, otherwise false
     */
    public static boolean checkMatch(final Map<Ingredient, Integer> expected, final Map<? extends Ingredient, Integer> actual) {
        final Map<BaseIngredient, Integer> actualIngredients = new HashMap<>(actual.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey().toBaseIngredient(), entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
        for (final Map.Entry<Ingredient, Integer> entry : expected.entrySet()) {
            final Optional<BaseIngredient> matchOptional = entry.getKey().findMatch(actualIngredients.keySet())
                    .map(Ingredient::toBaseIngredient);
            if (matchOptional.isEmpty()) {
                return false;
            }
            final int actualAmount = actualIngredients.remove(matchOptional.get());
            if (actualAmount < entry.getValue()) {
                return false;
            }
        }
        return actualIngredients.isEmpty();
    }
}
