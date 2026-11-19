package org.betonquest.betonquest.api.service.condition;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * The ConditionManager is responsible for evaluating conditions loaded by BetonQuest.
 * <p>
 * Each condition is uniquely identified by a {@link ConditionIdentifier}, which consists of the user-defined name
 * in the configuration and the {@link QuestPackage} it belongs to.
 * <p>
 * Evaluating a condition returns {@code true} if it is met and {@code false} otherwise.
 * Evaluation always involves the condition itself and any related conditions as a side effect.
 * For example, evaluating a conjunction of conditions will test each condition until the final result
 * can be determined.
 *
 * @since 3.0.0
 */
public interface ConditionManager {

    /**
     * Evaluates a condition for the optionally specified {@link Profile}.
     * <p>
     * The specified profile is used to resolve placeholders in the condition's instructions and any side effects.
     * If no profile is specified, placeholders requiring one will cause the evaluation to fail.
     *
     * @param profile             the profile to evaluate the condition for, or {@code null} if no profile is involved
     * @param conditionIdentifier the identifier of the condition to evaluate
     * @return {@code true} if the condition is met, {@code false} otherwise
     * @since 3.0.0
     */
    boolean test(@Nullable Profile profile, ConditionIdentifier conditionIdentifier);

    /**
     * Evaluates multiple conditions for the optionally specified {@link Profile} conjunctively.
     * <p>
     * The order of evaluation is not guaranteed, although evaluating a {@link Collection} typically retains
     * the order if one is present. The most common reason for order deviation is synchronization
     * requiring a wait for the server's main thread.
     * <p>
     * Since a conjunction fails as soon as a single literal is {@code false}, evaluation stops once
     * one condition evaluates to {@code false}.
     * <p>
     * The specified profile is used to resolve placeholders in the conditions' instructions and any side effects.
     * If no profile is specified, placeholders requiring one will cause the evaluation to fail.
     *
     * @param profile              the profile to evaluate the conditions for, or {@code null} if no profile is involved
     * @param conditionIdentifiers the identifiers of the conditions to evaluate
     * @return {@code true} if all conditions are met, {@code false} otherwise
     * @since 3.0.0
     */
    boolean testAll(@Nullable Profile profile, Collection<ConditionIdentifier> conditionIdentifiers);

    /**
     * Evaluates multiple conditions for the optionally specified {@link Profile} disjunctively.
     * <p>
     * The order of evaluation is not guaranteed, although evaluating a {@link Collection} typically retains
     * the order if one is present. The most common reason for order deviation is synchronization
     * requiring a wait for the server's main thread.
     * <p>
     * Since a disjunction succeeds as soon as a single literal is {@code true}, evaluation stops once
     * one condition evaluates to {@code true}.
     * <p>
     * The specified profile is used to resolve placeholders in the conditions' instructions and any side effects.
     * If no profile is specified, placeholders requiring one will cause the evaluation to fail.
     *
     * @param profile              the profile to evaluate the conditions for, or {@code null} if no profile is involved
     * @param conditionIdentifiers the identifiers of the conditions to evaluate
     * @return {@code true} if any condition is met, {@code false} otherwise
     * @since 3.0.0
     */
    boolean testAny(@Nullable Profile profile, Collection<ConditionIdentifier> conditionIdentifiers);

    /**
     * Evaluates multiple conditions for the optionally specified {@link Profile} against a {@link TestStrategy}.
     * <p>
     * The order of evaluation is not guaranteed, although evaluating a {@link Collection} typically retains
     * the order if one is present. The most common reason for order deviation is synchronization
     * requiring a wait for the server's main thread.
     * <p>
     * Since a {@link TestStrategy} can yield a result early, not all conditions might be evaluated.
     * <p>
     * The specified profile is used to resolve placeholders in the conditions' instructions and any side effects.
     * If no profile is specified, placeholders requiring one will cause the evaluation to fail.
     *
     * @param profile              the profile to evaluate the conditions for, or {@code null} if no profile is involved
     * @param conditionIdentifiers the identifiers of the conditions to evaluate
     * @param testStrategy         the strategy to test against
     * @return {@code true} if the conditions match the test strategy, {@code false} otherwise
     * @since 3.2.0
     */
    boolean test(@Nullable Profile profile, Collection<ConditionIdentifier> conditionIdentifiers, TestStrategy testStrategy);
}
