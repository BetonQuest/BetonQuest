package org.betonquest.betonquest.quest;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.kernel.processor.QuestProcessor;
import org.betonquest.betonquest.kernel.processor.StartTask;
import org.betonquest.betonquest.kernel.processor.feature.ConversationProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ActionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ConditionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.NpcProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ObjectiveProcessor;
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;
import org.betonquest.betonquest.kernel.registry.feature.InterceptorRegistry;
import org.betonquest.betonquest.kernel.registry.feature.NotifyIORegistry;
import org.betonquest.betonquest.lib.dependency.component.DefaultCoreComponentLoader;
import org.betonquest.betonquest.menu.RPGMenu;
import org.betonquest.betonquest.schedule.ActionScheduling;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Responsible for loading the quest factory types in the correct order.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class CoreQuestTypeHandler {

    /**
     * The logger used for the {@link CoreQuestTypeHandler}.
     */
    private final BetonQuestLogger log;

    /**
     * Contains all processors after initialization to execute functions for all of them iteratively.
     */
    private final Collection<QuestProcessor<?, ?>> allProcessors;

    /**
     * Contains all processors that are not part of the main quest processing chain.
     */
    private final Collection<QuestProcessor<?, ?>> additionalProcessors;

    /**
     * The default core component loader to load the components in this class.
     */
    private final DefaultCoreComponentLoader coreComponentLoader;

    /**
     * Sole constructor.
     *
     * @param log                 the logger
     * @param coreComponentLoader the pre initialized component loader
     */
    public CoreQuestTypeHandler(final BetonQuestLogger log, final DefaultCoreComponentLoader coreComponentLoader) {
        this.log = log;
        this.allProcessors = new ArrayList<>();
        this.additionalProcessors = new ArrayList<>();
        this.coreComponentLoader = coreComponentLoader;
    }

    /**
     * Initializes the quest types in a strict order to maintain dependencies between them.
     */
    public void init() {
        coreComponentLoader.load();
        coreComponentLoader.getAll(QuestProcessor.class).forEach(allProcessors::add);
    }

    /**
     * Loads the processors with the given quest packages.
     * <br> <br>
     * Removes previously loaded data entirely.
     *
     * @param packages the quest packages to load
     */
    public void loadData(final Collection<QuestPackage> packages) {
        final ConversationProcessor conversationProcessor = coreComponentLoader.get(ConversationProcessor.class);
        final ActionScheduling schedulesProcessor = coreComponentLoader.get(ActionScheduling.class);
        Objects.requireNonNull(conversationProcessor, "Conversation processor must be initialized before loading data!");
        Objects.requireNonNull(schedulesProcessor, "Schedule processor must be initialized before loading data!");

        allProcessors.forEach(QuestProcessor::clear);
        additionalProcessors.forEach(QuestProcessor::clear);

        for (final QuestPackage pack : packages) {
            final String packName = pack.getQuestPath();
            log.debug(pack, "Loading stuff in package " + packName);
            allProcessors.forEach(processor -> processor.load(pack));
            additionalProcessors.forEach(processor -> processor.load(pack));
            log.debug(pack, "Everything in package " + packName + " loaded");
        }

        conversationProcessor.checkExternalPointers();

        log.info("There are " + readableSize()
                + " (Additional: " + additionalProcessors.stream().map(QuestProcessor::readableSize).collect(Collectors.joining(", ")) + ")"
                + " loaded from " + packages.size() + " packages.");

        schedulesProcessor.startAll();
        additionalProcessors.forEach(questProcessor -> {
            if (questProcessor instanceof final StartTask startTask) {
                startTask.startAll();
            }
        });
    }

    /**
     * Gets the number of loaded Core Quest Types with their readable names.
     *
     * @return all loaded amounts
     */
    public String readableSize() {
        return allProcessors.stream().sorted(Comparator.comparing(questProcessor -> questProcessor.readableSize().split(" ")[1]))
                .map(QuestProcessor::readableSize).collect(Collectors.joining(", "));
    }

    /**
     * Gets a modifiable list of additional processors.
     * May be used to add more processors or remove some.
     *
     * @return all additional processors
     */
    public Collection<QuestProcessor<?, ?>> getAdditionalProcessors() {
        return additionalProcessors;
    }

    /**
     * Gets the action processor.
     *
     * @return the action processor
     */
    public ActionProcessor getActionProcessor() {
        return coreComponentLoader.get(ActionProcessor.class);
    }

    /**
     * Gets the condition processor.
     *
     * @return the condition processor
     */
    public ConditionProcessor getConditionProcessor() {
        return coreComponentLoader.get(ConditionProcessor.class);
    }

    /**
     * Gets the objective processor.
     *
     * @return the objective processor
     */
    public ObjectiveProcessor getObjectiveProcessor() {
        return coreComponentLoader.get(ObjectiveProcessor.class);
    }

    /**
     * Gets the placeholder processor.
     *
     * @return the placeholder processor
     */
    public PlaceholderProcessor getPlaceholderProcessor() {
        return coreComponentLoader.get(PlaceholderProcessor.class);
    }

    /**
     * Gets the npc processor.
     *
     * @return the npc processor
     */
    public NpcProcessor getNpcProcessor() {
        return coreComponentLoader.get(NpcProcessor.class);
    }

    /**
     * Gets the schedule processor.
     *
     * @return the schedule processor
     */
    public ActionScheduling getScheduleProcessor() {
        return coreComponentLoader.get(ActionScheduling.class);
    }

    /**
     * Gets the conversation processor.
     *
     * @return the conversation processor
     */
    public ConversationProcessor getConversationProcessor() {
        return coreComponentLoader.get(ConversationProcessor.class);
    }

    /**
     * Gets the plugin message instance.
     *
     * @return the plugin message instance
     */
    public PluginMessage getPluginMessage() {
        return coreComponentLoader.get(PluginMessage.class);
    }

    /**
     * Gets the text parser.
     *
     * @return the text parser
     */
    public TextParser getTextParser() {
        return coreComponentLoader.get(TextParser.class);
    }

    /**
     * Gets the text creator.
     *
     * @return the text creator
     */
    public ParsedSectionTextCreator getTextCreator() {
        return coreComponentLoader.get(ParsedSectionTextCreator.class);
    }

    /**
     * Gets the conversation io registry.
     *
     * @return the conversation io registry
     */
    public ConversationIORegistry getConversationIORegistry() {
        return coreComponentLoader.get(ConversationIORegistry.class);
    }

    /**
     * Gets the interceptor registry.
     *
     * @return the interceptor registry
     */
    public InterceptorRegistry getInterceptorRegistry() {
        return coreComponentLoader.get(InterceptorRegistry.class);
    }

    /**
     * Gets the notify io registry.
     *
     * @return the notify io registry
     */
    public NotifyIORegistry getNotifyIORegistry() {
        return coreComponentLoader.get(NotifyIORegistry.class);
    }

    /**
     * Gets the rpg menu.
     *
     * @return the rpg menu
     */
    public RPGMenu getRpgMenu() {
        return coreComponentLoader.get(RPGMenu.class);
    }
}
