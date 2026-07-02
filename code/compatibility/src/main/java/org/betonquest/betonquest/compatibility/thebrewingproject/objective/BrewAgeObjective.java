package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.api.brew.Brew;
import dev.jsinco.brewery.api.brew.BrewManager;
import dev.jsinco.brewery.api.brew.BrewingStep;
import dev.jsinco.brewery.api.breweries.BarrelType;
import dev.jsinco.brewery.bukkit.api.event.transaction.ItemTransactionEvent;
import dev.jsinco.brewery.bukkit.api.transaction.ItemSource;
import dev.jsinco.brewery.bukkit.api.transaction.ItemTransactionSession;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * The brew age objective.
 *
 * @param ageingYear         an aging year as defined by TheBrewingProject
 * @param brewManager        the brew manager provided by TheBrewingProject
 * @param service            the objective service
 * @param barrelTypeArgument a barrel type argument
 * @param agingYearsArgument an aging time argument
 */
public record BrewAgeObjective(long ageingYear, BrewManager<ItemStack> brewManager,
                               ObjectiveService service,
                               Argument<BarrelType> barrelTypeArgument,
                               Argument<Number> agingYearsArgument) implements Objective {

    /**
     * Handle brew extract events from barrel.
     *
     * @param event   the brew extract event
     * @param profile the player profile
     * @throws QuestException if any argument was invalid
     */
    public void handle(final ItemTransactionEvent<ItemSource.ItemBasedSource> event, final OnlineProfile profile) throws QuestException {
        final ItemTransactionSession<ItemSource.ItemBasedSource> session = event.getTransactionSession();
        final ItemSource.ItemBasedSource itemSource = session.getResult();
        if (itemSource == null) {
            return;
        }
        final BarrelType barrelType = barrelTypeArgument.getValue(profile);
        final double ageTime = agingYearsArgument.getValue(profile).doubleValue();
        final Optional<Brew> brewOptional = brewManager.fromItem(itemSource.get());
        if (brewOptional.isPresent()
                && brewOptional.get().lastCompletedStep() instanceof final BrewingStep.Age age
                && barrelType.proximityScore(age.barrelType()) == 1
                && ageTime * ageingYear <= age.time().moment()) {
            service.complete(profile);
        }
    }

    @Override
    public ObjectiveService getService() {
        return service;
    }
}
