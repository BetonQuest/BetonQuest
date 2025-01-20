package org.betonquest.betonquest.quest.registry.processor;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.conversation.ConversationData;
import org.betonquest.betonquest.exception.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConversationID;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

/**
 * Stores Conversation Data and validates it.
 */
public class ConversationProcessor extends QuestProcessor<ConversationID, ConversationData> {
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
        super(log);
        this.plugin = plugin;
    }

    @Override
    public void load(final QuestPackage pack) {
        final ConfigurationSection conversationsConfig = pack.getConfig().getConfigurationSection("conversations");
        if (conversationsConfig != null) {
            final String packName = pack.getQuestPath();
            for (final String convName : conversationsConfig.getKeys(false)) {
                try {
                    final ConfigurationSection convSection = conversationsConfig.getConfigurationSection(convName);
                    if (convSection == null) {
                        log.warn(pack, "No configuration section for '" + packName + "." + convName + "' conversation!");
                        continue;
                    }
                    final ConversationID convID = new ConversationID(pack, convName);
                    values.put(convID, new ConversationData(plugin, convID, convSection));
                } catch (final QuestException | ObjectNotFoundException e) {
                    log.warn(pack, "Error in '" + packName + "." + convName + "' conversation: " + e.getMessage(), e);
                }
            }
        }
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
            } catch (final ObjectNotFoundException e) {
                log.warn(convData.getPack(), "Error in '" + convData.getPack().getQuestPath() + "."
                        + convData.getName() + "' conversation: " + e.getMessage(), e);
                return true;
            }
            return false;
        });
    }

    /**
     * Gets stored Conversation Data.
     * <p>
     * The conversation data can be null if there was an error loading it.
     *
     * @param conversationID package name, dot and name of the conversation
     * @return ConversationData object for this conversation or null if it does not exist
     */
    @Nullable
    public ConversationData getConversation(final ConversationID conversationID) {
        return values.get(conversationID);
    }
}
