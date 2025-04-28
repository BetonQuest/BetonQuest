package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.message.Message;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.feature.QuestCompass;
import org.betonquest.betonquest.id.CompassID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.argument.types.location.LocationParser;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.message.ParsedSectionMessageCreator;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Loads and stores {@link QuestCompass}es.
 */
public class CompassProcessor extends SectionProcessor<CompassID, QuestCompass> {

    /**
     * Variable processor to create new variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Message creator to parse messages.
     */
    private final ParsedSectionMessageCreator messageCreator;

    /**
     * Create a new QuestProcessor to store {@link QuestCompass}es.
     *
     * @param log               the custom logger for this class
     * @param variableProcessor the variable processor to create new variables
     * @param messageCreator    the message creator to parse messages
     */
    public CompassProcessor(final BetonQuestLogger log, final VariableProcessor variableProcessor,
                            final ParsedSectionMessageCreator messageCreator) {
        super(log, "Compass", "compass");
        this.variableProcessor = variableProcessor;
        this.messageCreator = messageCreator;
    }

    @Override
    protected QuestCompass loadSection(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final Message names = messageCreator.parseFromSection(pack, section, "name");
        final String location = section.getString("location");
        if (location == null) {
            throw new QuestException("Location not defined");
        }
        final String rawLoc = GlobalVariableResolver.resolve(pack, location);
        final Variable<Location> loc = new Variable<>(variableProcessor, pack, rawLoc, LocationParser.LOCATION);
        final String itemName = section.getString("item");
        final ItemID itemID = itemName == null ? null : new ItemID(pack, itemName);
        return new QuestCompass(names, loc, itemID);
    }

    @Override
    protected CompassID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new CompassID(pack, identifier);
    }
}
