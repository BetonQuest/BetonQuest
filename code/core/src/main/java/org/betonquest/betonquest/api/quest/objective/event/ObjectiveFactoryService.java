package org.betonquest.betonquest.api.quest.objective.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.logger.LogSource;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

/**
 * Provides services for objective creation and event subscriptions.
 */
public interface ObjectiveFactoryService {

    /**
     * Requests a new event subscription using an {@link EventServiceSubscriptionBuilder}.
     * <br>
     * Calling this in the context of an {@link ObjectiveFactory} will cause
     * {@link EventServiceSubscriptionBuilder#source(LogSource)} to be called
     * with the objective's source before returning.
     * <br>
     * The request may be completed in one chain of calls requiring at least a handler and ending with
     * {@link EventServiceSubscriptionBuilder#subscribe(boolean)}.
     *
     * @param eventClass the event class to subscribe to
     * @param <T>        the event type
     * @return a new {@link EventServiceSubscriptionBuilder} for the requested event
     */
    <T extends Event> EventServiceSubscriptionBuilder<T> request(Class<T> eventClass);

    /**
     * Do not use this method directly. It is used for internal logic.
     * <br>
     * Renames the objective.
     *
     * @param newObjectiveID the new objective ID
     */
    void renameObjective(ObjectiveID newObjectiveID);

    /**
     * Retrieves the objective ID.
     *
     * @return the objective ID
     */
    ObjectiveID getObjectiveID();

    /**
     * Retrieves the service data provider containing all additional information about the objective.
     *
     * @return the service data provider
     */
    ObjectiveServiceDataProvider getServiceDataProvider();

    /**
     * Checks if the objective conditions are met.
     *
     * @param profile the profile to check conditions for
     * @return if the conditions are met
     * @throws QuestException if argument resolving for the profile fails
     */
    boolean checkConditions(@Nullable Profile profile) throws QuestException;

    /**
     * Executes all objective actions for the given profile.
     *
     * @param profile the profile to execute actions for
     * @throws QuestException if argument resolving for the profile fails
     */
    void callActions(@Nullable Profile profile) throws QuestException;
}
