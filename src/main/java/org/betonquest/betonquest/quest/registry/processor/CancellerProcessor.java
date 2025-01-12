package org.betonquest.betonquest.quest.registry.processor;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.config.QuestCanceler;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.QuestCancelerID;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores Quest Canceller.
 */
public class CancellerProcessor extends QuestProcessor<QuestCancelerID, QuestCanceler> {
    /**
     * Create a new Quest Canceler Processor to store them.
     *
     * @param log the custom logger for this class
     */
    public CancellerProcessor(final BetonQuestLogger log) {
        super(log);
    }

    @Override
    public void load(final QuestPackage pack) {
        final ConfigurationSection cancelSection = pack.getConfig().getConfigurationSection("cancel");
        if (cancelSection != null) {
            for (final String key : cancelSection.getKeys(false)) {
                try {
                    values.put(new QuestCancelerID(pack, key), new QuestCanceler(pack, key));
                } catch (final QuestException | ObjectNotFoundException e) {
                    log.warn(pack, "Could not load '" + pack.getQuestPath() + "." + key + "' quest canceler: " + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Get the loaded Quest Canceler.
     *
     * @return quest cancelers in a new map
     */
    public Map<QuestCancelerID, QuestCanceler> getCancelers() {
        return new HashMap<>(values);
    }
}
