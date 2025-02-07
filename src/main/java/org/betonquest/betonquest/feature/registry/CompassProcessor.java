package org.betonquest.betonquest.feature.registry;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.feature.QuestCompass;
import org.betonquest.betonquest.id.CompassID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.registry.processor.QuestProcessor;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * Loads and stores {@link QuestCompass}es.
 */
public class CompassProcessor extends QuestProcessor<CompassID, QuestCompass> {

    /**
     * Variable to create new variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create a new QuestProcessor to store {@link QuestCompass}es.
     *
     * @param log               the custom logger for this class
     * @param variableProcessor the variable to create new variables
     */
    public CompassProcessor(final BetonQuestLogger log, final VariableProcessor variableProcessor) {
        super(log);
        this.variableProcessor = variableProcessor;
    }

    @Override
    public void load(final QuestPackage pack) {
        final ConfigurationSection section = pack.getConfig().getConfigurationSection("compass");
        if (section == null) {
            return;
        }
        for (final String key : section.getKeys(false)) {
            try {
                values.put(new CompassID(pack, key), loadCompass(pack, section.getConfigurationSection(key)));
            } catch (final QuestException e) {
                log.warn("Could not load compass '" + key + "' in pack '" + pack.getQuestPath() + "': " + e.getMessage(), e);
            }
        }
    }

    private QuestCompass loadCompass(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final Map<String, String> names = new HashMap<>();
        if (section.isConfigurationSection("name")) {
            final ConfigurationSection nameSection = section.getConfigurationSection("name");
            for (final String key : section.getKeys(false)) {
                names.put(key, nameSection.getString(GlobalVariableResolver.resolve(pack, key)));
            }
        } else {
            names.put(Config.getLanguage(), GlobalVariableResolver.resolve(pack, section.getString("name")));
        }
        if (names.isEmpty()) {
            throw new QuestException("Name not defined");
        }
        final String location = section.getString("location");
        if (location == null) {
            throw new QuestException("Location not defined");
        }
        final VariableLocation loc = new VariableLocation(variableProcessor, pack, GlobalVariableResolver.resolve(pack, location));
        final String itemName = section.getString("item");
        final ItemID itemID = itemName == null ? null : new ItemID(pack, itemName);
        return new QuestCompass(names, loc, itemID);
    }

    /**
     * Get the loaded Quest Canceler.
     *
     * @return quest cancelers in a new map
     */
    public Map<CompassID, QuestCompass> getCompasses() {
        return new HashMap<>(values);
    }
}
