package org.betonquest.betonquest.api.quest.objective;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.QuestExceptionHandler;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;

/**
 * Any objective should implement this interface.
 *
 * @since 3.0.0
 */
@FunctionalInterface
public interface Objective {

    /**
     * Should return the {@link ObjectiveService} for this objective.
     *
     * @return the objective service
     * @since 3.0.0
     */
    ObjectiveService getService();

    /**
     * Should return the objective ID.
     *
     * @return the objective ID
     * @since 3.0.0
     */
    default ObjectiveIdentifier getObjectiveID() {
        return getService().getObjectiveID();
    }

    /**
     * Should return the package of this objective.
     *
     * @return the package
     * @since 3.0.0
     */
    default QuestPackage getPackage() {
        return getObjectiveID().getPackage();
    }

    /**
     * Should return the exception handler for this objective.
     *
     * @return the exception handler
     * @since 3.0.0
     */
    default QuestExceptionHandler getExceptionHandler() {
        return getService().getExceptionHandler();
    }

    /**
     * Should return the logger for this objective.
     *
     * @return the logger
     * @since 3.0.0
     */
    default BetonQuestLogger getLogger() {
        return getService().getLogger();
    }

    /**
     * This method will be called when the objective is closed when shutting down or reloading.
     *
     * @since 3.0.0
     */
    default void close() {
        // Empty
    }
}
