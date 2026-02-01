package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.Objective;

import java.util.List;

/**
 * The ObjectiveManager is responsible for managing objectives loaded by BetonQuest.
 * <br> <br>
 * Each {@link Objective} is uniquely identified by an {@link ObjectiveIdentifier} which consists
 * of the user-defined name in the configuration as well as the {@link QuestPackage} the {@link Objective} belongs to.
 * No profile may have more than one {@link Objective} with the same {@link ObjectiveIdentifier} at the same time.
 * There is only a single {@link Objective} instance shared among all profiles having the same {@link Objective} active.
 */
public interface ObjectiveManager {

    /**
     * Starts a new objective by its {@link ObjectiveIdentifier} for the specified {@link Profile}
     * with the objective's default starting state.
     * <br> <br>
     * If the objective to this identifier is already running for the specified profile, this method will do nothing.
     *
     * @param profile             the profile to start the objective for
     * @param objectiveIdentifier the identifier of the objective to start
     */
    void start(Profile profile, ObjectiveIdentifier objectiveIdentifier);

    /**
     * Starts a new objective by its {@link ObjectiveIdentifier} for the specified {@link Profile}
     * with the specified starting state.
     * <br> <br>
     * If the objective to this identifier is already running for the specified profile, this method will do nothing.
     *
     * @param profile             the profile to start the objective for
     * @param objectiveIdentifier the identifier of the objective to start
     * @param startingState       the starting state of the objective
     */
    void start(Profile profile, ObjectiveIdentifier objectiveIdentifier, String startingState);

    /**
     * Pauses the objective by its {@link ObjectiveIdentifier} for the specified {@link Profile}.
     * <br> <br>
     * If the objective to this identifier is not running for the specified profile, this method will do nothing.
     *
     * @param profile             the profile to pause the objective for
     * @param objectiveIdentifier the identifier of the objective to pause
     */
    void pause(Profile profile, ObjectiveIdentifier objectiveIdentifier);

    /**
     * Cancels the objective by its {@link ObjectiveIdentifier} for the specified {@link Profile}.
     * <br> <br>
     * If the objective to this identifier is not running for the specified profile, this method will do nothing.
     *
     * @param profile             the profile to cancel the objective for
     * @param objectiveIdentifier the identifier of the objective to cancel
     */
    void cancel(Profile profile, ObjectiveIdentifier objectiveIdentifier);

    /**
     * Returns all active and running {@link Objective}s for the specified {@link Profile}.
     * <br> <br>
     * {@link Objective} instances are shared among all profiles having the same objective active
     * and are uniquely identified by their {@link ObjectiveIdentifier}.
     *
     * @param profile the profile to get the objectives for
     * @return a list of active and running objectives
     */
    List<Objective> getForProfile(Profile profile);

    /**
     * Gets a loaded {@link Objective} by its {@link ObjectiveIdentifier}.
     * <br> <br>
     * {@link Objective} instances are shared among all profiles having the same objective active.
     *
     * @param objectiveIdentifier the identifier of the objective to get
     * @return the objective with the specified identifier
     * @throws QuestException if there is no objective with the given identifier
     */
    Objective getObjective(ObjectiveIdentifier objectiveIdentifier) throws QuestException;
}
