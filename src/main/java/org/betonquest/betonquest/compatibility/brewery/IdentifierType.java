package org.betonquest.betonquest.compatibility.brewery;

import com.dre.brewery.recipe.BRecipe;
import org.betonquest.betonquest.api.quest.QuestException;

import java.util.Optional;

/**
 * Allows to get a brewery recipe by different attributes.
 */
public enum IdentifierType {
    /**
     * Gets the brewery recipe by its id.
     */
    @SuppressWarnings("PMD.ShortVariable")
    ID {
        @Override
        public BRecipe getRecipeOrThrow(final String name) throws QuestException {
            for (final BRecipe recipe : BRecipe.getAllRecipes()) {
                final Optional<String> id = recipe.getOptionalID();
                if (id.isPresent() && id.get().equals(name)) {
                    return recipe;
                }
            }
            throw new QuestException("The recipe with the id " + name + " does not exist");
        }
    },
    /**
     * Gets the brewery recipe by (display) name.
     */
    NAME {
        @Override
        public BRecipe getRecipeOrThrow(final String name) throws QuestException {
            final BRecipe recipe = BRecipe.get(name);
            if (recipe == null) {
                throw new QuestException("There is no brewing recipe with the name " + name + "!");
            }
            return recipe;
        }
    };

    /**
     * Get a brewing recipe or throw a {@link QuestException} if it does not exist.
     *
     * @param name the name.
     * @return the {@link BRecipe}.
     * @throws QuestException if there is no such brewing recipe.
     */
    public abstract BRecipe getRecipeOrThrow(String name) throws QuestException;
}
