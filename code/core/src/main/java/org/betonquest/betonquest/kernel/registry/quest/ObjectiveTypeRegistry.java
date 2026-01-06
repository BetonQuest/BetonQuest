package org.betonquest.betonquest.kernel.registry.quest;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.ObjectiveRegistry;
import org.betonquest.betonquest.kernel.registry.FactoryRegistry;

/**
 * Stores the Objectives that can be used in BetonQuest.
 */
public class ObjectiveTypeRegistry extends FactoryRegistry<ObjectiveFactory> implements ObjectiveRegistry {

    /**
     * Create a new Objective registry.
     *
     * @param log the logger that will be used for logging
     */
    public ObjectiveTypeRegistry(final BetonQuestLogger log) {
        super(log, "objective");
    }
}
