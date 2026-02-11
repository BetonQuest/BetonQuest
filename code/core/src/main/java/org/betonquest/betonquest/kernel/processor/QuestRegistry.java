package org.betonquest.betonquest.kernel.processor;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.identifier.CompassIdentifier;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.identifier.JournalEntryIdentifier;
import org.betonquest.betonquest.api.identifier.JournalMainPageIdentifier;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.identifier.QuestCancelerIdentifier;
import org.betonquest.betonquest.api.identifier.ReadableIdentifier;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;
import org.betonquest.betonquest.api.instruction.InstructionApi;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcHider;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.bstats.InstructionMetricsSupplier;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.feature.QuestCanceler;
import org.betonquest.betonquest.feature.QuestCompass;
import org.betonquest.betonquest.feature.journal.JournalMainPageEntry;
import org.betonquest.betonquest.kernel.processor.feature.CancelerProcessor;
import org.betonquest.betonquest.kernel.processor.feature.CompassProcessor;
import org.betonquest.betonquest.kernel.processor.feature.ConversationProcessor;
import org.betonquest.betonquest.kernel.processor.feature.ItemProcessor;
import org.betonquest.betonquest.kernel.processor.feature.JournalEntryProcessor;
import org.betonquest.betonquest.kernel.processor.feature.JournalMainPageProcessor;
import org.betonquest.betonquest.kernel.processor.quest.NpcProcessor;
import org.betonquest.betonquest.kernel.registry.feature.BaseFeatureRegistries;
import org.betonquest.betonquest.kernel.registry.quest.IdentifierTypeRegistry;
import org.betonquest.betonquest.schedule.ActionScheduling;
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
 * @param actionScheduling Action scheduling module.
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
        ActionScheduling actionScheduling,
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
     * Create a new Registry for storing and using Processors.
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
     * @param identifiers       the available identifiers
     * @return the newly created QuestRegistry
     * @throws QuestException if identifier factories are not registered
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static QuestRegistry create(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                                       final BetonQuest plugin, final CoreQuestRegistry coreQuestRegistry,
                                       final BaseFeatureRegistries otherRegistries, final PluginMessage pluginMessage,
                                       final ParsedSectionTextCreator textCreator, final ProfileProvider profileProvider,
                                       final PlayerDataStorage playerDataStorage, final IdentifierTypeRegistry identifiers) throws QuestException {
        final IdentifierFactory<ItemIdentifier> itemIdentifierFactory = identifiers.getFactory(ItemIdentifier.class);
        final IdentifierFactory<QuestCancelerIdentifier> cancelerIdentifierIdentifierFactory = identifiers.getFactory(QuestCancelerIdentifier.class);
        final IdentifierFactory<NpcIdentifier> npcIdentifierFactory = identifiers.getFactory(NpcIdentifier.class);
        final IdentifierFactory<JournalMainPageIdentifier> journalMainPageIdentifierFactory = identifiers.getFactory(JournalMainPageIdentifier.class);
        final IdentifierFactory<JournalEntryIdentifier> entryIdentifierIdentifierFactory = identifiers.getFactory(JournalEntryIdentifier.class);
        final IdentifierFactory<ConversationIdentifier> conversationIdentifierFactory = identifiers.getFactory(ConversationIdentifier.class);
        final IdentifierFactory<CompassIdentifier> compassIdentifierFactory = identifiers.getFactory(CompassIdentifier.class);
        final IdentifierFactory<ScheduleIdentifier> scheduleIdentifierFactory = identifiers.getFactory(ScheduleIdentifier.class);

        final InstructionApi instructionApi = plugin.getInstructionApi();
        final ItemProcessor items = new ItemProcessor(loggerFactory.create(ItemProcessor.class),
                itemIdentifierFactory, otherRegistries.item(), instructionApi);

        final ActionScheduling actionScheduling = new ActionScheduling(
                loggerFactory.create(ActionScheduling.class, "Schedules"), instructionApi,
                otherRegistries.actionScheduling(), scheduleIdentifierFactory);
        final CancelerProcessor cancelers = new CancelerProcessor(loggerFactory.create(CancelerProcessor.class),
                loggerFactory, plugin, pluginMessage, instructionApi, textCreator, coreQuestRegistry,
                playerDataStorage, cancelerIdentifierIdentifierFactory);
        final CompassProcessor compasses = new CompassProcessor(loggerFactory.create(CompassProcessor.class),
                instructionApi, textCreator, compassIdentifierFactory);
        final ConversationProcessor conversations = new ConversationProcessor(loggerFactory.create(ConversationProcessor.class),
                loggerFactory, plugin, textCreator, otherRegistries.conversationIO(), otherRegistries.interceptor(),
                instructionApi, pluginMessage, conversationIdentifierFactory);
        final JournalEntryProcessor journalEntries = new JournalEntryProcessor(loggerFactory.create(JournalEntryProcessor.class),
                entryIdentifierIdentifierFactory, textCreator);
        final JournalMainPageProcessor journalMainPages = new JournalMainPageProcessor(
                loggerFactory.create(JournalMainPageProcessor.class), instructionApi, textCreator,
                journalMainPageIdentifierFactory);
        final NpcProcessor npcs = new NpcProcessor(loggerFactory.create(NpcProcessor.class), loggerFactory,
                npcIdentifierFactory, conversationIdentifierFactory, otherRegistries.npc(), pluginMessage,
                plugin, profileProvider, coreQuestRegistry.conditions(), conversations.getStarter(), instructionApi);
        return new QuestRegistry(log, coreQuestRegistry, actionScheduling, cancelers, compasses, conversations,
                items, journalEntries, journalMainPages, npcs, new ArrayList<>());
    }

    /**
     * Loads the Processors with the QuestPackages.
     * <p>
     * Removes previous data and loads the given QuestPackages.
     *
     * @param packages the quest packages to load
     */
    public void loadData(final Collection<QuestPackage> packages) {
        actionScheduling.clear();
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
            actionScheduling.load(pack);
            additional.forEach(questProcessor -> questProcessor.load(pack));

            log.debug(pack, "Everything in package " + packName + " loaded");
        }

        conversations.checkExternalPointers();

        log.info("There are " + String.join(", ", core.readableSize(),
                cancelers.readableSize(), compasses.readableSize(), conversations.readableSize(), items.readableSize(),
                journalEntries.readableSize(), journalMainPages.readableSize(), npcs.readableSize())
                + " (Additional: " + additional.stream().map(QuestProcessor::readableSize).collect(Collectors.joining(", ")) + ")"
                + " loaded from " + packages.size() + " packages.");

        actionScheduling.startAll();
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
    public Map<String, InstructionMetricsSupplier<? extends ReadableIdentifier>> metricsSupplier() {
        final Map<String, InstructionMetricsSupplier<? extends ReadableIdentifier>> map = new HashMap<>(core.metricsSupplier());
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
    public Map<QuestCancelerIdentifier, QuestCanceler> getCancelers() {
        return new HashMap<>(cancelers().getValues());
    }

    @Override
    public QuestCanceler getCanceler(final QuestCancelerIdentifier cancelerID) throws QuestException {
        return cancelers().get(cancelerID);
    }

    @Override
    public Map<CompassIdentifier, QuestCompass> getCompasses() {
        return new HashMap<>(compasses().getValues());
    }

    @Override
    public Text getJournalEntry(final JournalEntryIdentifier journalEntryID) throws QuestException {
        return journalEntries().get(journalEntryID);
    }

    @Override
    public void renameJournalEntry(final JournalEntryIdentifier name, final JournalEntryIdentifier rename) {
        journalEntries().renameJournalEntry(name, rename);
    }

    @Override
    public Map<JournalMainPageIdentifier, JournalMainPageEntry> getJournalMainPages() {
        return new HashMap<>(journalMainPages().getValues());
    }

    @Override
    public Npc<?> getNpc(final NpcIdentifier npcID, @Nullable final Profile profile) throws QuestException {
        return npcs().get(npcID).getNpc(profile);
    }

    @Override
    public NpcHider getNpcHider() {
        return npcs().getNpcHider();
    }

    @Override
    public QuestItem getItem(final ItemIdentifier itemID, @Nullable final Profile profile) throws QuestException {
        return items().get(itemID).getItem(profile);
    }
}
