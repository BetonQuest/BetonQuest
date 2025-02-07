package org.betonquest.betonquest.feature.registry.processor;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.feature.QuestCanceler;
import org.betonquest.betonquest.id.QuestCancelerID;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Stores Quest Canceller.
 */
public class CancellerProcessor extends SectionProcessor<QuestCancelerID, QuestCanceler> {
    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Create a new Quest Canceler Processor to store them.
     *
     * @param log           the custom logger for this class
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public CancellerProcessor(final BetonQuestLogger log, final PluginMessage pluginMessage) {
        super(log, "Quest Canceler", "cancel");
        this.pluginMessage = pluginMessage;
    }

    @Override
    protected QuestCanceler loadSection(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        return new QuestCanceler(pluginMessage, pack, section.getName());
    }

    @Override
    protected QuestCancelerID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new QuestCancelerID(pack, identifier);
    }
}
