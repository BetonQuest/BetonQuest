package org.betonquest.betonquest.quest.placeholder.objective;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;

/**
 * Resolves to a specified property of an objective.
 */
public class ObjectivePropertyPlaceholder implements PlayerPlaceholder {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * The objective ID.
     */
    private final ObjectiveID objectiveID;

    /**
     * The property name.
     */
    private final String propertyName;

    /**
     * Create a new objective property placeholder.
     *
     * @param questTypeApi the Quest Type API
     * @param objectiveID  The objective ID.
     * @param propertyName The property name.
     */
    public ObjectivePropertyPlaceholder(final QuestTypeApi questTypeApi, final ObjectiveID objectiveID, final String propertyName) {
        this.questTypeApi = questTypeApi;
        this.objectiveID = objectiveID;
        this.propertyName = propertyName;
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        final Objective objective = questTypeApi.getObjective(objectiveID);
        final ObjectiveService service = objective.getService();
        if (service.containsProfile(profile)) {
            return service.getProperties().getProperty(propertyName, profile);
        }
        return "";
    }
}
