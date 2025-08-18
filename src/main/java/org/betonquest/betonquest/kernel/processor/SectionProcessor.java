package org.betonquest.betonquest.kernel.processor;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

/**
 * Does the load logic around {@link T} from a configuration section.
 *
 * @param <I> the {@link Identifier} identifying the type
 * @param <T> the type
 */
public abstract class SectionProcessor<I extends Identifier, T> extends QuestProcessor<I, T> {

    /**
     * Create a new QuestProcessor to store and execute type logic.
     *
     * @param log         the custom logger for this class
     * @param packManager the quest package manager to get quest packages from
     * @param readable    the type name used for logging, with the first letter in upper case
     * @param internal    the section name and/or bstats topic identifier
     */
    public SectionProcessor(final BetonQuestLogger log, final QuestPackageManager packManager, final String readable,
                            final String internal) {
        super(log, packManager, readable, internal);
    }

    @Override
    public void load(final QuestPackage pack) {
        final ConfigurationSection section = pack.getConfig().getConfigurationSection(internal);
        if (section == null) {
            return;
        }
        for (final String key : section.getKeys(false)) {
            try {
                final ConfigurationSection featureSection = section.getConfigurationSection(key);
                final I identifier = getIdentifier(pack, key);
                if (featureSection == null) {
                    log.warn(pack, "No configuration section for '" + identifier + "' " + readable + "!");
                    continue;
                }
                values.put(identifier, loadSection(pack, featureSection));
            } catch (final QuestException e) {
                log.warn("Could not load " + readable + " '" + key + "' in pack '" + pack.getQuestPath() + "': " + e.getMessage(), e);
            }
        }
    }

    /**
     * Load all {@link T} from the QuestPackage.
     * <p>
     * Any errors will be logged.
     *
     * @param pack    to load the {@link T} from
     * @param section the section to load
     * @return the loaded {@link T}
     * @throws QuestException if the loading fails
     */
    protected abstract T loadSection(QuestPackage pack, ConfigurationSection section) throws QuestException;

    /**
     * Get the loaded {@link T} by their ID.
     *
     * @return loaded values map, reflecting changes
     */
    public Map<I, T> getValues() {
        return values;
    }
}
