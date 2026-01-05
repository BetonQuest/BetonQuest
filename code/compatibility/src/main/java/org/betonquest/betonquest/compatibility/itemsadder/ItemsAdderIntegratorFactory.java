package org.betonquest.betonquest.compatibility.itemsadder;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

public class ItemsAdderIntegratorFactory implements IntegratorFactory {

    @Override
    public Integrator getIntegrator() {
        return new ItemsAdderIntegrator();
    }

}
