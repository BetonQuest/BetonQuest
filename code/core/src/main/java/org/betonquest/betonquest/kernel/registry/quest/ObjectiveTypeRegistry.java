package org.betonquest.betonquest.kernel.registry.quest;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.kernel.registry.FactoryTypeRegistry;

/**
 * Stores the Objectives that can be used in BetonQuest.
 */
public class ObjectiveTypeRegistry extends FactoryTypeRegistry<DefaultObjective> {

    /**
     * Create a new Objective registry.
     *
     * @param log the logger that will be used for logging
     */
    public ObjectiveTypeRegistry(final BetonQuestLogger log) {
        super(log, "objective");
    }
}
