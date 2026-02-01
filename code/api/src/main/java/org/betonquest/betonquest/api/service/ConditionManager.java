package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * The ConditionManager is responsible for evaluating conditions that are loaded by BetonQuest.
 * <br> <br>
 * Each condition is uniquely identified by a {@link ConditionIdentifier} which consists of the user-defined name in the
 * configuration as well as the {@link QuestPackage} the condition belongs to.
 * Evaluating a condition will return true if the condition is met and false otherwise.
 * Evaluating a condition will always evaluate itself and all related conditions as a side effect.
 * For example, evaluating a condition defining a conjunction of conditions will run all conditions until the result
 * can be determined.
 */
public interface ConditionManager {

    /**
     * Evaluates a condition for the optionally specified {@link Profile}.
     * <br> <br>
     * The specified profile will be used to resolve any placeholders in the condition's instructions as well as in any
     * placeholders contained in evaluations of the condition's side effects.
     * <br> <br>
     * If no profile is specified, the condition will be evaluated without any profile and any related placeholders
     * will be resolved without a profile.
     * If there are placeholders requiring a profile, but none is given, the evaluation will fail.
     *
     * @param profile             the profile to evaluate the condition for or null if no profile is involved
     * @param conditionIdentifier the identifier of the condition to evaluate
     * @return whether the condition is met
     */
    boolean test(@Nullable Profile profile, ConditionIdentifier conditionIdentifier);

    /**
     * Evaluates multiple conditions for the optionally specified {@link Profile} conjunctively.
     * <br> <br>
     * The order of evaluation is not guaranteed, however evaluating a {@link Collection}
     * <i>usually</i> retains the order if one is present.
     * The most common reason for breaking the order is synchronization requiring to wait for the servers main thread.
     * Since a conjunction fails once a single literal is {@code false}, the evaluation will stop as soon as
     * one condition evaluates to {@code false}.
     * <br> <br>
     * The specified profile will be used to resolve any placeholders in the condition's instructions as well as in any
     * placeholders contained in evaluations of the condition's side effects.
     * <br> <br>
     * If no profile is specified, the conditions will be evaluated without any profile and any related placeholders
     * will be resolved without a profile.
     * If there are placeholders requiring a profile, but none is given, the evaluation will fail.
     *
     * @param profile              the profile to evaluate the conditions for or null if no profile is involved
     * @param conditionIdentifiers the identifiers of the conditions to evaluate
     * @return whether all conditions are met
     */
    boolean testAll(@Nullable Profile profile, Collection<ConditionIdentifier> conditionIdentifiers);

    /**
     * Evaluates multiple conditions for the optionally specified {@link Profile} disjunctively.
     * <br> <br>
     * The order of evaluation is not guaranteed, however evaluating a {@link Collection}
     * <i>usually</i> retains the order if one is present.
     * The most common reason for breaking the order is synchronization requiring to wait for the servers main thread.
     * Since a disjunction succeeds once a single literal is {@code true}, the evaluation will stop as soon as
     * one condition evaluates to {@code true}.
     * <br> <br>
     * The specified profile will be used to resolve any placeholders in the condition's instructions as well as in any
     * placeholders contained in evaluations of the condition's side effects.
     * <br> <br>
     * If no profile is specified, the conditions will be evaluated without any profile and any related placeholders
     * will be resolved without a profile.
     * If there are placeholders requiring a profile, but none is given, the evaluation will fail.
     *
     * @param profile              the profile to evaluate the conditions for or null if no profile is involved
     * @param conditionIdentifiers the identifiers of the conditions to evaluate
     * @return whether any condition is met
     */
    boolean testAny(@Nullable Profile profile, Collection<ConditionIdentifier> conditionIdentifiers);
}
