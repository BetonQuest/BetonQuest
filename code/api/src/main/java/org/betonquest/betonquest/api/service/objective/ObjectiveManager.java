package org.betonquest.betonquest.api.service.objective;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.jetbrains.annotations.Contract;

import java.util.List;

/**
 * The ObjectiveManager is responsible for managing objectives loaded by BetonQuest.
 * <p>
 * Each {@link Objective} is uniquely identified by an {@link ObjectiveIdentifier}, which consists
 * of the user-defined name in the configuration and the {@link QuestPackage} it belongs to.
 * <p>
 * A profile cannot have more than one active objective with the same {@link ObjectiveIdentifier} at a time.
 * A single {@link Objective} instance is shared among all profiles that have the same objective active.
 *
 * @since 3.0.0
 */
public interface ObjectiveManager {

    /**
     * Starts a new objective for the specified {@link Profile} using the objective's default starting state.
     * <p>
     * If the objective with this identifier is already running for the specified profile, this method does nothing.
     *
     * @param profile             the profile to start the objective for
     * @param objectiveIdentifier the identifier of the objective to start
     * @since 3.0.0
     */
    void start(Profile profile, ObjectiveIdentifier objectiveIdentifier);

    /**
     * Starts a new objective for the specified {@link Profile} using the specified starting state.
     * <p>
     * If the objective with this identifier is already running for the specified profile, this method does nothing.
     *
     * @param profile             the profile to start the objective for
     * @param objectiveIdentifier the identifier of the objective to start
     * @param startingState       the starting state for the objective
     * @since 3.0.0
     */
    void start(Profile profile, ObjectiveIdentifier objectiveIdentifier, String startingState);

    /**
     * Pauses the objective for the specified {@link Profile}.
     * <p>
     * If the objective with this identifier is not running for the specified profile, this method does nothing.
     *
     * @param profile             the profile to pause the objective for
     * @param objectiveIdentifier the identifier of the objective to pause
     * @since 3.0.0
     */
    void pause(Profile profile, ObjectiveIdentifier objectiveIdentifier);

    /**
     * Cancels the objective for the specified {@link Profile}.
     * <p>
     * If the objective with this identifier is not running for the specified profile, this method does nothing.
     *
     * @param profile             the profile to cancel the objective for
     * @param objectiveIdentifier the identifier of the objective to cancel
     * @since 3.0.0
     */
    void cancel(Profile profile, ObjectiveIdentifier objectiveIdentifier);

    /**
     * Returns a list of all active and running {@link Objective}s for the specified {@link Profile}.
     * <p>
     * {@link Objective} instances are shared among all profiles with the same active objective.
     *
     * @param profile the profile to retrieve the objectives for
     * @return a list of active and running objectives
     * @since 3.0.0
     */
    @Contract(pure = true)
    List<Objective> getForProfile(Profile profile);

    /**
     * Returns a loaded {@link Objective} by its {@link ObjectiveIdentifier}.
     * <p>
     * {@link Objective} instances are shared among all profiles with the same active objective.
     *
     * @param objectiveIdentifier the identifier of the objective to retrieve
     * @return the objective instance
     * @throws QuestException if no objective exists with the given identifier
     * @since 3.0.0
     */
    @Contract(pure = true)
    Objective getObjective(ObjectiveIdentifier objectiveIdentifier) throws QuestException;
}
