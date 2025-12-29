package org.betonquest.betonquest.api;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.bukkit.event.PlayerObjectiveChangeEvent;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveData;
import org.betonquest.betonquest.api.quest.objective.ObjectiveDataFactory;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveState;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.lib.logger.QuestExceptionHandler;
import org.betonquest.betonquest.lib.profile.ProfileKeyMap;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Superclass for all objectives. You need to extend it in order to create new custom objectives.
 */
@SuppressWarnings("PMD.TooManyMethods")
public abstract class DefaultObjective implements PropertyHolder {

    /**
     * The factory for the default Objective Data.
     */
    private static final ObjectiveDataFactory DATA_FACTORY = ObjectiveData::new;

    /**
     * Profile provider to get profiles from players.
     */
    protected final ProfileProvider profileProvider;

    /**
     * Interval in which progress should be notified.
     */
    protected final int notifyInterval;

    /**
     * If progress should be displayed.
     */
    protected final boolean notify;

    /**
     * Exception Handler to not spam the log.
     */
    protected final QuestExceptionHandler qeHandler;

    /**
     * Contains all data objects of the profiles with this objective active.
     */
    protected final Map<Profile, ObjectiveData> dataMap;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Should be set to the data class used to hold the objective specific information.
     */
    private final ObjectiveDataFactory templateFactory;

    /**
     * Conditions to count progress.
     */
    private final Argument<List<ConditionID>> conditions;

    /**
     * Events to fire on completion.
     */
    private final Argument<List<EventID>> events;

    /**
     * If the objective should start again on completion.
     */
    private final FlagArgument<Boolean> persistent;

    /**
     * Instruction of this.
     */
    protected Instruction instruction;

    /**
     * Creates a new instance of the objective.
     * <p>
     * <b>Do not register listeners here!</b>
     * This is done automatically after creation.
     *
     * @param instruction Instruction object representing the objective
     * @throws QuestException if the syntax is wrong or any error happens while parsing
     */
    public DefaultObjective(final Instruction instruction) throws QuestException {
        this(instruction, DATA_FACTORY);
    }

    /**
     * Creates a new instance of the objective.
     * <p>
     * <b>Do not register listeners here!</b>
     * This is done automatically after creation.
     *
     * @param instruction     Instruction object representing the objective
     * @param templateFactory the factory for the objective data object
     * @throws QuestException if the syntax is wrong or any error happens while parsing
     */
    public DefaultObjective(final Instruction instruction, final ObjectiveDataFactory templateFactory) throws QuestException {
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());
        this.templateFactory = templateFactory;
        this.instruction = instruction;
        this.qeHandler = new QuestExceptionHandler(instruction.getPackage(), log, instruction.getID().getFull());
        this.profileProvider = BetonQuest.getInstance().getProfileProvider();
        this.dataMap = new ProfileKeyMap<>(profileProvider);
        persistent = instruction.bool().getFlag("persistent", true);
        events = instruction.parse(EventID::new).list().get("events", Collections.emptyList());
        conditions = instruction.parse(ConditionID::new).list().get("conditions", Collections.emptyList());
        final FlagArgument<Number> notify = instruction.number().atLeast(0).getFlag("notify", 1);
        this.notifyInterval = notify.getValue(null).orElse(0).intValue();
        this.notify = notifyInterval > 0;
    }

    /**
     * This method is called by the plugin when the objective starts for a specific profile.
     *
     * @param profile the {@link Profile} of the player
     */
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    public void start(final Profile profile) {
        //Empty
    }

    /**
     * This method is called by the plugin when the objective stop for a specific profile.
     *
     * @param profile the {@link Profile} of the player
     */
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    public void stop(final Profile profile) {
        //Empty
    }

    /**
     * This method should return the default data instruction for the objective,
     * ready to be parsed by the ObjectiveData class.
     *
     * @param profile the {@link Profile} to parse the instruction for
     * @return the default data instruction string
     * @throws QuestException when values could not be resolved for the profile
     */
    public abstract String getDefaultDataInstruction(Profile profile) throws QuestException;

    /**
     * This method fires events for the objective and removes it from the profile's
     * list of active objectives. Use it when you detect that the objective has
     * been completed. It deletes the objective using delete() method.
     *
     * @param profile the {@link Profile} for which the objective is to be completed
     */
    public final void completeObjective(final Profile profile) {
        completeObjectiveForPlayer(profile);
        final PlayerData playerData = BetonQuest.getInstance().getPlayerDataStorage().get(profile);
        final ObjectiveID objectiveID = (ObjectiveID) instruction.getID();
        playerData.removeRawObjective(objectiveID);
        try {
            if (persistent.getValue(profile).orElse(false)) {
                try {
                    final String defaultDataInstruction = getDefaultDataInstruction(profile);
                    playerData.addRawObjective(objectiveID, defaultDataInstruction);
                    playerData.addObjToDB(objectiveID, defaultDataInstruction);
                    createObjectiveForPlayer(profile, defaultDataInstruction);
                } catch (final QuestException e) {
                    log.warn(instruction.getPackage(), "Could not re-create persistent Objective for '" + instruction.getID()
                            + "' for '" + profile + "' objective: The Objective Instruction could not be resolved: " + e.getMessage(), e);
                }
            }
        } catch (final QuestException e) {
            log.error(instruction.getPackage(), "Could not get persistent flag for '" + instruction.getID() + "' for '" + profile + "' objective: " + e.getMessage(), e);
        }
        log.debug(instruction.getPackage(),
                "Objective '" + instruction.getID() + "' has been completed for " + profile + ", firing events.");
        try {
            BetonQuest.getInstance().getQuestTypeApi().events(profile, events.getValue(profile));
        } catch (final QuestException e) {
            log.warn(instruction.getPackage(), "Error while firing events in objective '" + instruction.getID()
                    + "' for " + profile + ": " + e.getMessage(), e);
        }
        log.debug(instruction.getPackage(),
                "Firing events in objective '" + instruction.getID() + "' for " + profile + " finished");
    }

    /**
     * Checks if all conditions has been met. Use it when the player has done
     * something that modifies data (e.g. killing zombies). If conditions are
     * met, you can safely modify the data.
     *
     * @param profile the {@link Profile} for which the conditions are to be checked
     * @return if all conditions of this objective has been met
     */
    public final boolean checkConditions(final Profile profile) {
        log.debug(instruction.getPackage(), "Condition check in '" + instruction.getID()
                + "' objective for " + profile);
        try {
            return BetonQuest.getInstance().getQuestTypeApi().conditions(profile, conditions.getValue(profile));
        } catch (final QuestException e) {
            log.warn(instruction.getPackage(),
                    "Error while checking conditions in objective '" + instruction.getID()
                            + "' for " + profile + ": " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Adds this new objective to the profile. Also updates the database with the
     * objective.
     *
     * @param profile the {@link Profile} for which the objective is to be added
     */
    public final void newPlayer(final Profile profile) {
        try {
            final String defaultInstruction = getDefaultDataInstruction(profile);
            createObjectiveForPlayer(profile, defaultInstruction);
            BetonQuest.getInstance().getPlayerDataStorage().get(profile).addObjToDB((ObjectiveID) instruction.getID(), defaultInstruction);
        } catch (final QuestException e) {
            log.warn(instruction.getPackage(), "Could not create new Objective for '" + instruction.getID()
                    + "' for '" + profile + "' objective: The Objective Instruction could not be resolved: " + e.getMessage(), e);
        }
    }

    /**
     * Starts a new objective for the profile.
     *
     * @param profile           the {@link Profile} for which the objective is to be started
     * @param instructionString the objective data instruction
     * @see #resumeObjectiveForPlayer(Profile, String)
     */
    public final void createObjectiveForPlayer(final Profile profile, final String instructionString) {
        startObjective(profile, instructionString, ObjectiveState.NEW);
    }

    /**
     * Resumes a paused objective for the profile.
     *
     * @param profile           the {@link Profile} for which the objective is to be resumed
     * @param instructionString the objective data instruction
     * @see #createObjectiveForPlayer(Profile, String)
     */
    public final void resumeObjectiveForPlayer(final Profile profile, final String instructionString) {
        startObjective(profile, instructionString, ObjectiveState.PAUSED);
    }

    /**
     * Start an objective for the profile. This lower level method allows to set the previous state directly. If possible
     * prefer {@link #createObjectiveForPlayer(Profile, String)} and {@link #resumeObjectiveForPlayer(Profile, String)}.
     *
     * @param profile           the {@link Profile} for which the objective is to be started
     * @param instructionString the objective data instruction
     * @param previousState     the objective's previous state
     */
    @SuppressWarnings("PMD.AvoidSynchronizedStatement")
    public final void startObjective(final Profile profile, final String instructionString, final ObjectiveState previousState) {
        synchronized (this) {
            try {
                final ObjectiveData data = templateFactory.create(instructionString, profile, (ObjectiveID) instruction.getID());
                runObjectiveChangeEvent(profile, previousState, ObjectiveState.ACTIVE);
                dataMap.put(profile, data);
                start(profile);
            } catch (final QuestException exception) {
                log.warn(instruction.getPackage(), "Error while loading " + instruction.getID() + " objective data for "
                        + profile + ": " + exception.getMessage(), exception);
            }
        }
    }

    /**
     * Complete an active objective for the profile. It will only remove it from the profile and not run any completion
     * events, run {@link #completeObjective(Profile)} instead! It does also not remove it from the database.
     *
     * @param profile the {@link Profile} for which the objective is to be completed
     * @see #cancelObjectiveForPlayer(Profile)
     * @see #pauseObjectiveForPlayer(Profile)
     */
    public final void completeObjectiveForPlayer(final Profile profile) {
        stopObjective(profile, ObjectiveState.COMPLETED);
    }

    /**
     * Cancel an active objective for the profile. It will only remove it from the profile and not remove it from the
     * database.
     *
     * @param profile the {@link Profile} for which the objective is to be cancelled
     * @see #completeObjectiveForPlayer(Profile)
     * @see #pauseObjectiveForPlayer(Profile)
     */
    public final void cancelObjectiveForPlayer(final Profile profile) {
        stopObjective(profile, ObjectiveState.CANCELED);
    }

    /**
     * Pause an active objective for the profile.
     *
     * @param profile the {@link Profile} for which the objective is to be paused
     * @see #completeObjectiveForPlayer(Profile)
     * @see #cancelObjectiveForPlayer(Profile)
     */
    public final void pauseObjectiveForPlayer(final Profile profile) {
        stopObjective(profile, ObjectiveState.PAUSED);
    }

    /**
     * Stops an objective for the profile. This lower level method allows to set the previous state directly. If possible
     * prefer {@link #completeObjectiveForPlayer(Profile)}, {@link #cancelObjectiveForPlayer(Profile)} and
     * {@link #pauseObjectiveForPlayer(Profile)}.
     *
     * @param profile  the {@link Profile} for which the objective is to be stopped
     * @param newState the objective's new state
     */
    @SuppressWarnings("PMD.AvoidSynchronizedStatement")
    public final void stopObjective(final Profile profile, final ObjectiveState newState) {
        synchronized (this) {
            runObjectiveChangeEvent(profile, ObjectiveState.ACTIVE, newState);
            stop(profile);
            dataMap.remove(profile);
        }
    }

    private void runObjectiveChangeEvent(final Profile profile, final ObjectiveState previousState, final ObjectiveState newState) {
        final boolean isAsync = !BetonQuest.getInstance().getServer().isPrimaryThread();
        new PlayerObjectiveChangeEvent(profile, isAsync, this, instruction.getID(), newState, previousState).callEvent();
    }

    /**
     * Checks if the profile has this objective.
     *
     * @param profile the {@link Profile} to check
     * @return true if the profile has this objective
     */
    public final boolean containsPlayer(final Profile profile) {
        return dataMap.containsKey(profile);
    }

    /**
     * Returns the data of the specified profile.
     *
     * @param profile the {@link Profile} to get the data for
     * @return the data string for this objective or null if there is no data
     */
    @Nullable
    public final String getData(final Profile profile) {
        final ObjectiveData data = dataMap.get(profile);
        if (data == null) {
            return null;
        }
        return data.toString();
    }

    /**
     * Returns the label of this objective. Don't worry about it, it's only used
     * by the rest of BetonQuest's logic.
     *
     * @return the label of the objective
     */
    public final String getLabel() {
        return instruction.getID().getFull();
    }

    /**
     * Sets the label of this objective. Don't worry about it, it's only used by
     * the rest of BetonQuest's logic.
     *
     * @param newID new ID of the objective
     */
    public void setLabel(final ObjectiveID newID) {
        instruction = instruction.copy(newID);
    }

    /**
     * Should be called at the end of the use of this objective, for example
     * when reloading the plugin. It will save all profile data to their "inactive" map.
     */
    public void close() {
        for (final Map.Entry<Profile, ObjectiveData> entry : dataMap.entrySet()) {
            final Profile profile = entry.getKey();
            stop(profile);
            BetonQuest.getInstance().getPlayerDataStorage().get(profile).addRawObjective((ObjectiveID) instruction.getID(),
                    entry.getValue().toString());
        }
    }
}
