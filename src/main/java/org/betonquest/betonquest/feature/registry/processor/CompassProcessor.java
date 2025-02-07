package org.betonquest.betonquest.feature.registry.processor;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.feature.QuestCompass;
import org.betonquest.betonquest.id.CompassID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

/**
 * Loads and stores {@link QuestCompass}es.
 */
public class CompassProcessor extends SectionProcessor<CompassID, QuestCompass> {

    /**
     * Variable processor to create new variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create a new QuestProcessor to store {@link QuestCompass}es.
     *
     * @param log               the custom logger for this class
     * @param variableProcessor the variable processor to create new variables
     */
    public CompassProcessor(final BetonQuestLogger log, final VariableProcessor variableProcessor) {
        super(log, "Compass", "compass");
        this.variableProcessor = variableProcessor;
    }

    @Override
    protected QuestCompass loadSection(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final Map<String, String> names = parseWithLanguage(pack, section, "name");
        final String location = section.getString("location");
        if (location == null) {
            throw new QuestException("Location not defined");
        }
        final VariableLocation loc = new VariableLocation(variableProcessor, pack, GlobalVariableResolver.resolve(pack, location));
        final String itemName = section.getString("item");
        final ItemID itemID = itemName == null ? null : new ItemID(pack, itemName);
        return new QuestCompass(names, loc, itemID);
    }

    @Override
    protected CompassID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new CompassID(pack, identifier);
    }
}
