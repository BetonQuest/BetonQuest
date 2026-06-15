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
 * @param barrelTypeArgument A barrel type argument
 * @param agingYearsArgument An aging time argument
 * @param ageingYear         An aging year as defined by TheBrewingProject
 * @param brewManager        The brew manager provided by TheBrewingProject
 * @param service            The objective service
 */
public record BrewAgeObjective(Argument<BarrelType> barrelTypeArgument,
                               Argument<Number> agingYearsArgument,
                               long ageingYear, BrewManager<ItemStack> brewManager,
                               ObjectiveService service) implements Objective {

    /**
     * Handle brew extract events from barrel.
     *
     * @param event   The brew extract event
     * @param profile The player profile
     * @throws QuestException If any argument was invalid
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
