package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.api.brew.Brew;
import dev.jsinco.brewery.api.brew.BrewManager;
import dev.jsinco.brewery.api.brew.BrewingStep;
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
 * A brew  distill objective.
 *
 * @param service             the objective service
 * @param brewManager         the brew manager provided by TheBrewingProject
 * @param distillRunsArgument a distill runs argument
 */
public record BrewDistillObjective(ObjectiveService service, BrewManager<ItemStack> brewManager,
                                   Argument<Number> distillRunsArgument) implements Objective {

    /**
     * Handle brew extract events from distillery.
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
        final int distillRuns = distillRunsArgument.getValue(profile).intValue();
        final Optional<Brew> brewOptional = brewManager.fromItem(itemSource.get());
        if (brewOptional.isPresent()
                && brewOptional.get().lastCompletedStep() instanceof final BrewingStep.Distill distill
                && distill.runs() == distillRuns
        ) {
            service.complete(profile);
        }
    }

    @Override
    public ObjectiveService getService() {
        return service;
    }
}
