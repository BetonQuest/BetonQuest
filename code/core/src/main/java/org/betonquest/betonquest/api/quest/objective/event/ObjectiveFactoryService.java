package org.betonquest.betonquest.api.quest.objective.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.lib.logger.QuestExceptionHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Provides services for objective creation and event subscriptions.
 */
public interface ObjectiveFactoryService {

    /**
     * Requests a new event subscription using an {@link EventServiceSubscriptionBuilder}.
     * <br>
     * Calling this in the context of an {@link ObjectiveFactory} will cause
     * {@link EventServiceSubscriptionBuilder#source(ObjectiveID)} to be called
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
     * Get an exception handler specificly for this objective.
     *
     * @return the exception handler
     */
    QuestExceptionHandler getExceptionHandler();

    /**
     * Get the logger specificly for this objective.
     *
     * @return the logger
     */
    BetonQuestLogger getLogger();

    /**
     * Get the general profile provider.
     *
     * @return the profile provider
     */
    ProfileProvider getProfileProvider();

    /**
     * Get the raw objective data.
     *
     * @return the objective data
     * @deprecated do not use this method. it's scheduled for removal and only exists for compatibility.
     */
    @Deprecated
    Map<Profile, String> getData();

    /**
     * Update the raw objective data with the database.
     *
     * @param profile the profile to update
     * @deprecated do not use this method. it's scheduled for removal and only exists for compatibility.
     */
    @Deprecated
    void updateData(Profile profile);

    /**
     * Get the default objective data.
     *
     * @param profile the profile to get the data for
     * @return the supplier
     * @throws QuestException when argument resolving fails
     * @deprecated do not use this method. it's scheduled for removal and only exists for compatibility.
     */
    @Deprecated
    String getDefaultData(Profile profile) throws QuestException;

    /**
     * Set the default objective data supplier.
     *
     * @param supplier the supplier to use
     * @deprecated do not use this method. it's scheduled for removal and only exists for compatibility.
     */
    @Deprecated
    void setDefaultData(QuestFunction<Profile, String> supplier);

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

    /**
     * Checks if the objective contains the given profile.
     *
     * @param profile the profile to check for
     * @return if the objective contains the profile
     */
    boolean containsProfile(Profile profile);

    /**
     * Completes the objective for the given profile.
     *
     * @param profile the profile to complete the objective for
     */
    void complete(Profile profile);
}
