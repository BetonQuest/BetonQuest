package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.api.brew.Brew;
import dev.jsinco.brewery.api.brew.BrewManager;
import dev.jsinco.brewery.bukkit.api.event.transaction.CauldronExtractEvent;
import dev.jsinco.brewery.bukkit.api.event.transaction.ItemTransactionEvent;
import dev.jsinco.brewery.bukkit.api.transaction.ItemSource;
import dev.jsinco.brewery.bukkit.api.transaction.ItemTransactionSession;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A brew create objective.
 *
 * @param brewPredicateArgument A brew predicate argument
 * @param service               The objective service
 * @param brewManager           The brew manager/adapter
 */
public record BrewCreateObjective(Argument<QuestFunction<Brew, Boolean>> brewPredicateArgument,
                                  ObjectiveService service, BrewManager<ItemStack> brewManager) implements Objective {

    @Override
    public ObjectiveService getService() {
        return service;
    }

    /**
     * Handle brew extract events from distillery or barrel.
     *
     * @param event   The brew extract event
     * @param profile The player profile
     * @throws QuestException If any argument was invalid
     */
    public void handle(final ItemTransactionEvent<ItemSource.ItemBasedSource> event, final OnlineProfile profile) throws QuestException {
        final ItemTransactionSession<ItemSource.ItemBasedSource> session = event.getTransactionSession();
        completeForItem(session.getResult(), profile);
    }

    /**
     * Handle brew extract events from cauldrons.
     *
     * @param event   The brew extract event
     * @param profile The player profile
     * @throws QuestException If any argument was invalid
     */
    public void handle(final CauldronExtractEvent event, final OnlineProfile profile) throws QuestException {
        completeForItem(event.getItemResult(), profile);
    }

    private void completeForItem(@Nullable final ItemSource itemSource, final OnlineProfile profile) throws QuestException {
        if (itemSource == null) {
            return;
        }
        final Optional<Brew> brewOptional = brewManager.fromItem(itemSource.get());
        if (brewOptional.isPresent() && brewPredicateArgument.getValue(profile).apply(brewOptional.get())) {
            service.complete(profile);
        }
    }
}
