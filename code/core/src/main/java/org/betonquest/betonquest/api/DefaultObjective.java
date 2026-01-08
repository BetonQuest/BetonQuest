package org.betonquest.betonquest.api;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.bukkit.event.PlayerObjectiveChangeEvent;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveState;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.betonquest.betonquest.database.PlayerData;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Superclass for all objectives. You need to extend it in order to create new custom objectives.
 */
@SuppressWarnings("PMD.TooManyMethods")
public abstract class DefaultObjective implements Objective {

    /**
     * The {@link ObjectiveFactoryService} for this objective.
     */
    private final ObjectiveFactoryService service;

    /**
     * Creates a new instance of the objective.
     * <p>
     * <b>Do not register listeners here!</b>
     * This is done automatically after creation.
     *
     * @param service the {@link ObjectiveFactoryService} for this objective
     * @throws QuestException if the syntax is wrong or any error happens while parsing
     */
    public DefaultObjective(final ObjectiveFactoryService service) throws QuestException {
        this.service = service;
    }

    @Override
    public ObjectiveFactoryService getService() {
        return service;
    }

    /**
     * Whether this objective has notifications enabled for a profile.
     *
     * @param profile the profile to check
     * @return if notifications are enabled for the profile
     */
    protected boolean hasNotify(@Nullable final Profile profile) {
        return getNotifyInterval(profile) > 0;
    }

    /**
     * Get the notification interval for a profile.
     * <br>
     * An interval of 0 means notifications are disabled.
     *
     * @param profile the profile to get the interval for
     * @return the notification interval
     */
    protected int getNotifyInterval(@Nullable final Profile profile) {
        return getExceptionHandler().handle(() -> getService().getServiceDataProvider().getNotificationInterval(profile), 0);
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
     * This method fires actions for the objective and removes it from the profile's
     * list of active objectives. Use it when you detect that the objective has
     * been completed. It deletes the objective using delete() method.
     *
     * @param profile the {@link Profile} for which the objective is to be completed
     */
    public final void completeObjective(final Profile profile) {
        completeObjectiveForPlayer(profile);
        final PlayerData playerData = BetonQuest.getInstance().getPlayerDataStorage().get(profile);
        final ObjectiveID objectiveID = getService().getObjectiveID();
        final QuestPackage questPackage = objectiveID.getPackage();
        playerData.removeRawObjective(objectiveID);
        try {
            if (getService().getServiceDataProvider().isPersistent(profile)) {
                try {
                    final String defaultDataInstruction = getDefaultDataInstruction(profile);
                    playerData.addRawObjective(objectiveID, defaultDataInstruction);
                    playerData.addObjToDB(objectiveID, defaultDataInstruction);
                    createObjectiveForPlayer(profile, defaultDataInstruction);
                } catch (final QuestException e) {
                    getLogger().warn(questPackage, "Could not re-create persistent Objective for '" + objectiveID
                            + "' for '" + profile + "' objective: The Objective Instruction could not be resolved: " + e.getMessage(), e);
                }
            }
        } catch (final QuestException e) {
            getLogger().error(questPackage, "Could not get persistent flag for '" + objectiveID + "' for '" + profile + "' objective: " + e.getMessage(), e);
        }
        getLogger().debug(questPackage,
                "Objective '" + objectiveID + "' has been completed for " + profile + ", firing actions.");
        try {
            getService().callActions(profile);
        } catch (final QuestException e) {
            getLogger().warn(questPackage, "Error while firing actions in objective '" + objectiveID
                    + "' for " + profile + ": " + e.getMessage(), e);
        }
        getLogger().debug(questPackage,
                "Firing actions in objective '" + objectiveID + "' for " + profile + " finished");
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
            BetonQuest.getInstance().getPlayerDataStorage().get(profile).addObjToDB(getObjectiveID(), defaultInstruction);
        } catch (final QuestException e) {
            getLogger().warn(getPackage(), "Could not create new Objective for '" + getObjectiveID()
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
            runObjectiveChangeEvent(profile, previousState, ObjectiveState.ACTIVE);
            getService().getData().put(profile, instructionString);
        }
    }

    /**
     * Complete an active objective for the profile. It will only remove it from the profile and not run any completion
     * actions, run {@link #completeObjective(Profile)} instead! It does also not remove it from the database.
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
            getService().getData().remove(profile);
        }
    }

    private void runObjectiveChangeEvent(final Profile profile, final ObjectiveState previousState, final ObjectiveState newState) {
        final boolean isAsync = !BetonQuest.getInstance().getServer().isPrimaryThread();
        new PlayerObjectiveChangeEvent(profile, isAsync, this, getService().getObjectiveID(), newState, previousState).callEvent();
    }

    /**
     * Returns the label of this objective. Don't worry about it, it's only used
     * by the rest of BetonQuest's logic.
     *
     * @return the label of the objective
     */
    public final String getLabel() {
        return getService().getObjectiveID().getFull();
    }

    /**
     * Should be called at the end of the use of this objective, for example
     * when reloading the plugin. It will save all profile data to their "inactive" map.
     */
    public void close() {
        for (final Map.Entry<Profile, String> entry : service.getData().entrySet()) {
            final Profile profile = entry.getKey();
            BetonQuest.getInstance().getPlayerDataStorage().get(profile).addRawObjective(getService().getObjectiveID(),
                    entry.getValue());
        }
    }
}
