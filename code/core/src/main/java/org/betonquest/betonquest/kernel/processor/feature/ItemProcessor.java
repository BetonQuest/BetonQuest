package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.instruction.InstructionApi;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.item.QuestItemWrapper;
import org.betonquest.betonquest.kernel.processor.TypedQuestProcessor;
import org.betonquest.betonquest.kernel.registry.feature.ItemTypeRegistry;

/**
 * Stores QuestItems and generates new.
 */
public class ItemProcessor extends TypedQuestProcessor<ItemIdentifier, QuestItemWrapper> {

    /**
     * Create a new ItemProcessor to store and get {@link QuestItem}s.
     *
     * @param log                   the custom logger for this class
     * @param placeholders          the {@link Placeholders} to create and resolve placeholders
     * @param itemIdentifierFactory the factory to create item identifiers
     * @param packManager           the quest package manager to get quest packages from
     * @param types                 the available types
     * @param instructionApi        the instruction api
     */
    public ItemProcessor(final BetonQuestLogger log, final Placeholders placeholders,
                         final IdentifierFactory<ItemIdentifier> itemIdentifierFactory,
                         final QuestPackageManager packManager, final ItemTypeRegistry types,
                         final InstructionApi instructionApi) {
        super(log, placeholders, packManager, types, itemIdentifierFactory, instructionApi, "Item", "items");
    }
}
