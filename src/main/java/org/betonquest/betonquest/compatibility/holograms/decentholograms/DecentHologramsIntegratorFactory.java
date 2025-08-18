package org.betonquest.betonquest.compatibility.holograms.decentholograms;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link DecentHologramsIntegrator} instances.
 */
public class DecentHologramsIntegratorFactory implements IntegratorFactory {
    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * Creates a new instance of the factory.
     *
     * @param packManager the quest package manager to get quest packages from
     */
    public DecentHologramsIntegratorFactory(final QuestPackageManager packManager) {
        this.packManager = packManager;
    }

    @Override
    public Integrator getIntegrator() {
        return new DecentHologramsIntegrator(packManager);
    }
}
