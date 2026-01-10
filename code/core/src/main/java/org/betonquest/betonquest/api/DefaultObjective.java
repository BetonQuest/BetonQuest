package org.betonquest.betonquest.api;

import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;

/**
 * Optional superclass for all objectives instead of implementing {@link Objective} directly.
 */
public abstract class DefaultObjective implements Objective {

    /**
     * The {@link ObjectiveService} for this objective.
     */
    private final ObjectiveService service;

    /**
     * Creates a new instance of the objective.
     *
     * @param service the {@link ObjectiveService} for this objective
     * @throws QuestException if the syntax is wrong or any error happens while parsing
     */
    public DefaultObjective(final ObjectiveService service) throws QuestException {
        this.service = service;
    }

    @Override
    public ObjectiveService getService() {
        return service;
    }
}
