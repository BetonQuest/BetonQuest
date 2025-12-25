package org.betonquest.betonquest.compatibility.packetevents;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link PacketEventsIntegrator} instances.
 */
public class PacketEventsIntegratorFactory implements IntegratorFactory {

    /**
     * Creates a new instance of the factory.
     */
    public PacketEventsIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new PacketEventsIntegrator();
    }
}
