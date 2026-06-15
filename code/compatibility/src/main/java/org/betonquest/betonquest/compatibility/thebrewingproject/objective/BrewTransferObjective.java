package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.api.brew.BrewQuality;
import dev.jsinco.brewery.bukkit.api.event.transaction.CauldronExtractEvent;
import dev.jsinco.brewery.bukkit.api.event.transaction.CauldronInsertEvent;
import dev.jsinco.brewery.bukkit.api.event.transaction.ItemTransactionEvent;
import dev.jsinco.brewery.bukkit.api.transaction.ItemSource;
import dev.jsinco.brewery.bukkit.api.transaction.ItemTransactionSession;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.compatibility.thebrewingproject.BrewUtil;
import org.betonquest.betonquest.compatibility.thebrewingproject.argument.BrewQualityArgument;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * The objective reached when transferring brews from either distillery, barrels, or cauldrons.
 *
 * @param qualityArgument      A quality filter
 * @param brewTypeArgument     A brew type argument
 * @param transferTypeArgument A transfer type argument
 * @param service              The objective service
 */
public record BrewTransferObjective(BrewQualityArgument qualityArgument, Argument<String> brewTypeArgument,
                                    Argument<TransferType> transferTypeArgument,
                                    ObjectiveService service) implements Objective {

    /**
     * Handle item transaction events related to extracting items from inventories.
     *
     * @param event   The event to handle
     * @param profile The online profile of the player
     * @throws QuestException If any argument is invalid
     */
    public void handleExtract(final ItemTransactionEvent<ItemSource.ItemBasedSource> event, final OnlineProfile profile) throws QuestException {
        if (transferTypeArgument.getValue(profile) != TransferType.EXTRACT) {
            return;
        }
        final ItemTransactionSession<ItemSource.ItemBasedSource> session = event.getTransactionSession();
        completeForItem(session.getResult(), profile);
    }

    /**
     * Handle item transaction events related to inserting items into inventories.
     *
     * @param event   The event to handle
     * @param profile The online profile of the player
     * @throws QuestException If any argument is invalid
     */
    public void handleInsert(final ItemTransactionEvent<ItemSource.BrewBasedSource> event, final OnlineProfile profile) throws QuestException {
        if (transferTypeArgument.getValue(profile) != TransferType.INSERT) {
            return;
        }
        final ItemTransactionSession<ItemSource.BrewBasedSource> session = event.getTransactionSession();
        completeForItem(session.getResult(), profile);
    }

    /**
     * Handle cauldron extract event.
     *
     * @param event   A cauldron extract event
     * @param profile The online player profile
     * @throws QuestException If any argument is invalid
     */
    public void handleExtract(final CauldronExtractEvent event, final OnlineProfile profile) throws QuestException {
        if (transferTypeArgument.getValue(profile) != TransferType.EXTRACT) {
            return;
        }
        completeForItem(event.getItemResult(), profile);
    }

    /**
     * Handle cauldron insert event.
     *
     * @param event   A cauldron extract event
     * @param profile The online player profile
     * @throws QuestException If any argument is invalid
     */
    public void handleInsert(final CauldronInsertEvent event, final OnlineProfile profile) throws QuestException {
        if (transferTypeArgument.getValue(profile) != TransferType.INSERT) {
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
        final ItemMeta itemMeta = itemStack.getItemMeta();
        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (BrewUtil.brewName(container).filter(type::equals).isPresent()
                && BrewUtil.quality(container).filter(quality).isPresent()
        ) {
            service.complete(profile);
        }
    }

    @Override
    public ObjectiveService getService() {
        return service;
    }
}
