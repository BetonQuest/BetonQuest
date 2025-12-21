package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.argument.parser.LocationParser;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.feature.QuestCompass;
import org.betonquest.betonquest.id.CompassID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Loads and stores {@link QuestCompass}es.
 */
public class CompassProcessor extends SectionProcessor<CompassID, QuestCompass> {

    /**
     * Text creator to parse text.
     */
    private final ParsedSectionTextCreator textCreator;

    /**
     * Create a new QuestProcessor to store {@link QuestCompass}es.
     *
     * @param log         the custom logger for this class
     * @param packManager the quest package manager to get quest packages from
     * @param variables   the variable processor to create and resolve variables
     * @param textCreator the text creator to parse text
     */
    public CompassProcessor(final BetonQuestLogger log, final Variables variables, final QuestPackageManager packManager,
                            final ParsedSectionTextCreator textCreator) {
        super(log, variables, packManager, "Compass", "compass");
        this.textCreator = textCreator;
    }

    @Override
    protected QuestCompass loadSection(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final Text names = textCreator.parseFromSection(pack, section, "name");
        final String location = section.getString("location");
        if (location == null) {
            throw new QuestException("Location not defined");
        }
        final Variable<Location> loc = new Variable<>(variables, pack, location, new LocationParser(Bukkit.getServer()));
        final String itemName = section.getString("item");
        final ItemID itemID = itemName == null ? null : new ItemID(variables, packManager, pack, itemName);
        return new QuestCompass(names, loc, itemID);
    }

    @Override
    protected CompassID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new CompassID(packManager, pack, identifier);
    }
}
