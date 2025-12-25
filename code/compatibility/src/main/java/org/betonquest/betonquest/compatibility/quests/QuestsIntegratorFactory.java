package org.betonquest.betonquest.compatibility.quests;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link QuestsIntegrator} instances.
 */
public class QuestsIntegratorFactory implements IntegratorFactory {

    /**
     * Creates a new instance of the factory.
     */
    public QuestsIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new QuestsIntegrator();
    }
}
