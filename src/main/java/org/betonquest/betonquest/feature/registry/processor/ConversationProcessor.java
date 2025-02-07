package org.betonquest.betonquest.feature.registry.processor;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.conversation.ConversationData;
import org.betonquest.betonquest.id.ConversationID;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Stores Conversation Data and validates it.
 */
public class ConversationProcessor extends SectionProcessor<ConversationID, ConversationData> {
    /**
     * Plugin instance used for new Conversation Data.
     */
    private final BetonQuest plugin;

    /**
     * Create a new Conversation Data Processor to load and process conversation data.
     *
     * @param log    the custom logger for this class
     * @param plugin the plugin instance used for new conversation data
     */
    public ConversationProcessor(final BetonQuestLogger log, final BetonQuest plugin) {
        super(log, "Conversation", "conversations");
        this.plugin = plugin;
    }

    @Override
    protected ConversationData loadSection(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        return new ConversationData(plugin, new ConversationID(pack, section.getName()), section);
    }

    @Override
    protected ConversationID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new ConversationID(pack, identifier);
    }

    /**
     * Validates all pointers to conversations and removes them when the target conversation is not loaded.
     * <p>
     * This method should be invoked after loading QuestPackages.
     *
     * @see ConversationData#checkExternalPointers()
     */
    public void checkExternalPointers() {
        values.entrySet().removeIf(entry -> {
            final ConversationData convData = entry.getValue();
            try {
                convData.checkExternalPointers();
            } catch (final QuestException e) {
                log.warn(convData.getPack(), "Error in '" + convData.getPack().getQuestPath() + "."
                        + convData.getName() + "' conversation: " + e.getMessage(), e);
                return true;
            }
            return false;
        });
    }
}
