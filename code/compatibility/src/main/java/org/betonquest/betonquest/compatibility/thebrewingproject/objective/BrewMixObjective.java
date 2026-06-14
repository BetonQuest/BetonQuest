package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.api.brew.Brew;
import dev.jsinco.brewery.api.brew.BrewingStep;
import dev.jsinco.brewery.api.breweries.CauldronType;
import dev.jsinco.brewery.api.ingredient.Ingredient;
import dev.jsinco.brewery.api.moment.Moment;
import dev.jsinco.brewery.bukkit.api.TheBrewingProjectApi;
import dev.jsinco.brewery.bukkit.api.event.transaction.CauldronExtractEvent;
import dev.jsinco.brewery.bukkit.api.transaction.ItemSource;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

/**
 * A brew mix objective.
 *
 * @param cauldronTypeArgument A cauldron type argument
 * @param mixTimeArgument      A mix time argument
 * @param ingredientsArgument  An ingredients argument
 * @param api                  TheBrewingProject API
 * @param service              The objective service
 */
public record BrewMixObjective(Argument<CauldronType> cauldronTypeArgument, Argument<Number> mixTimeArgument,
                               Argument<List<String>> ingredientsArgument, TheBrewingProjectApi api,
                               ObjectiveService service) implements Objective {

    @Override
    public ObjectiveService getService() {
        return service;
    }

    /**
     * Handle brew extract events from cauldrons.
     *
     * @param event   The brew extract event
     * @param profile The player profile
     * @throws QuestException If any argument was invalid
     */
    public void handle(final CauldronExtractEvent event, final OnlineProfile profile) throws QuestException {
        final CauldronType cauldronType = cauldronTypeArgument.getValue(profile);
        final double mixTime = mixTimeArgument.getValue(profile).doubleValue();

        final CompletableFuture<Map<Ingredient, Integer>> ingredientsFuture = api.getIngredientManager()
                .getIngredientsWithAmount(ingredientsArgument.getValue(profile));
        final ItemSource itemSource = event.getItemResult();
        if (itemSource == null) {
            return;
        }
        final Optional<Brew> brewOptional = api.getBrewManager().fromItem(itemSource.get());
        final Map<Ingredient, Integer> ingredients;
        try {
            ingredients = ingredientsFuture.orTimeout(10, TimeUnit.MICROSECONDS).join();
        } catch (final CompletionException e) {
            throw new QuestException(e);
        }
        if (brewOptional.isPresent()
                && brewOptional.get().lastCompletedStep() instanceof final BrewingStep.Mix mixStep
                && mixStep.time().moment() > mixTime * Moment.MINUTE
                && cauldronType.appliesTo(mixStep.cauldronType())
                && IngredientsUtil.checkMatch(ingredients, mixStep.ingredients())) {
            service.complete(profile);
        }
    }
}
