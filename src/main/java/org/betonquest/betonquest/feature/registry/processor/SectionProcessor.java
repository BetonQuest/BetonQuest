package org.betonquest.betonquest.feature.registry.processor;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.quest.registry.processor.QuestProcessor;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * Does the load logic around {@link T} from a configuration section.
 *
 * @param <I> the {@link ID} identifying the type
 * @param <T> the type
 */
public abstract class SectionProcessor<I extends ID, T> extends QuestProcessor<I, T> {

    /**
     * Create a new QuestProcessor to store and execute type logic.
     *
     * @param log      the custom logger for this class
     * @param readable the type name used for logging, with the first letter in upper case
     * @param internal the section name and/or bstats topic identifier
     */
    public SectionProcessor(final BetonQuestLogger log, final String readable, final String internal) {
        super(log, readable, internal);
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
                    log.warn(pack, "No configuration section for '" + identifier.getFullID() + "' " + readable + "!");
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
     * Loads value(s) from a key in a section, potentially identified by a language key.
     * When there is no section the value will be identified by the default language.
     *
     * @param pack    the pack to resolve variables
     * @param section the section to load from
     * @param path    where the value(s) are stored
     * @return the values identified by the language key
     * @throws QuestException if there is no value
     */
    protected Map<String, String> parseWithLanguage(final QuestPackage pack, final ConfigurationSection section, final String path)
            throws QuestException {
        final Map<String, String> map = new HashMap<>();
        if (section.isConfigurationSection(path)) {
            final ConfigurationSection subSection = section.getConfigurationSection(path);
            if (subSection == null) {
                throw new QuestException("No configuration section for '" + path + "'!");
            }
            for (final String key : subSection.getKeys(false)) {
                map.put(key, GlobalVariableResolver.resolve(pack, subSection.getString(key)));
            }
        } else if (section.isString(path)) {
            map.put(Config.getLanguage(), GlobalVariableResolver.resolve(pack, section.getString(path)));
        } else {
            throw new QuestException("The '" + path + "' is missing!");
        }
        if (map.isEmpty()) {
            throw new QuestException("No values defined for '" + path + "'!");
        }
        return map;
    }

    /**
     * Get the loaded {@link T} by their ID.
     *
     * @return loaded values map, reflecting changes
     */
    public Map<I, T> getValues() {
        return values;
    }
}
