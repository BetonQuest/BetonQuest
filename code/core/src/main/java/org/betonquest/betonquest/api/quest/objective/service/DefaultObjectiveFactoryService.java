package org.betonquest.betonquest.api.quest.objective.service;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.QuestDataUpdateEvent;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.DefaultIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveState;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.kernel.processor.quest.ActionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ConditionProcessor;
import org.betonquest.betonquest.lib.logger.QuestExceptionHandler;
import org.betonquest.betonquest.lib.profile.ProfileKeyMap;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Default implementation of the {@link ObjectiveService}.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class DefaultObjectiveFactoryService implements ObjectiveService {

    /**
     * The objective service data.
     */
    private final ObjectiveServiceData objectiveServiceData;

    /**
     * The objective service.
     */
    private final ObjectiveServiceProvider objectiveService;

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
     * The objective data per profile.
     */
    private final Map<Profile, String> objectiveData;

    /**
     * The profile provider.
     */
    private final ProfileProvider profileProvider;

    /**
     * The properties of the objective.
     */
    private final ObjectiveProperties properties;

    /**
     * The default data supplier.
     */
    private QuestFunction<Profile, String> defaultDataSupplier;

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
     * @param profileProvider    the profile provider to use
     * @throws QuestException if the objective service data of the instruction could not be parsed
     */
    public DefaultObjectiveFactoryService(final ObjectiveID objectiveID, final ActionProcessor actionProcessor,
                                          final ConditionProcessor conditionProcessor, final ObjectiveServiceProvider objectiveService,
                                          final BetonQuestLoggerFactory factory, final ProfileProvider profileProvider) throws QuestException {
        this.objectiveID = objectiveID;
        this.objectiveService = objectiveService;
        this.actionProcessor = actionProcessor;
        this.conditionProcessor = conditionProcessor;
        this.profileProvider = profileProvider;
        this.logger = factory.create(DefaultObjectiveFactoryService.class);
        this.properties = new DefaultObjectiveProperties(this.logger);
        this.questExceptionHandler = new QuestExceptionHandler(objectiveID.getPackage(), this.logger, objectiveID.getFull());
        this.objectiveServiceData = parseObjectiveData(objectiveID.getInstruction());
        this.objectiveData = new ProfileKeyMap<>(profileProvider);
        this.defaultDataSupplier = profile -> "";
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
        return objectiveService.request(eventClass).source(objectiveID);
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
    public ProfileProvider getProfileProvider() {
        return profileProvider;
    }

    @Override
    public Map<Profile, String> getData() {
        return objectiveData;
    }

    @Override
    public void updateData(final Profile profile) {
        final BetonQuest plugin = BetonQuest.getInstance();
        final Saver saver = plugin.getSaver();
        final String freshData = objectiveData.get(profile);
        saver.add(new Saver.Record(UpdateType.REMOVE_OBJECTIVES, profile.getProfileUUID().toString(), objectiveID.getFull()));
        saver.add(new Saver.Record(UpdateType.ADD_OBJECTIVES, profile.getProfileUUID().toString(), objectiveID.getFull(), freshData));
        final QuestDataUpdateEvent event = new QuestDataUpdateEvent(profile, objectiveID, freshData);
        plugin.getServer().getScheduler().runTask(plugin, event::callEvent);
        if (profile.getOnlineProfile().isPresent()) {
            plugin.getPlayerDataStorage().get(profile).getJournal().update();
        }
    }

    @Override
    public String getDefaultData(final Profile profile) throws QuestException {
        return defaultDataSupplier.apply(profile);
    }

    @Override
    public void setDefaultData(final QuestFunction<Profile, String> supplier) {
        this.defaultDataSupplier = supplier;
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
    public ObjectiveProperties getProperties() {
        return properties;
    }

    @Override
    public boolean checkConditions(@Nullable final Profile profile) throws QuestException {
        getLogger().debug("Checking conditions for objective '%s' and profile '%s'".formatted(objectiveID, profile));
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
        getLogger().debug("Calling actions [%s] for objective '%s' and profile '%s'"
                .formatted(String.join(",", events.stream().map(DefaultIdentifier::toString).toList()), objectiveID, profile));
        actionProcessor.executes(profile, events);
    }

    @Override
    public boolean containsProfile(final Profile profile) {
        return objectiveData.containsKey(profile);
    }

    @Override
    public void complete(final Profile profile) {
        try {
            objectiveService.stop(objectiveID, profile, ObjectiveState.COMPLETED);
        } catch (final QuestException e) {
            logger.error("Could not stop objective '%s' for profile '%s': %s".formatted(getObjectiveID(), profile, e.getMessage()), e);
            return;
        }
        final PlayerData playerData = BetonQuest.getInstance().getPlayerDataStorage().get(profile);
        final QuestPackage questPackage = objectiveID.getPackage();
        playerData.removeRawObjective(objectiveID);
        checkForPersistence(profile, playerData);
        logger.debug("Objective '%s' has been completed for '%s', firing actions.".formatted(objectiveID, profile));
        try {
            callActions(profile);
        } catch (final QuestException e) {
            logger.warn(questPackage, "Error while firing actions in objective '%s' for profile '%s': %s".formatted(objectiveID, profile, e.getMessage()), e);
        }
        logger.debug(questPackage, "Firing actions in objective '%s' for profile '%s' finished".formatted(objectiveID, profile));
    }

    private void checkForPersistence(final Profile profile, final PlayerData playerData) {
        boolean persistent;
        try {
            persistent = objectiveServiceData.isPersistent(profile);
        } catch (final QuestException e) {
            logger.error("Could not get persistent flag of objective '%s' for profile '%s': %s".formatted(objectiveID, profile, e.getMessage()), e);
            persistent = false;
        }
        if (persistent) {
            try {
                final String defaultDataInstruction = getDefaultData(profile);
                playerData.addRawObjective(objectiveID, defaultDataInstruction);
                playerData.addObjToDB(objectiveID, defaultDataInstruction);
                objectiveService.start(objectiveID, profile, defaultDataInstruction, ObjectiveState.NEW);
                logger.debug("Persistent objective '%s' has been re-created for '%s'.".formatted(objectiveID, profile));
            } catch (final QuestException e) {
                logger.warn("Could not re-create persistent objective '%s' for profile '%s': The objective instruction could not be resolved: %s"
                        .formatted(objectiveID, profile, e.getMessage()), e);
            }
        }
    }
}
