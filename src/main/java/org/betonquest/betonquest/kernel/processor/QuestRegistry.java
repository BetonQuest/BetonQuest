package org.betonquest.betonquest.kernel.processor;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.bstats.InstructionMetricsSupplier;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.kernel.processor.feature.CancelerProcessor;
import org.betonquest.betonquest.kernel.processor.feature.CompassProcessor;
import org.betonquest.betonquest.kernel.processor.feature.ConversationProcessor;
import org.betonquest.betonquest.kernel.processor.feature.JournalEntryProcessor;
import org.betonquest.betonquest.kernel.processor.feature.JournalMainPageProcessor;
import org.betonquest.betonquest.kernel.processor.quest.NpcProcessor;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.kernel.registry.feature.FeatureRegistries;
import org.betonquest.betonquest.schedule.EventScheduling;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores the active Processors to store and execute type logic.
 *
 * @param log              The custom {@link BetonQuestLogger} instance for this class.
 * @param core             The core quest type processors.
 * @param eventScheduling  Event scheduling module.
 * @param cancelers        Quest Canceler logic.
 * @param compasses        Compasses.
 * @param conversations    Conversation Data logic.
 * @param journalEntries   Journal Entries.
 * @param journalMainPages Journal Main Pages.
 * @param npcs             Npc getting.
 */
public record QuestRegistry(
        BetonQuestLogger log,
        CoreQuestRegistry core,
        EventScheduling eventScheduling,
        CancelerProcessor cancelers,
        CompassProcessor compasses,
        ConversationProcessor conversations,
        JournalEntryProcessor journalEntries,
        JournalMainPageProcessor journalMainPages,
        NpcProcessor npcs
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
     * @param messageParser     the {@link MessageParser} instance
     * @param profileProvider   the profile provider instance
     * @return the newly created QuestRegistry
     */
    public static QuestRegistry create(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                                       final BetonQuest plugin, final CoreQuestRegistry coreQuestRegistry, final FeatureRegistries otherRegistries,
                                       final PluginMessage pluginMessage, final MessageParser messageParser, final ProfileProvider profileProvider) {
        final VariableProcessor variables = coreQuestRegistry.variables();
        final PlayerDataStorage playerDataStorage = plugin.getPlayerDataStorage();
        final EventScheduling eventScheduling = new EventScheduling(loggerFactory.create(EventScheduling.class, "Schedules"), otherRegistries.eventScheduling());
        final CancelerProcessor cancelers = new CancelerProcessor(loggerFactory.create(CancelerProcessor.class), loggerFactory, pluginMessage, variables, messageParser, playerDataStorage);
        final CompassProcessor compasses = new CompassProcessor(loggerFactory.create(CompassProcessor.class), variables, messageParser, playerDataStorage);
        final ConversationProcessor conversations = new ConversationProcessor(loggerFactory.create(ConversationProcessor.class), loggerFactory, plugin, variables, messageParser,
                playerDataStorage, otherRegistries.conversationIO(), otherRegistries.interceptor());
        final JournalEntryProcessor journalEntries = new JournalEntryProcessor(loggerFactory.create(JournalEntryProcessor.class), variables, messageParser, playerDataStorage);
        final JournalMainPageProcessor journalMainPages = new JournalMainPageProcessor(loggerFactory.create(JournalMainPageProcessor.class), variables, messageParser, playerDataStorage);
        final NpcProcessor npcs = new NpcProcessor(loggerFactory.create(NpcProcessor.class), loggerFactory, otherRegistries.npc(), pluginMessage, plugin, profileProvider);
        return new QuestRegistry(log, coreQuestRegistry, eventScheduling, cancelers, compasses, conversations, journalEntries, journalMainPages, npcs);
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
        journalEntries.clear();
        journalMainPages.clear();
        npcs.clear();

        for (final QuestPackage pack : packages) {
            final String packName = pack.getQuestPath();
            log.debug(pack, "Loading stuff in package " + packName);
            cancelers.load(pack);
            core.load(pack);
            compasses.load(pack);
            conversations.load(pack);
            journalEntries.load(pack);
            journalMainPages.load(pack);
            npcs.load(pack);
            eventScheduling.loadData(pack);

            log.debug(pack, "Everything in package " + packName + " loaded");
        }

        conversations.checkExternalPointers();

        log.info("There are " + String.join(", ", core.readableSize(),
                cancelers.readableSize(), compasses.readableSize(), conversations.readableSize(),
                journalEntries.readableSize(), journalMainPages.readableSize(), npcs.readableSize())
                + " loaded from " + packages.size() + " packages.");

        eventScheduling.startAll();
    }

    /**
     * Gets the bstats metric supplier for registered and active quest types.
     *
     * @return available instruction metrics
     */
    public Map<String, InstructionMetricsSupplier<? extends ID>> metricsSupplier() {
        final Map<String, InstructionMetricsSupplier<? extends ID>> map = new HashMap<>(core.metricsSupplier());
        map.putAll(Map.ofEntries(
                npcs.metricsSupplier())
        );
        return map;
    }
}
