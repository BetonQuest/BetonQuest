package org.betonquest.betonquest.compatibility.itemsadder;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link ItemsAdderIntegrator} instances.
 */
public class ItemsAdderIntegratorFactory implements IntegratorFactory {

    /** The empty default constructor. */
    public ItemsAdderIntegratorFactory() { }

    @Override
    public Integrator getIntegrator() {
        return new ItemsAdderIntegrator();
    }
}
