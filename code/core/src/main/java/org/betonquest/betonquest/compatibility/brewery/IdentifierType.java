package org.betonquest.betonquest.compatibility.brewery;

import com.dre.brewery.recipe.BRecipe;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
            try {
                for (final BRecipe recipe : BRecipe.getAllRecipes()) {
                    final Optional<String> id = recipe.getOptionalID();
                    if (id.isPresent() && id.get().equals(name)) {
                        return recipe;
                    }
                }
            } catch (final NoSuchMethodError ignored) {
                try {
                    return getByDirectId(name);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    throw new QuestException("Could not get 'getID' method for recipe: " + e.getMessage(), e);
                }
            }
            throw new QuestException("The recipe with the id " + name + " does not exist");
        }

        private BRecipe getByDirectId(final String name) throws NoSuchMethodException, InvocationTargetException,
                IllegalAccessException, QuestException {
            if (getIdMethod == null) {
                getIdMethod = BRecipe.class.getMethod("getId");
            }
            for (final BRecipe recipe : BRecipe.getAllRecipes()) {
                final String id = (String) getIdMethod.invoke(recipe);
                if (id.equals(name)) {
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
     * The direct return of a Recipe's id.
     * The method returning an optional was removed in BreweryX 3.2.9.
     */
    @Nullable
    private static Method getIdMethod;

    /**
     * Get a brewing recipe or throw a {@link QuestException} if it does not exist.
     *
     * @param name the name.
     * @return the {@link BRecipe}.
     * @throws QuestException if there is no such brewing recipe.
     */
    public abstract BRecipe getRecipeOrThrow(String name) throws QuestException;
}
