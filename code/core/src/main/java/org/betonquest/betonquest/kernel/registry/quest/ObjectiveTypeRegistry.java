package org.betonquest.betonquest.kernel.registry.quest;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.kernel.registry.FactoryTypeRegistry;

/**
 * Stores the Objectives that can be used in BetonQuest.
 */
public class ObjectiveTypeRegistry extends FactoryTypeRegistry<Objective> {

    /**
     * Create a new Objective registry.
     *
     * @param log the logger that will be used for logging
     */
    public ObjectiveTypeRegistry(final BetonQuestLogger log) {
        super(log, "objective");
    }
}
