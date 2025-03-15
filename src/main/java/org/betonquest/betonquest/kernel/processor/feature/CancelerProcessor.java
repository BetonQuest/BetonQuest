package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.feature.QuestCanceler;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.id.JournalEntryID;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.id.QuestCancelerID;
import org.betonquest.betonquest.instruction.argument.IDArgument;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.message.ParsedSectionMessage;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;

/**
 * Stores Quest Canceler.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class CancelerProcessor extends SectionProcessor<QuestCancelerID, QuestCanceler> {

    /**
     * Logger factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Variable processor to create new variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Message parser to parse messages.
     */
    private final MessageParser messageParser;

    /**
     * Player data storage to get the player language.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * Create a new Quest Canceler Processor to store them.
     *
     * @param log               the custom logger for this class
     * @param loggerFactory     the logger factory to create new class specific logger
     * @param pluginMessage     the {@link PluginMessage} instance
     * @param variableProcessor the variable processor to create new variables
     * @param messageParser     the message parser to parse messages
     * @param playerDataStorage the player data storage to get the player language
     */
    public CancelerProcessor(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                             final PluginMessage pluginMessage, final VariableProcessor variableProcessor,
                             final MessageParser messageParser, final PlayerDataStorage playerDataStorage) {
        super(log, "Quest Canceler", "cancel");
        this.loggerFactory = loggerFactory;
        this.pluginMessage = pluginMessage;
        this.variableProcessor = variableProcessor;
        this.messageParser = messageParser;
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    protected QuestCanceler loadSection(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final ParsedSectionMessage names = new ParsedSectionMessage(variableProcessor, messageParser, playerDataStorage, pack, section, "name");
        final String itemString = section.getString("item");
        final String rawItem = itemString == null ? pack.getConfig().getString("items.cancel_button") : itemString;
        final ItemID item = rawItem == null ? null : new ItemID(pack, rawItem);
        final CreationHelper helper = new CreationHelper(pack, section);
        final EventID[] events = helper.parseID("events", EventID::new);
        final ConditionID[] conditions = helper.parseID("conditions", ConditionID::new);
        final ObjectiveID[] objectives = helper.parseID("objectives", ObjectiveID::new);
        final String[] tags = helper.split("tags");
        final String[] points = helper.split("points");
        final JournalEntryID[] journal = helper.parseID("journal", JournalEntryID::new);
        final String rawLoc = GlobalVariableResolver.resolve(pack, section.getString("location"));
        final VariableLocation location = rawLoc == null ? null : new VariableLocation(variableProcessor, pack, rawLoc);
        final QuestCanceler.CancelData cancelData = new QuestCanceler.CancelData(conditions, location, events, objectives, tags, points, journal);
        final BetonQuestLogger logger = loggerFactory.create(QuestCanceler.class);
        return new QuestCanceler(logger, section.getName(), pluginMessage, names, item, pack, cancelData);
    }

    @Override
    protected QuestCancelerID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new QuestCancelerID(pack, identifier);
    }

    /**
     * Class to bundle objects required to create a QuestCanceler.
     *
     * @param pack    The canceler pack.
     * @param section The canceler specific section.
     */
    private record CreationHelper(QuestPackage pack, ConfigurationSection section) {

        @Nullable
        private String[] split(final String path) {
            final String raw = section.getString(path);
            return raw == null ? null : GlobalVariableResolver.resolve(pack, raw).split(",");
        }

        @SuppressWarnings("PMD.ReturnEmptyCollectionRatherThanNull")
        @Nullable
        private <T extends ID> T[] parseID(final String path, final IDArgument<T> argument) throws QuestException {
            final String[] rawObjectives = split(path);
            if (rawObjectives == null || rawObjectives.length == 0) {
                return null;
            }
            final T first = argument.convert(pack, rawObjectives[0]);
            @SuppressWarnings("unchecked") final T[] converted = (T[]) Array.newInstance(first.getClass(), rawObjectives.length);
            converted[0] = first;
            for (int i = 1; i < rawObjectives.length; i++) {
                converted[i] = argument.convert(pack, rawObjectives[i]);
            }
            return converted;
        }
    }
}
