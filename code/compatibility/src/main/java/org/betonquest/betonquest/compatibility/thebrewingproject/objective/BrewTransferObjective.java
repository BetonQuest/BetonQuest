package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.api.brew.BrewManager;
import dev.jsinco.brewery.api.brew.BrewQuality;
import dev.jsinco.brewery.bukkit.api.event.transaction.BarrelExtractEvent;
import dev.jsinco.brewery.bukkit.api.event.transaction.BarrelInsertEvent;
import dev.jsinco.brewery.bukkit.api.event.transaction.CauldronExtractEvent;
import dev.jsinco.brewery.bukkit.api.event.transaction.CauldronInsertEvent;
import dev.jsinco.brewery.bukkit.api.event.transaction.DistilleryExtractEvent;
import dev.jsinco.brewery.bukkit.api.event.transaction.DistilleryInsertEvent;
import dev.jsinco.brewery.bukkit.api.event.transaction.ItemTransactionEvent;
import dev.jsinco.brewery.bukkit.api.transaction.ItemSource;
import dev.jsinco.brewery.bukkit.api.transaction.ItemTransactionSession;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.compatibility.thebrewingproject.argument.BrewQualityArgument;
import org.betonquest.betonquest.compatibility.thebrewingproject.argument.BrewingStructureType;
import org.betonquest.betonquest.compatibility.thebrewingproject.argument.TransferType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * The objective reached when transferring brews from either distillery, barrels, or cauldrons.
 *
 * @param service               the objective service
 * @param brewManager           the brew manager provided by TheBrewingProject
 * @param qualityArgument       a quality filter
 * @param brewTypeArgument      a brew type argument
 * @param transferTypeArgument  a transfer type argument
 * @param structureTypeArgument a structure type argument
 */
public record BrewTransferObjective(ObjectiveService service, BrewManager<ItemStack> brewManager,
                                    BrewQualityArgument qualityArgument, Argument<String> brewTypeArgument,
                                    Argument<TransferType> transferTypeArgument,
                                    Argument<BrewingStructureType> structureTypeArgument) implements Objective {

    /**
     * Handle item transaction events related to extracting items from inventories.
     *
     * @param event   the event to handle
     * @param profile the online profile of the player
     * @throws QuestException if any argument is invalid
     */
    public void handleExtract(final ItemTransactionEvent<ItemSource.ItemBasedSource> event, final OnlineProfile profile) throws QuestException {
        if (transferTypeArgument.getValue(profile) != TransferType.EXTRACT) {
            return;
        }
        final BrewingStructureType structureType;
        if (event instanceof BarrelExtractEvent) {
            structureType = BrewingStructureType.BARREL;
        } else if (event instanceof DistilleryExtractEvent) {
            structureType = BrewingStructureType.DISTILLERY;
        } else {
            throw new QuestException("Unsupported event type '%s'".formatted(event.getClass().getSimpleName()));
        }
        if (structureTypeArgument.getValue(profile) != structureType) {
            return;
        }
        final ItemTransactionSession<ItemSource.ItemBasedSource> session = event.getTransactionSession();
        completeForItem(session.getResult(), profile);
    }

    /**
     * Handle item transaction events related to inserting items into inventories.
     *
     * @param event   the event to handle
     * @param profile the online profile of the player
     * @throws QuestException if any argument is invalid
     */
    public void handleInsert(final ItemTransactionEvent<ItemSource.BrewBasedSource> event, final OnlineProfile profile) throws QuestException {
        if (transferTypeArgument.getValue(profile) != TransferType.INSERT) {
            return;
        }
        final BrewingStructureType structureType;
        if (event instanceof BarrelInsertEvent) {
            structureType = BrewingStructureType.BARREL;
        } else if (event instanceof DistilleryInsertEvent) {
            structureType = BrewingStructureType.DISTILLERY;
        } else {
            throw new QuestException("Unsupported event type '%s'".formatted(event.getClass().getSimpleName()));
        }
        if (structureTypeArgument.getValue(profile) != structureType) {
            return;
        }
        final ItemTransactionSession<ItemSource.BrewBasedSource> session = event.getTransactionSession();
        completeForItem(session.getResult(), profile);
    }

    /**
     * Handle cauldron extract event.
     *
     * @param event   a cauldron extract event
     * @param profile the online player profile
     * @throws QuestException if any argument is invalid
     */
    public void handleExtract(final CauldronExtractEvent event, final OnlineProfile profile) throws QuestException {
        if (transferTypeArgument.getValue(profile) != TransferType.EXTRACT
                || structureTypeArgument.getValue(profile) != BrewingStructureType.CAULDRON) {
            return;
        }
        completeForItem(event.getItemResult(), profile);
    }

    /**
     * Handle cauldron insert event.
     *
     * @param event   a cauldron extract event
     * @param profile the online player profile
     * @throws QuestException if any argument is invalid
     */
    public void handleInsert(final CauldronInsertEvent event, final OnlineProfile profile) throws QuestException {
        if (transferTypeArgument.getValue(profile) != TransferType.INSERT
                || structureTypeArgument.getValue(profile) != BrewingStructureType.CAULDRON) {
            return;
        }
        completeForItem(event.getItemSource(), profile);
    }

    private void completeForItem(@Nullable final ItemSource itemSource, final OnlineProfile profile) throws QuestException {
        final Predicate<BrewQuality> quality = qualityArgument.resolve(profile);
        final String type = brewTypeArgument.getValue(profile);
        if (itemSource == null) {
            return;
        }
        final ItemStack itemStack = itemSource.get();
        if (brewManager.brewName(itemStack).filter(type::equals).isPresent()
                && brewManager.brewQuality(itemStack).filter(quality).isPresent()
        ) {
            service.complete(profile);
        }
    }

    @Override
    public ObjectiveService getService() {
        return service;
    }
}
