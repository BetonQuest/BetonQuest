package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link HolographicDisplaysIntegrator} instances.
 */
public class HolographicDisplaysIntegratorFactory implements IntegratorFactory {
    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * Creates a new instance of the factory.
     *
     * @param packManager the quest package manager to get quest packages from
     */
    public HolographicDisplaysIntegratorFactory(final QuestPackageManager packManager) {
        this.packManager = packManager;
    }

    @Override
    public Integrator getIntegrator() {
        return new HolographicDisplaysIntegrator(packManager);
    }
}
