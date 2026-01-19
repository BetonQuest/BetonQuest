package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.feature.QuestCanceler;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.id.JournalEntryID;
import org.betonquest.betonquest.id.QuestCancelerID;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
     * The BetonQuest API instance.
     */
    private final BetonQuestApi api;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

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
     * @param api               the BetonQuest API instance
     * @param pluginMessage     the {@link PluginMessage} instance
     * @param parsers           the argument parsers to use
     * @param placeholders      the {@link Placeholders} to create and resolve placeholders
     * @param textCreator       the text creator to parse text
     * @param questTypeApi      the Quest Type API
     * @param playerDataStorage the storage for player data
     */
    public CancelerProcessor(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                             final BetonQuestApi api, final PluginMessage pluginMessage, final ArgumentParsers parsers,
                             final Placeholders placeholders, final ParsedSectionTextCreator textCreator,
                             final QuestTypeApi questTypeApi, final PlayerDataStorage playerDataStorage) {
        super(loggerFactory, log, placeholders, api.getQuestPackageManager(), parsers, "Quest Canceler", "cancel");
        this.loggerFactory = loggerFactory;
        this.api = api;
        this.pluginMessage = pluginMessage;
        this.textCreator = textCreator;
        this.questTypeApi = questTypeApi;
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    protected Map.Entry<QuestCancelerID, QuestCanceler> loadSection(final String sectionName, final SectionInstruction instruction) throws QuestException {
        final QuestPackage pack = instruction.getPackage();
        final Text name = textCreator.parseFromSection(pack, instruction.getSection(), "name");
        final String rawItem = instruction.read().value("item").string().getOptional(pack.getConfig().getString("item.cancel_button")).getValue(null);
        final ItemID item = rawItem == null ? null : new ItemID(placeholders, packManager, pack, rawItem);

        final Optional<Argument<Location>> location = instruction.read().value("location").location().getOptional();
        final Argument<List<ConditionID>> conditions = instruction.read().value("conditions").parse(ConditionID::new).list().getOptional(Collections.emptyList());
        final Argument<List<ActionID>> actions = instruction.read().value("actions").parse(ActionID::new).list().getOptional(Collections.emptyList());
        final Argument<List<ObjectiveID>> objectives = instruction.read().value("objectives").parse(ObjectiveID::new).list().getOptional(Collections.emptyList());
        final Argument<List<String>> tags = instruction.read().value("tags").string().list().getOptional(Collections.emptyList());
        final Argument<List<String>> points = instruction.read().value("points").string().list().getOptional(Collections.emptyList());
        final Argument<List<JournalEntryID>> journal = instruction.read().value("journal").parse(JournalEntryID::new).list().getOptional(Collections.emptyList());

        final QuestCanceler.CancelData cancelData = new QuestCanceler.CancelData(conditions, actions, objectives, tags, points, journal, location.orElse(null));
        final BetonQuestLogger logger = loggerFactory.create(QuestCanceler.class);
        final QuestCancelerID identifier = getIdentifier(pack, sectionName);
        final QuestCanceler questCanceler = new QuestCanceler(logger, questTypeApi, playerDataStorage, identifier,
                api.getFeatureApi(), pluginMessage, name, item, pack, cancelData);
        return Map.entry(identifier, questCanceler);
    }

    @Override
    protected QuestCancelerID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new QuestCancelerID(placeholders, packManager, pack, identifier);
    }
}
