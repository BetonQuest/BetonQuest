package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.instruction.InstructionApi;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.item.QuestItemWrapper;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.service.ItemManager;
import org.betonquest.betonquest.kernel.processor.TypedQuestProcessor;
import org.betonquest.betonquest.kernel.registry.feature.ItemTypeRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * Stores QuestItems and generates new.
 */
public class ItemProcessor extends TypedQuestProcessor<ItemIdentifier, QuestItemWrapper> implements ItemManager {

    /**
     * Create a new ItemProcessor to store and get {@link QuestItem}s.
     *
     * @param log                   the custom logger for this class
     * @param itemIdentifierFactory the factory to create item identifiers
     * @param types                 the available types
     * @param instructionApi        the instruction api
     */
    public ItemProcessor(final BetonQuestLogger log,
                         final IdentifierFactory<ItemIdentifier> itemIdentifierFactory,
                         final ItemTypeRegistry types,
                         final InstructionApi instructionApi) {
        super(log, types, itemIdentifierFactory, instructionApi, "Item", "items");
    }

    @Override
    public QuestItem getItem(@Nullable final Profile profile, final ItemIdentifier itemIdentifier) throws QuestException {
        return get(itemIdentifier).getItem(profile);
    }
}
