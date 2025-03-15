package org.betonquest.betonquest.kernel.processor;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.bstats.InstructionMetricsSupplier;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.kernel.processor.feature.CancelerProcessor;
import org.betonquest.betonquest.kernel.processor.feature.CompassProcessor;
import org.betonquest.betonquest.kernel.processor.feature.ConversationProcessor;
import org.betonquest.betonquest.kernel.processor.feature.JournalEntryProcessor;
import org.betonquest.betonquest.kernel.processor.feature.JournalMainPageProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ConditionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.EventProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ObjectiveProcessor;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.kernel.registry.feature.FeatureRegistries;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.betonquest.betonquest.schedule.EventScheduling;

import java.util.Collection;
import java.util.Map;

/**
 * Stores the active Processors to store and execute type logic.
 *
 * @param log              The custom {@link BetonQuestLogger} instance for this class.
 * @param eventScheduling  Event scheduling module.
 * @param conditions       Condition logic.
 * @param events           Event logic.
 * @param objectives       Objective logic.
 * @param variables        Variable logic.
 * @param cancelers        Quest Canceler logic.
 * @param conversations    Conversation Data logic.
 * @param compasses        Compasses.
 * @param journalEntries   Journal Entries.
 * @param journalMainPages Journal Main Pages.
 */
public record QuestRegistry(
        BetonQuestLogger log,
        EventScheduling eventScheduling,
        ConditionProcessor conditions,
        EventProcessor events,
        ObjectiveProcessor objectives,
        VariableProcessor variables,
        CancelerProcessor cancelers,
        ConversationProcessor conversations,
        CompassProcessor compasses,
        JournalEntryProcessor journalEntries,
        JournalMainPageProcessor journalMainPages
) {

    /**
     * Create a new Registry for storing and using Conditions, Events, Objectives, Variables,
     * Conversations and Quest canceler.
     *
     * @param log                 the custom logger for this registry
     * @param loggerFactory       the logger factory used for new custom logger instances
     * @param plugin              the plugin used to create new conversation data
     * @param otherRegistries     the available other types
     * @param questTypeRegistries the available quest types
     * @param pluginMessage       the {@link PluginMessage} instance
     * @return the newly created
     */
    public static QuestRegistry create(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                                       final BetonQuest plugin, final FeatureRegistries otherRegistries,
                                       final QuestTypeRegistries questTypeRegistries, final PluginMessage pluginMessage,
                                       final MessageParser messageParser) {
        final EventScheduling eventScheduling = new EventScheduling(loggerFactory.create(EventScheduling.class, "Schedules"), otherRegistries.eventScheduling());
        final ConditionProcessor conditions = new ConditionProcessor(loggerFactory.create(ConditionProcessor.class), questTypeRegistries.condition());
        final EventProcessor events = new EventProcessor(loggerFactory.create(EventProcessor.class), questTypeRegistries.event());
        final ObjectiveProcessor objectives = new ObjectiveProcessor(loggerFactory.create(ObjectiveProcessor.class), questTypeRegistries.objective());
        final VariableProcessor variables = new VariableProcessor(loggerFactory.create(VariableProcessor.class), questTypeRegistries.variable());
        final CancelerProcessor cancelers = new CancelerProcessor(loggerFactory.create(CancelerProcessor.class), loggerFactory, pluginMessage, variables, messageParser, plugin.getPlayerDataStorage());
        final ConversationProcessor conversations = new ConversationProcessor(loggerFactory.create(ConversationProcessor.class), loggerFactory, plugin, variables, messageParser, plugin.getPlayerDataStorage(),
                otherRegistries.conversationIO(), otherRegistries.interceptor());
        final CompassProcessor compasses = new CompassProcessor(loggerFactory.create(CompassProcessor.class), variables, messageParser, plugin.getPlayerDataStorage());
        final JournalEntryProcessor journalEntries = new JournalEntryProcessor(loggerFactory.create(JournalEntryProcessor.class), variables, messageParser, plugin.getPlayerDataStorage());
        final JournalMainPageProcessor journalMainPages = new JournalMainPageProcessor(loggerFactory.create(JournalMainPageProcessor.class), variables, messageParser, plugin.getPlayerDataStorage());
        return new QuestRegistry(log, eventScheduling, conditions, events, objectives, variables, cancelers, conversations, compasses, journalEntries, journalMainPages);
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
        conditions.clear();
        events.clear();
        objectives.clear();
        cancelers.clear();
        conversations.clear();
        compasses.clear();
        journalEntries.clear();
        journalMainPages.clear();

        for (final QuestPackage pack : packages) {
            final String packName = pack.getQuestPath();
            log.debug(pack, "Loading stuff in package " + packName);
            cancelers.load(pack);
            events.load(pack);
            conditions.load(pack);
            objectives.load(pack);
            conversations.load(pack);
            compasses.load(pack);
            journalEntries.load(pack);
            journalMainPages.load(pack);
            eventScheduling.loadData(pack);

            log.debug(pack, "Everything in package " + packName + " loaded");
        }

        conversations.checkExternalPointers();

        log.info("There are " + String.join(", ", conditions.readableSize(), events.readableSize(),
                objectives.readableSize(), cancelers.readableSize(), compasses.readableSize(),
                journalEntries.readableSize(), journalMainPages.readableSize())
                + " and " + conversations.readableSize() + " loaded from " + packages.size() + " packages.");

        eventScheduling.startAll();
    }

    /**
     * Gets the bstats metric supplier for registered and active quest types.
     *
     * @return instruction metrics for conditions, events, objectives and variables
     */
    public Map<String, InstructionMetricsSupplier<? extends ID>> metricsSupplier() {
        return Map.ofEntries(
                conditions.metricsSupplier(),
                events.metricsSupplier(),
                objectives.metricsSupplier(),
                variables.metricsSupplier()
        );
    }
}
