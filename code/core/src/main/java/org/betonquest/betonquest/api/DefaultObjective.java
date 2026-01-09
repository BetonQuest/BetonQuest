package org.betonquest.betonquest.api;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Superclass for all objectives. You need to extend it in order to create new custom objectives.
 */
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
