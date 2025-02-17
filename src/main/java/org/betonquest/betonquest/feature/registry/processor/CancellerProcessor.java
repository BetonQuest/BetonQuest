package org.betonquest.betonquest.feature.registry.processor;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.feature.QuestCanceler;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.id.QuestCancelerID;
import org.betonquest.betonquest.instruction.argument.IDArgument;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Map;

/**
 * Stores Quest Canceller.
 */
public class CancellerProcessor extends SectionProcessor<QuestCancelerID, QuestCanceler> {

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
     * Create a new Quest Canceler Processor to store them.
     *
     * @param log               the custom logger for this class
     * @param loggerFactory     the logger factory to create new class specific logger
     * @param pluginMessage     the {@link PluginMessage} instance
     * @param variableProcessor the variable processor to create new variables
     */
    public CancellerProcessor(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory, final PluginMessage pluginMessage, final VariableProcessor variableProcessor) {
        super(log, "Quest Canceler", "cancel");
        this.loggerFactory = loggerFactory;
        this.pluginMessage = pluginMessage;
        this.variableProcessor = variableProcessor;
    }

    @Override
    protected QuestCanceler loadSection(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final Map<String, String> names = parseWithLanguage(pack, section, "name");
        final String itemString = section.getString("item");
        final String rawItem = itemString == null ? pack.getConfig().getString("items.cancel_button") : itemString;
        final ItemID item = rawItem == null ? null : new ItemID(pack, rawItem);
        // parse it to get the data
        final EventID[] events = parseID(pack, section, "events", EventID::new);
        final ConditionID[] conditions = parseID(pack, section, "conditions", ConditionID::new);
        final ObjectiveID[] objectives = parseID(pack, section, "objectives", ObjectiveID::new);
        final String[] tags = split(pack, section, "tags");
        final String[] points = split(pack, section, "points");
        final String[] journal = split(pack, section, "journal");
        final String rawLoc = GlobalVariableResolver.resolve(pack, section.getString("loc"));
        final VariableLocation location = rawLoc == null ? null : new VariableLocation(variableProcessor, pack, rawLoc);
        final QuestCanceler.CancelData cancelData = new QuestCanceler.CancelData(conditions, location, events, objectives, tags, points, journal);
        final BetonQuestLogger logger = loggerFactory.create(getClass());
        return new QuestCanceler(logger, section.getName(), pluginMessage, names, item, pack, cancelData);
    }

    @Nullable
    private String[] split(final QuestPackage pack, final ConfigurationSection section, final String path) {
        final String raw = section.getString(path);
        return raw == null ? null : GlobalVariableResolver.resolve(pack, raw).split(",");
    }

    @SuppressWarnings("PMD.ReturnEmptyCollectionRatherThanNull")
    @Nullable
    private <T extends ID> T[] parseID(final QuestPackage pack, final ConfigurationSection section, final String path, final IDArgument<T> argument) throws QuestException {
        final String[] rawObjectives = split(pack, section, path);
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

    @Override
    protected QuestCancelerID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new QuestCancelerID(pack, identifier);
    }
}
