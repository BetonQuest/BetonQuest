package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.item.QuestItemWrapper;
import org.betonquest.betonquest.kernel.processor.TypedQuestProcessor;
import org.betonquest.betonquest.kernel.registry.feature.ItemTypeRegistry;

/**
 * Stores QuestItems and generates new.
 */
public class ItemProcessor extends TypedQuestProcessor<ItemID, QuestItemWrapper> {

    /**
     * Create a new ItemProcessor to store and get {@link QuestItem}s.
     *
     * @param log   the custom logger for this class
     * @param types the available types
     */
    public ItemProcessor(final BetonQuestLogger log, final ItemTypeRegistry types) {
        super(log, types, "Item", "items");
    }

    @Override
    protected ItemID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new ItemID(pack, identifier);
    }
}
