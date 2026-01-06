package org.betonquest.betonquest.api.quest.objective;

import org.betonquest.betonquest.api.PropertyHolder;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;

/**
 * Any objective should implement this interface.
 */
public interface Objective extends PropertyHolder {

    /**
     * Should return the {@link ObjectiveFactoryService} for this objective.
     *
     * @return the objective service
     */
    ObjectiveFactoryService getService();

    /**
     * Should return the objective ID.
     *
     * @return the objective ID
     */
    default ObjectiveID getObjectiveID() {
        return getService().getObjectiveID();
    }

    /**
     * Should return the package of this objective.
     *
     * @return the package
     */
    default QuestPackage getPackage() {
        return getObjectiveID().getPackage();
    }
}
