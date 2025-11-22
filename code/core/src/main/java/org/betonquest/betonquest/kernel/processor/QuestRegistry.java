package org.betonquest.betonquest.kernel.processor;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.identifier.InstructionIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.api.quest.npc.feature.NpcHider;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.bstats.InstructionMetricsSupplier;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.feature.QuestCanceler;
import org.betonquest.betonquest.feature.QuestCompass;
import org.betonquest.betonquest.feature.journal.JournalMainPageEntry;
import org.betonquest.betonquest.id.CompassID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.id.JournalEntryID;
import org.betonquest.betonquest.id.JournalMainPageID;
import org.betonquest.betonquest.id.QuestCancelerID;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.kernel.processor.feature.CancelerProcessor;
import org.betonquest.betonquest.kernel.processor.feature.CompassProcessor;
import org.betonquest.betonquest.kernel.processor.feature.ConversationProcessor;
import org.betonquest.betonquest.kernel.processor.feature.ItemProcessor;
import org.betonquest.betonquest.kernel.processor.feature.JournalEntryProcessor;
import org.betonquest.betonquest.kernel.processor.feature.JournalMainPageProcessor;
import org.betonquest.betonquest.kernel.processor.quest.NpcProcessor;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.kernel.registry.feature.BaseFeatureRegistries;
import org.betonquest.betonquest.schedule.EventScheduling;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.jetbrains.annotations.Nullable;

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
 * @param additional       Additional quest processors.
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
) implements FeatureApi {

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
     * @param playerDataStorage the storage to get player data
     * @return the newly created QuestRegistry
     */
    public static QuestRegistry create(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                                       final BetonQuest plugin, final CoreQuestRegistry coreQuestRegistry,
                                       final BaseFeatureRegistries otherRegistries, final PluginMessage pluginMessage,
                                       final ParsedSectionTextCreator textCreator, final ProfileProvider profileProvider,
                                       final PlayerDataStorage playerDataStorage) {
        final VariableProcessor variables = coreQuestRegistry.variables();
        final QuestPackageManager packManager = plugin.getQuestPackageManager();
        final EventScheduling eventScheduling = new EventScheduling(loggerFactory.create(EventScheduling.class, "Schedules"), packManager, otherRegistries.eventScheduling());
        final CancelerProcessor cancelers = new CancelerProcessor(loggerFactory.create(CancelerProcessor.class), loggerFactory, plugin, pluginMessage, variables, textCreator, coreQuestRegistry, playerDataStorage);
        final CompassProcessor compasses = new CompassProcessor(loggerFactory.create(CompassProcessor.class), packManager, variables, textCreator);
        final ConversationProcessor conversations = new ConversationProcessor(loggerFactory.create(ConversationProcessor.class), loggerFactory, plugin,
                textCreator, otherRegistries.conversationIO(), otherRegistries.interceptor(), variables, pluginMessage);
        final ItemProcessor items = new ItemProcessor(loggerFactory.create(ItemProcessor.class), packManager, otherRegistries.item());
        final JournalEntryProcessor journalEntries = new JournalEntryProcessor(loggerFactory.create(JournalEntryProcessor.class), packManager, textCreator);
        final JournalMainPageProcessor journalMainPages = new JournalMainPageProcessor(loggerFactory.create(JournalMainPageProcessor.class), packManager, variables, textCreator);
        final NpcProcessor npcs = new NpcProcessor(loggerFactory.create(NpcProcessor.class), loggerFactory, packManager, variables,
                otherRegistries.npc(), pluginMessage, plugin, profileProvider, coreQuestRegistry, conversations.getStarter());
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
        eventScheduling.clear();
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
            eventScheduling.load(pack);
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

    @Override
    public ConversationApi conversationApi() {
        return conversations;
    }

    @Override
    public Map<QuestCancelerID, QuestCanceler> getCancelers() {
        return new HashMap<>(cancelers().getValues());
    }

    @Override
    public QuestCanceler getCanceler(final QuestCancelerID cancelerID) throws QuestException {
        return cancelers().get(cancelerID);
    }

    @Override
    public Map<CompassID, QuestCompass> getCompasses() {
        return new HashMap<>(compasses().getValues());
    }

    @Override
    public Text getJournalEntry(final JournalEntryID journalEntryID) throws QuestException {
        return journalEntries().get(journalEntryID);
    }

    @Override
    public void renameJournalEntry(final JournalEntryID name, final JournalEntryID rename) {
        journalEntries().renameJournalEntry(name, rename);
    }

    @Override
    public Map<JournalMainPageID, JournalMainPageEntry> getJournalMainPages() {
        return new HashMap<>(journalMainPages().getValues());
    }

    @Override
    public Npc<?> getNpc(final NpcID npcID, @Nullable final Profile profile) throws QuestException {
        return npcs().get(npcID).getNpc(profile);
    }

    @Override
    public NpcHider getNpcHider() {
        return npcs().getNpcHider();
    }

    @Override
    public QuestItem getItem(final ItemID itemID, @Nullable final Profile profile) throws QuestException {
        return items().get(itemID).getItem(profile);
    }
}
