package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.api.brew.Brew;
import dev.jsinco.brewery.api.brew.BrewManager;
import dev.jsinco.brewery.api.brew.BrewingStep;
import dev.jsinco.brewery.api.breweries.CauldronType;
import dev.jsinco.brewery.api.ingredient.Ingredient;
import dev.jsinco.brewery.api.moment.Moment;
import dev.jsinco.brewery.bukkit.api.event.transaction.CauldronExtractEvent;
import dev.jsinco.brewery.bukkit.api.transaction.ItemSource;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

/**
 * A brew cook objective.
 *
 * @param cauldronTypeArgument A cauldron type argument
 * @param cookTimeArgument     A cook time argument
 * @param ingredientsFuture    A parsed ingredients argument
 * @param brewManager          the brew manager provided by TheBrewingProject
 * @param service              The objective service
 */
public record BrewCookObjective(Argument<CauldronType> cauldronTypeArgument, Argument<Number> cookTimeArgument,
                                CompletableFuture<Map<Ingredient, Integer>> ingredientsFuture,
                                BrewManager<ItemStack> brewManager,
                                ObjectiveService service) implements Objective {

    /**
     * Handle brew extract events from cauldrons.
     *
     * @param event   The brew extract event
     * @param profile The player profile
     * @throws QuestException If any argument was invalid
     */
    public void handle(final CauldronExtractEvent event, final OnlineProfile profile) throws QuestException {
        final CauldronType cauldronType = cauldronTypeArgument.getValue(profile);
        final double cookTime = cookTimeArgument.getValue(profile).doubleValue();

        final ItemSource itemSource = event.getItemResult();
        if (itemSource == null) {
            return;
        }
        final Optional<Brew> brewOptional = brewManager.fromItem(itemSource.get());
        final Map<Ingredient, Integer> ingredients;
        try {
            ingredients = ingredientsFuture.orTimeout(10, TimeUnit.MICROSECONDS).join();
        } catch (final CompletionException e) {
            throw new QuestException(e);
        }
        if (brewOptional.isPresent()
                && brewOptional.get().lastCompletedStep() instanceof final BrewingStep.Cook cookStep
                && cookStep.time().moment() >= cookTime * Moment.MINUTE
                && cauldronType.appliesTo(cookStep.cauldronType())
                && IngredientsUtil.checkMatch(ingredients, cookStep.ingredients())) {
            service.complete(profile);
        }
    }

    @Override
    public ObjectiveService getService() {
        return service;
    }
}
