package org.betonquest.betonquest.quest.registry;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.bstats.InstructionMetricsSupplier;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.quest.registry.feature.FeatureRegistries;
import org.betonquest.betonquest.quest.registry.processor.CancellerProcessor;
import org.betonquest.betonquest.quest.registry.processor.ConditionProcessor;
import org.betonquest.betonquest.quest.registry.processor.ConversationProcessor;
import org.betonquest.betonquest.quest.registry.processor.EventProcessor;
import org.betonquest.betonquest.quest.registry.processor.NpcProcessor;
import org.betonquest.betonquest.quest.registry.processor.ObjectiveProcessor;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.betonquest.betonquest.schedule.EventScheduling;

import java.util.Collection;
import java.util.Map;

/**
 * Stores the active Quest Types, Conversations, Quest Canceller and Event Scheduler.
 */
public class QuestRegistry {
    /**
     * The custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Event scheduling module.
     */
    private final EventScheduling eventScheduling;

    /**
     * Condition logic.
     */
    private final ConditionProcessor conditionProcessor;

    /**
     * Event logic.
     */
    private final EventProcessor eventProcessor;

    /**
     * Objective logic.
     */
    private final ObjectiveProcessor objectiveProcessor;

    /**
     * Variable logic.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Quest Canceller logic.
     */
    private final CancellerProcessor cancellerProcessor;

    /**
     * Conversation Data logic.
     */
    private final ConversationProcessor conversationProcessor;

    /**
     * Npc getting and logic.
     */
    private final NpcProcessor npcProcessor;

    /**
     * Create a new Registry for storing and using Conditions, Events, Objectives, Variables,
     * Conversations, Npcs and Quest cancellers.
     *
     * @param log                 the custom logger for this registry
     * @param loggerFactory       the logger factory used for new custom logger instances
     * @param plugin              the plugin used to create new conversation data
     * @param otherRegistries     the available other types
     * @param questTypeRegistries the available quest types
     */
    public QuestRegistry(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory, final BetonQuest plugin,
                         final FeatureRegistries otherRegistries, final QuestTypeRegistries questTypeRegistries) {
        this.log = log;
        this.eventScheduling = new EventScheduling(loggerFactory.create(EventScheduling.class, "Schedules"), otherRegistries.eventScheduling());
        this.conditionProcessor = new ConditionProcessor(loggerFactory.create(ConditionProcessor.class), questTypeRegistries.condition());
        this.eventProcessor = new EventProcessor(loggerFactory.create(EventProcessor.class), questTypeRegistries.event());
        this.objectiveProcessor = new ObjectiveProcessor(loggerFactory.create(ObjectiveProcessor.class), questTypeRegistries.objective());
        this.variableProcessor = new VariableProcessor(loggerFactory.create(VariableProcessor.class), questTypeRegistries.variable());
        this.cancellerProcessor = new CancellerProcessor(loggerFactory.create(CancellerProcessor.class));
        this.npcProcessor = new NpcProcessor(loggerFactory.create(NpcProcessor.class), questTypeRegistries.npc(), loggerFactory, plugin);
        this.conversationProcessor = new ConversationProcessor(loggerFactory.create(ConversationProcessor.class), plugin, npcProcessor);
    }

    /**
     * Loads Conditions, Events, Objectives, Variables, Conversations, Quest Canceller and Event Scheduler.
     * <p>
     * Removes previous data and loads the given QuestPackages.
     *
     * @param packages the quest packages to load
     */
    public void loadData(final Collection<QuestPackage> packages) {
        eventScheduling.stopAll();
        conditionProcessor.clear();
        eventProcessor.clear();
        objectiveProcessor.clear();
        variableProcessor.clear();
        cancellerProcessor.clear();
        npcProcessor.clear();
        conversationProcessor.clear();

        for (final QuestPackage pack : packages) {
            final String packName = pack.getQuestPath();
            log.debug(pack, "Loading stuff in package " + packName);
            cancellerProcessor.load(pack);
            eventProcessor.load(pack);
            conditionProcessor.load(pack);
            objectiveProcessor.load(pack);
            npcProcessor.load(pack);
            conversationProcessor.load(pack);
            eventScheduling.loadData(pack);

            log.debug(pack, "Everything in package " + packName + " loaded");
        }

        conversationProcessor.checkExternalPointers();

        log.info("There are " + conditionProcessor.size() + " conditions, " + eventProcessor.size() + " events, "
                + objectiveProcessor.size() + " objectives, " + npcProcessor.size() + " npcs and "
                + conversationProcessor.size() + " conversations loaded from "
                + packages.size() + " packages.");

        eventScheduling.startAll();
    }

    /**
     * Gets the bstats metric supplier for registered and active quest types.
     *
     * @return instruction metrics for conditions, events, objectives and variables
     */
    public Map<String, InstructionMetricsSupplier<? extends ID>> metricsSupplier() {
        return Map.ofEntries(
                conditionProcessor.metricsSupplier(),
                eventProcessor.metricsSupplier(),
                npcProcessor.metricsSupplier(),
                objectiveProcessor.metricsSupplier(),
                variableProcessor.metricsSupplier()
        );
    }

    /**
     * Stops the {@link EventScheduling} module.
     */
    public void stopAllEventSchedules() {
        eventScheduling.stopAll();
    }

    /**
     * Gets the class processing condition logic.
     *
     * @return condition logic
     */
    public ConditionProcessor conditions() {
        return conditionProcessor;
    }

    /**
     * Gets the class processing event logic.
     *
     * @return event logic
     */
    public EventProcessor events() {
        return eventProcessor;
    }

    /**
     * Gets the class processing objective logic.
     *
     * @return objective logic
     */
    public ObjectiveProcessor objectives() {
        return objectiveProcessor;
    }

    /**
     * Gets the class processing variable logic.
     *
     * @return variable logic
     */
    public VariableProcessor variables() {
        return variableProcessor;
    }

    /**
     * Gets the class processing quest canceller logic.
     *
     * @return canceller logic
     */
    public CancellerProcessor questCanceller() {
        return cancellerProcessor;
    }

    /**
     * Gets the class processing quest conversation logic.
     *
     * @return conversation logic
     */
    public ConversationProcessor conversations() {
        return conversationProcessor;
    }

    /**
     * Gets the class processing quest conversation logic.
     *
     * @return conversation logic
     */
    public NpcProcessor npcs() {
        return npcProcessor;
    }
}
