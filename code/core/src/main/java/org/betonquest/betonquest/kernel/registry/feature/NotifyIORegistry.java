package org.betonquest.betonquest.kernel.registry.feature;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.kernel.registry.FactoryRegistry;
import org.betonquest.betonquest.notify.NotifyIOFactory;

/**
 * Stores the Notify IOs that can be used in BetonQuest.
 */
public class NotifyIORegistry extends FactoryRegistry<NotifyIOFactory> {

    /**
     * Create a new NotifyIO registry.
     *
     * @param log the logger that will be used for logging
     */
    public NotifyIORegistry(final BetonQuestLogger log) {
        super(log, "Notify IO");
    }
}
