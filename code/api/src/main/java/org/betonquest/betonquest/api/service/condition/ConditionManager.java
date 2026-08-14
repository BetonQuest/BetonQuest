package org.betonquest.betonquest.api.service.condition;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

/**
 * The ConditionManager is responsible for evaluating conditions that are loaded by BetonQuest.
 * <br> <br>
 * Each condition is uniquely identified by a {@link ConditionIdentifier} which consists of the user-defined name in the
 * configuration as well as the {@link QuestPackage} the condition belongs to.
 * Evaluating a condition will return true if the condition is met and false otherwise.
 * Evaluating a condition will always evaluate itself and all related conditions as a side effect.
 * For example, evaluating a condition defining a conjunction of conditions will run all conditions until the result
 * can be determined.
 *
 * @since 3.0.0
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
     * @since 3.0.0
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
     * @since 3.0.0
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
     * @since 3.0.0
     */
    boolean testAny(@Nullable Profile profile, Collection<ConditionIdentifier> conditionIdentifiers);

    /**
     * Evaluates multiple conditions for the optionally specified {@link Profile} against a {@link TestStrategy}.
     * <br> <br>
     * The order of evaluation is not guaranteed, however evaluating a {@link Collection}
     * <i>usually</i> retains the order if one is present.
     * The most common reason for breaking the order is synchronization requiring to wait for the servers main thread.
     * Since a {@link TestStrategy} can early evaluate to a result, not all conditions may be evaluated.
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
     * @param testStrategy         the strategy to test against
     * @return whether the conditions meet the test strategy
     * @since 3.2.0
     */
    boolean test(@Nullable Profile profile, Collection<ConditionIdentifier> conditionIdentifiers, TestStrategy testStrategy);

    /**
     * Strategy to check if a certain combination of condition evaluations is valid.
     *
     * @since 3.2.0
     */
    @FunctionalInterface
    interface TestStrategy {

        /**
         * Checks the given condition evaluations for a result.
         * <br> <br>
         * When the result is definit, an optional with the result is returned.
         * After that no more condition may be evaluated.
         * <br>
         * Otherwise, an empty optional will be returned and the next condition, if one is remaining,
         * it will be evaluated and the check repeated.
         * <br> <br>
         * If the result is still empty even when no condition is remaining to evaluate,
         * it will be interpreted as {@code false}.
         *
         * @param positive  the amount of conditions which evaluated to {@code true}
         * @param negative  the amount of conditions which evaluated to {@code false}
         * @param remaining the amount of conditions which are not evaluated
         * @return the result of the test, or an empty optional when it is indefinit
         * @since 3.2.0
         */
        Optional<Boolean> getResult(int positive, int negative, int remaining);
    }
}
