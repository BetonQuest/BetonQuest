package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
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
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
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
     * Text creator to parse text.
     */
    private final ParsedSectionTextCreator textCreator;

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Player Data Storage.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * Create a new Quest Canceler Processor to store them.
     *
     * @param log               the custom logger for this class
     * @param loggerFactory     the logger factory to create new class specific logger
     * @param plugin            the class to get initialized feature API.
     * @param pluginMessage     the {@link PluginMessage} instance
     * @param variableProcessor the variable processor to create new variables
     * @param textCreator       the text creator to parse text
     * @param questTypeApi      the Quest Type API
     * @param playerDataStorage the storage for player data
     */
    public CancelerProcessor(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory, final BetonQuest plugin,
                             final PluginMessage pluginMessage, final VariableProcessor variableProcessor,
                             final ParsedSectionTextCreator textCreator, final QuestTypeApi questTypeApi,
                             final PlayerDataStorage playerDataStorage) {
        super(log, "Quest Canceler", "cancel");
        this.loggerFactory = loggerFactory;
        this.plugin = plugin;
        this.pluginMessage = pluginMessage;
        this.variableProcessor = variableProcessor;
        this.textCreator = textCreator;
        this.questTypeApi = questTypeApi;
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    protected QuestCanceler loadSection(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final Text names = textCreator.parseFromSection(pack, section, "name");
        final String itemString = section.getString("item");
        final String rawItem = itemString == null ? pack.getConfig().getString("item.cancel_button") : itemString;
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
        return new QuestCanceler(logger, questTypeApi, playerDataStorage, section.getName(), plugin.getFeatureApi(), pluginMessage, names, item, pack, cancelData);
    }

    @Override
    protected QuestCancelerID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new QuestCancelerID(pack, identifier);
    }
}
