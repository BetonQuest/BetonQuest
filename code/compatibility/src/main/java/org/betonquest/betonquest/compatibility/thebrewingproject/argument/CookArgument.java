package org.betonquest.betonquest.compatibility.thebrewingproject.argument;

import dev.jsinco.brewery.api.brew.Brew;
import dev.jsinco.brewery.api.brew.BrewingStep;
import dev.jsinco.brewery.api.breweries.CauldronType;
import dev.jsinco.brewery.api.ingredient.Ingredient;
import dev.jsinco.brewery.api.ingredient.IngredientManager;
import dev.jsinco.brewery.api.moment.Moment;
import dev.jsinco.brewery.api.util.BreweryRegistry;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * A collection of arguments representing a cook step argument.
 *
 * @param cauldronTypeArgument A cauldron type argument
 * @param cookTimeArgument     A cooking time argument
 * @param ingredientsArgument  An ingredients argument
 * @param ingredientManager    The ingredients manager provided by TheBrewingProject
 */
public record CookArgument(Argument<CauldronType> cauldronTypeArgument, Argument<Number> cookTimeArgument,
                           Argument<List<String>> ingredientsArgument,
                           IngredientManager<?> ingredientManager) implements Argument<QuestFunction<Brew, Boolean>> {

    /**
     * Interpret the instruction from an instruction chain.
     *
     * @param instruction       The instruction chain to use.
     * @param ingredientManager The ingredient manager provided by TheBrewingProject
     * @return A cook argument
     * @throws QuestException If any of the arguments are invalid
     */
    public static CookArgument fromChain(final Instruction instruction, final IngredientManager<?> ingredientManager) throws QuestException {
        final Argument<CauldronType> cauldronTypeArgument = instruction.parse(new BreweryKeyedParser<>(BreweryRegistry.CAULDRON_TYPE)).get();
        final Argument<Number> cookTimeArgument = instruction.number().atLeast(0).get();
        final Argument<List<String>> ingredientsArgument = instruction.string().list().get();
        return new CookArgument(cauldronTypeArgument, cookTimeArgument, ingredientsArgument, ingredientManager);
    }

    @Override
    public QuestFunction<Brew, Boolean> getValue(@Nullable final Profile profile) throws QuestException {
        final CauldronType cauldronType = cauldronTypeArgument.getValue(profile);
        final double cookTime = cookTimeArgument.getValue(profile).doubleValue();
        final List<String> ingredientsStrings = ingredientsArgument.getValue(profile);
        final CompletableFuture<Map<Ingredient, Integer>> ingredientsFuture = ingredientManager.getIngredientsWithAmount(ingredientsStrings);
        return brew -> {
            if (!ingredientsFuture.isDone()) {
                return false;
            }
            try {
                if (!(brew.lastCompletedStep() instanceof final BrewingStep.Cook cookStep)) {
                    return false;
                }
                return cookStep.time().moment() > cookTime * Moment.MINUTE
                        && cauldronType.appliesTo(cookStep.cauldronType())
                        && IngredientsUtil.checkMatch(ingredientsFuture.join(), cookStep.ingredients());
            } catch (final CompletionException e) {
                throw new QuestException(e);
            }
        };
    }
}
