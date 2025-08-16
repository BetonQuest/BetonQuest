package org.betonquest.betonquest.kernel.processor;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.InstructionIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.bstats.InstructionMetricsSupplier;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.kernel.processor.feature.CancelerProcessor;
import org.betonquest.betonquest.kernel.processor.feature.CompassProcessor;
import org.betonquest.betonquest.kernel.processor.feature.ConversationProcessor;
import org.betonquest.betonquest.kernel.processor.feature.ItemProcessor;
import org.betonquest.betonquest.kernel.processor.feature.JournalEntryProcessor;
import org.betonquest.betonquest.kernel.processor.feature.JournalMainPageProcessor;
import org.betonquest.betonquest.kernel.processor.quest.NpcProcessor;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.kernel.registry.feature.FeatureRegistries;
import org.betonquest.betonquest.schedule.EventScheduling;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Stores the active Processors to store and execute type logic.
 *
 * @param log              The custom {@link BetonQuestLogger} instance for this class.
 * @param core             The core quest type processors.
 * @param eventScheduling  Event scheduling module.
 * @param cancelers        Quest Canceler logic.
 * @param compasses        Compasses.
 * @param conversations    Conversation Data logic.
 * @param items            Quest Item logic.
 * @param journalEntries   Journal Entries.
 * @param journalMainPages Journal Main Pages.
 * @param npcs             Npc getting.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public record QuestRegistry(
        BetonQuestLogger log,
        CoreQuestRegistry core,
        EventScheduling eventScheduling,
        CancelerProcessor cancelers,
        CompassProcessor compasses,
        ConversationProcessor conversations,
        ItemProcessor items,
        JournalEntryProcessor journalEntries,
        JournalMainPageProcessor journalMainPages,
        NpcProcessor npcs,
        List<QuestProcessor<?, ?>> additional
) {

    /**
     * Create a new Registry for storing and using Conditions, Events, Objectives, Variables,
     * Conversations and Quest canceler.
     *
     * @param log               the custom logger for this registry
     * @param loggerFactory     the logger factory used for new custom logger instances
     * @param plugin            the plugin used to create new conversation data
     * @param coreQuestRegistry the core quest type processors
     * @param otherRegistries   the available other types
     * @param pluginMessage     the {@link PluginMessage} instance
     * @param textCreator       the text creator to parse text
     * @param profileProvider   the profile provider instance
     * @param questTypeApi      the Quest Type API
     * @param playerDataStorage the storage to get player data
     * @return the newly created QuestRegistry
     */
    public static QuestRegistry create(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                                       final BetonQuest plugin, final CoreQuestRegistry coreQuestRegistry,
                                       final FeatureRegistries otherRegistries, final PluginMessage pluginMessage,
                                       final ParsedSectionTextCreator textCreator, final ProfileProvider profileProvider,
                                       final QuestTypeApi questTypeApi, final PlayerDataStorage playerDataStorage) {
        final VariableProcessor variables = coreQuestRegistry.variables();
        final EventScheduling eventScheduling = new EventScheduling(loggerFactory.create(EventScheduling.class, "Schedules"), otherRegistries.eventScheduling());
        final CancelerProcessor cancelers = new CancelerProcessor(loggerFactory.create(CancelerProcessor.class), loggerFactory, plugin, pluginMessage, variables, textCreator, questTypeApi, playerDataStorage);
        final CompassProcessor compasses = new CompassProcessor(loggerFactory.create(CompassProcessor.class), variables, textCreator);
        final ConversationProcessor conversations = new ConversationProcessor(loggerFactory.create(ConversationProcessor.class), loggerFactory, plugin,
                textCreator, otherRegistries.conversationIO(), otherRegistries.interceptor(), variables);
        final ItemProcessor items = new ItemProcessor(loggerFactory.create(ItemProcessor.class), otherRegistries.item());
        final JournalEntryProcessor journalEntries = new JournalEntryProcessor(loggerFactory.create(JournalEntryProcessor.class), textCreator);
        final JournalMainPageProcessor journalMainPages = new JournalMainPageProcessor(loggerFactory.create(JournalMainPageProcessor.class), variables, textCreator);
        final NpcProcessor npcs = new NpcProcessor(loggerFactory.create(NpcProcessor.class), loggerFactory, otherRegistries.npc(), pluginMessage, plugin, profileProvider, questTypeApi);
        return new QuestRegistry(log, coreQuestRegistry, eventScheduling, cancelers, compasses, conversations, items, journalEntries, journalMainPages, npcs, new ArrayList<>());
    }

    /**
     * Loads the Processors with the QuestPackages.
     * <p>
     * Removes previous data and loads the given QuestPackages.
     *
     * @param packages the quest packages to load
     */
    public void loadData(final Collection<QuestPackage> packages) {
        eventScheduling.stopAll();
        core.clear();
        cancelers.clear();
        conversations.clear();
        compasses.clear();
        items.clear();
        journalEntries.clear();
        journalMainPages.clear();
        npcs.clear();
        additional.forEach(QuestProcessor::clear);

        for (final QuestPackage pack : packages) {
            final String packName = pack.getQuestPath();
            log.debug(pack, "Loading stuff in package " + packName);
            cancelers.load(pack);
            core.load(pack);
            compasses.load(pack);
            conversations.load(pack);
            items.load(pack);
            journalEntries.load(pack);
            journalMainPages.load(pack);
            npcs.load(pack);
            eventScheduling.loadData(pack);
            additional.forEach(questProcessor -> questProcessor.load(pack));

            log.debug(pack, "Everything in package " + packName + " loaded");
        }

        conversations.checkExternalPointers();

        log.info("There are " + String.join(", ", core.readableSize(),
                cancelers.readableSize(), compasses.readableSize(), conversations.readableSize(), items.readableSize(),
                journalEntries.readableSize(), journalMainPages.readableSize(), npcs.readableSize())
                + " (Additional: " + additional.stream().map(QuestProcessor::readableSize).collect(Collectors.joining(", ")) + ")"
                + " loaded from " + packages.size() + " packages.");

        eventScheduling.startAll();
        additional.forEach(questProcessor -> {
            if (questProcessor instanceof final StartTask startTask) {
                startTask.startAll();
            }
        });
    }

    /**
     * Gets the bstats metric supplier for registered and active quest types.
     *
     * @return available instruction metrics
     */
    public Map<String, InstructionMetricsSupplier<? extends InstructionIdentifier>> metricsSupplier() {
        final Map<String, InstructionMetricsSupplier<? extends InstructionIdentifier>> map = new HashMap<>(core.metricsSupplier());
        map.putAll(Map.ofEntries(
                items.metricsSupplier(),
                npcs.metricsSupplier())
        );
        return map;
    }
}
