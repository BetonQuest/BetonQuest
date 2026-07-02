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
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A brew mix objective.
 *
 * @param cauldronTypeArgument a cauldron type argument
 * @param mixTimeArgument      a mix time argument
 * @param ingredientsArgument  an ingredients argument
 * @param api                  the TheBrewingProject API
 * @param service              the objective service
 */
public record BrewMixObjective(TheBrewingProjectApi api, ObjectiveService service,
                               FlagArgument<CauldronType> cauldronTypeArgument, Argument<Number> mixTimeArgument,
                               Argument<List<String>> ingredientsArgument) implements Objective {

    /**
     * Handle brew extract events from cauldrons.
     *
     * @param event   the brew extract event
     * @param profile the player profile
     * @throws QuestException if any argument was invalid
     */
    public void handle(final CauldronExtractEvent event, final OnlineProfile profile) throws QuestException {
        final CauldronType cauldronType = cauldronTypeArgument.getValue(profile).orElse(null);
        final double mixTime = mixTimeArgument.getValue(profile).doubleValue();

        final ItemSource itemSource = event.getItemResult();
        if (itemSource == null) {
            return;
        }
        final Optional<Brew> brewOptional = api.getBrewManager().fromItem(itemSource.get());
        final Map<Ingredient, Integer> ingredients;
        try {
            ingredients = api.getResolvedIngredientManager().get(10, TimeUnit.MICROSECONDS)
                    .getIngredientsWithAmount(ingredientsArgument.getValue(profile));
        } catch (final CompletionException | ExecutionException | InterruptedException | TimeoutException e) {
            throw new QuestException(e);
        }
        if (brewOptional.map(brew -> filterBrew(brew, mixTime, cauldronType, ingredients)).isPresent()) {
            service.complete(profile);
        }
    }

    private boolean filterBrew(final Brew brew, final double mixTime, @Nullable final CauldronType cauldronType, final Map<Ingredient, Integer> ingredients) {
        return brew.stepMatches(-1, BrewingStep.Mix.class, mixStep ->
                mixStep.time().moment() >= mixTime * Moment.MINUTE
                        && (cauldronType == null || cauldronType.appliesTo(mixStep.cauldronType()))
                        && IngredientsUtil.checkMatch(ingredients, mixStep.ingredients())
        );
    }

    @Override
    public ObjectiveService getService() {
        return service;
    }
}
