package org.betonquest.betonquest.compatibility.nexo;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

public class NexoIntegratorFactory implements IntegratorFactory {

    @Override
    public Integrator getIntegrator() {
        return new NexoIntegrator();
    }
}
