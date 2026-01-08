package org.betonquest.betonquest.api.quest.objective.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.kernel.processor.quest.ActionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ConditionProcessor;
import org.betonquest.betonquest.lib.logger.QuestExceptionHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Default implementation of the {@link ObjectiveFactoryService}.
 */
public class DefaultObjectiveFactoryService implements ObjectiveFactoryService {

    /**
     * The objective service data.
     */
    private final ObjectiveServiceData objectiveServiceData;

    /**
     * The objective service.
     */
    private final ObjectiveService objectiveService;

    /**
     * The processors to process actions.
     */
    private final ActionProcessor actionProcessor;

    /**
     * The processors to process conditions.
     */
    private final ConditionProcessor conditionProcessor;

    /**
     * The exception handler for this service.
     */
    private final QuestExceptionHandler questExceptionHandler;

    /**
     * The logger for this service.
     */
    private final BetonQuestLogger logger;

    /**
     * The objective related to this service.
     */
    private ObjectiveID objectiveID;

    /**
     * Creates a new objective service.
     *
     * @param objectiveID        the objective related to this service
     * @param actionProcessor    the event processor to use
     * @param conditionProcessor the condition processor to use
     * @param objectiveService   the event service to request events from
     * @param factory            the logger factory to use
     * @throws QuestException if the objective service data of the instruction could not be parsed
     */
    public DefaultObjectiveFactoryService(final ObjectiveID objectiveID, final ActionProcessor actionProcessor,
                                          final ConditionProcessor conditionProcessor, final ObjectiveService objectiveService,
                                          final BetonQuestLoggerFactory factory) throws QuestException {
        this.objectiveID = objectiveID;
        this.objectiveService = objectiveService;
        this.actionProcessor = actionProcessor;
        this.conditionProcessor = conditionProcessor;
        this.logger = factory.create(DefaultObjectiveFactoryService.class);
        this.questExceptionHandler = new QuestExceptionHandler(objectiveID.getPackage(), this.logger, objectiveID.getFull());
        this.objectiveServiceData = parseObjectiveData(objectiveID.getInstruction());
    }

    private static ObjectiveServiceData parseObjectiveData(final Instruction instruction) throws QuestException {
        final FlagArgument<Boolean> persistent = instruction.bool().getFlag("persistent", true);
        final Optional<Argument<List<ActionID>>> actions = instruction.parse(ActionID::new).list().get("actions");
        final Optional<Argument<List<ConditionID>>> conditions = instruction.parse(ConditionID::new).list().get("conditions");
        final FlagArgument<Number> notify = instruction.number().atLeast(0).getFlag("notify", 1);
        return new ObjectiveServiceData(conditions, actions, persistent, notify);
    }

    @Override
    public <T extends Event> EventServiceSubscriptionBuilder<T> request(final Class<T> eventClass) {
        return objectiveService.request(eventClass).source(objectiveID.getPackage());
    }

    @Override
    public QuestExceptionHandler getExceptionHandler() {
        return questExceptionHandler;
    }

    @Override
    public BetonQuestLogger getLogger() {
        return logger;
    }

    @Override
    public void renameObjective(final ObjectiveID newObjectiveID) {
        this.objectiveID = newObjectiveID;
    }

    @Override
    public ObjectiveID getObjectiveID() {
        return objectiveID;
    }

    @Override
    public ObjectiveServiceDataProvider getServiceDataProvider() {
        return objectiveServiceData;
    }

    @Override
    public boolean checkConditions(@Nullable final Profile profile) throws QuestException {
        final ObjectiveServiceDataProvider provider = getServiceDataProvider();
        final List<ConditionID> conditions = provider.getConditions(profile);
        return conditions.isEmpty() || conditionProcessor.checks(profile, conditions, true);
    }

    @Override
    public void callActions(@Nullable final Profile profile) throws QuestException {
        final ObjectiveServiceDataProvider provider = getServiceDataProvider();
        final List<ActionID> events = provider.getActions(profile);
        if (events.isEmpty()) {
            return;
        }
        actionProcessor.executes(profile, events);
    }
}
