package org.betonquest.betonquest.compatibility.craftengine;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

public class CraftEngineIntegratorFactory implements IntegratorFactory {

    @Override
    public Integrator getIntegrator() {
        return new CraftEngineIntegrator();
    }
}
