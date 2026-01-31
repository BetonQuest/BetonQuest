package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.identifier.JournalEntryIdentifier;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.identifier.QuestCancelerIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.feature.QuestCanceler;
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
public class CancelerProcessor extends SectionProcessor<QuestCancelerIdentifier, QuestCanceler> {

    /**
     * Logger factory to create new class-specific loggers.
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
     * @param loggerFactory     the logger factory to create new class-specific logger
     * @param api               the BetonQuest API instance
     * @param pluginMessage     the {@link PluginMessage} instance
     * @param parsers           the argument parsers to use
     * @param placeholders      the {@link Placeholders} to create and resolve placeholders
     * @param textCreator       the text creator to parse text
     * @param questTypeApi      the Quest Type API
     * @param playerDataStorage the storage for player data
     * @param identifierFactory the identifier factory to create {@link QuestCancelerIdentifier}s for this type
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public CancelerProcessor(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                             final BetonQuestApi api, final PluginMessage pluginMessage, final ArgumentParsers parsers,
                             final Placeholders placeholders, final ParsedSectionTextCreator textCreator,
                             final QuestTypeApi questTypeApi, final PlayerDataStorage playerDataStorage,
                             final IdentifierFactory<QuestCancelerIdentifier> identifierFactory) {
        super(loggerFactory, log, placeholders, api.getQuestPackageManager(), parsers, identifierFactory, "Quest Canceler", "cancel");
        this.loggerFactory = loggerFactory;
        this.api = api;
        this.pluginMessage = pluginMessage;
        this.textCreator = textCreator;
        this.questTypeApi = questTypeApi;
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    protected Map.Entry<QuestCancelerIdentifier, QuestCanceler> loadSection(final String sectionName, final SectionInstruction instruction) throws QuestException {
        final QuestPackage pack = instruction.getPackage();
        final Text name = textCreator.parseFromSection(pack, instruction.getSection(), "name");
        final String rawItem = instruction.read().value("item").string().getOptional(pack.getConfig().getString("item.cancel_button")).getValue(null);
        final ItemIdentifier item = rawItem == null ? null : instruction.getParsers().forIdentifier(ItemIdentifier.class).apply(placeholders, packManager, pack, rawItem);

        final Optional<Argument<Location>> location = instruction.read().value("location").location().getOptional();
        final Argument<List<ConditionIdentifier>> conditions = instruction.read().value("conditions").identifier(ConditionIdentifier.class).list().getOptional(Collections.emptyList());
        final Argument<List<ActionIdentifier>> actions = instruction.read().value("actions").identifier(ActionIdentifier.class).list().getOptional(Collections.emptyList());
        final Argument<List<ObjectiveIdentifier>> objectives = instruction.read().value("objectives").identifier(ObjectiveIdentifier.class).list().getOptional(Collections.emptyList());
        final Argument<List<String>> tags = instruction.read().value("tags").string().list().getOptional(Collections.emptyList());
        final Argument<List<String>> points = instruction.read().value("points").string().list().getOptional(Collections.emptyList());
        final Argument<List<JournalEntryIdentifier>> journal = instruction.read().value("journal").identifier(JournalEntryIdentifier.class).list().getOptional(Collections.emptyList());

        final QuestCanceler.CancelData cancelData = new QuestCanceler.CancelData(conditions, actions, objectives, tags, points, journal, location.orElse(null));
        final BetonQuestLogger logger = loggerFactory.create(QuestCanceler.class);
        final QuestCancelerIdentifier identifier = getIdentifier(pack, sectionName);
        final QuestCanceler questCanceler = new QuestCanceler(logger, questTypeApi, playerDataStorage, identifier,
                api.getFeatureApi(), pluginMessage, name, item, pack, cancelData);
        return Map.entry(identifier, questCanceler);
    }
}
