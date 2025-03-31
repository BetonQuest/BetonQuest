package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.item.QuestItemFactory;
import org.betonquest.betonquest.kernel.processor.TypedQuestProcessor;
import org.betonquest.betonquest.kernel.registry.TypeFactory;
import org.betonquest.betonquest.kernel.registry.feature.ItemTypeRegistry;

import java.util.List;

/**
 * Stores QuestItems and generates new.
 */
public class ItemProcessor extends TypedQuestProcessor<ItemID, QuestItem> {

    /**
     * Instruction requires a QuestPackage.
     */
    private final QuestPackage dummy = new QuestDummy();

    /**
     * Create a new ItemProcessor to store and get {@link QuestItem}s.
     *
     * @param log   the custom logger for this class
     * @param types the available types
     */
    public ItemProcessor(final BetonQuestLogger log, final ItemTypeRegistry types) {
        super(log, types, "Quest Item", "items");
        types.setDefaultItemFactory(new QuestItemFactory(true));
        types.register("simple", new QuestItemFactory());
    }

    @Override
    protected ItemID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new ItemID(pack, identifier);
    }

    /**
     * Generates a QuestItem just from instruction string.
     *
     * @param instruction the instruction string to parse
     * @return the new parsed QuestItem
     * @throws QuestException if the instruction cannot be parsed or Item could not be generated
     */
    public QuestItem generate(final String instruction) throws QuestException {
        final Instruction parsed = new Instruction(dummy, null, instruction);
        final String type = parsed.getPart(0);
        final TypeFactory<QuestItem> factory = types.getFactory(type);
        if (factory == null) {
            throw new QuestException("Unknown item type: " + type);
        }
        return factory.parseInstruction(parsed);
    }

    /**
     * An "empty" QuestPackage used for string generated QuestItems.
     */
    private static final class QuestDummy implements QuestPackage {
        /**
         * The empty constructor.
         */
        public QuestDummy() {
        }

        @Override
        public String getQuestPath() {
            throw new UnsupportedOperationException();
        }

        @Override
        public MultiConfiguration getConfig() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<String> getTemplates() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasTemplate(final String templatePath) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean saveAll() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ConfigAccessor getOrCreateConfigAccessor(final String relativePath) {
            throw new UnsupportedOperationException();
        }
    }
}
