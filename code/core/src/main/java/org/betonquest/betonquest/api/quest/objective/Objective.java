package org.betonquest.betonquest.api.quest.objective;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveService;
import org.betonquest.betonquest.lib.logger.QuestExceptionHandler;

/**
 * Any objective should implement this interface.
 */
@FunctionalInterface
public interface Objective {

    /**
     * Should return the {@link ObjectiveService} for this objective.
     *
     * @return the objective service
     */
    ObjectiveService getService();

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

    /**
     * Should return the exception handler for this objective.
     *
     * @return the exception handler
     */
    default QuestExceptionHandler getExceptionHandler() {
        return getService().getExceptionHandler();
    }

    /**
     * Should return the logger for this objective.
     *
     * @return the logger
     */
    default BetonQuestLogger getLogger() {
        return getService().getLogger();
    }

    /**
     * This method will be called when the objective is closed when shutting down or reloading.
     */
    default void close() {
        // Empty
    }
}
