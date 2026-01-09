package org.betonquest.betonquest.api;

import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;

/**
 * Optional superclass for all objectives instead of implementing {@link Objective} directly.
 */
public abstract class DefaultObjective implements Objective {

    /**
     * The {@link ObjectiveFactoryService} for this objective.
     */
    private final ObjectiveFactoryService service;

    /**
     * Creates a new instance of the objective.
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
}
