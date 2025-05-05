package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.message.Message;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.feature.QuestCanceler;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.id.JournalEntryID;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.id.QuestCancelerID;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.message.ParsedSectionMessageCreator;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Stores Quest Canceler.
 */
public class CancelerProcessor extends SectionProcessor<QuestCancelerID, QuestCanceler> {

    /**
     * Logger factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Class to get initialized feature API.
     */
    private final BetonQuest plugin;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Variable processor to create new variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Message creator to parse messages.
     */
    private final ParsedSectionMessageCreator messageCreator;

    /**
     * Create a new Quest Canceler Processor to store them.
     *
     * @param log               the custom logger for this class
     * @param loggerFactory     the logger factory to create new class specific logger
     * @param plugin            the class to get initialized feature API.
     * @param pluginMessage     the {@link PluginMessage} instance
     * @param variableProcessor the variable processor to create new variables
     * @param messageCreator    the message creator to parse messages
     */
    public CancelerProcessor(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory, final BetonQuest plugin,
                             final PluginMessage pluginMessage, final VariableProcessor variableProcessor,
                             final ParsedSectionMessageCreator messageCreator) {
        super(log, "Quest Canceler", "cancel");
        this.loggerFactory = loggerFactory;
        this.plugin = plugin;
        this.pluginMessage = pluginMessage;
        this.variableProcessor = variableProcessor;
        this.messageCreator = messageCreator;
    }

    @Override
    protected QuestCanceler loadSection(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final Message names = messageCreator.parseFromSection(pack, section, "name");
        final String itemString = section.getString("item");
        final String rawItem = itemString == null ? pack.getConfig().getString("items.cancel_button") : itemString;
        final ItemID item = rawItem == null ? null : new ItemID(pack, rawItem);
        final String rawLoc = section.getString("location");
        final Variable<Location> location = rawLoc == null ? null : new Variable<>(variableProcessor, pack, rawLoc, Argument.LOCATION);
        final QuestCanceler.CancelData cancelData = new QuestCanceler.CancelData(
                new VariableList<>(variableProcessor, pack, section.getString("conditions", ""), value -> new ConditionID(pack, value)),
                new VariableList<>(variableProcessor, pack, section.getString("events", ""), value -> new EventID(pack, value)),
                new VariableList<>(variableProcessor, pack, section.getString("objectives", ""), value -> new ObjectiveID(pack, value)),
                new VariableList<>(variableProcessor, pack, section.getString("tags", ""), Argument.STRING),
                new VariableList<>(variableProcessor, pack, section.getString("points", ""), Argument.STRING),
                new VariableList<>(variableProcessor, pack, section.getString("journal", ""), value -> new JournalEntryID(pack, value)),
                location);
        final BetonQuestLogger logger = loggerFactory.create(QuestCanceler.class);
        return new QuestCanceler(logger, section.getName(), plugin.getFeatureAPI(), pluginMessage, names, item, pack, cancelData);
    }

    @Override
    protected QuestCancelerID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new QuestCancelerID(pack, identifier);
    }
}
