package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link HolographicDisplaysIntegrator} instances.
 */
public class HolographicDisplaysIntegratorFactory implements IntegratorFactory {
    /**
     * Logger factory to create class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * Creates a new instance of the factory.
     *
     * @param loggerFactory the logger factory to create class specific logger
     * @param packManager   the quest package manager to get quest packages from
     */
    public HolographicDisplaysIntegratorFactory(final BetonQuestLoggerFactory loggerFactory, final QuestPackageManager packManager) {
        this.loggerFactory = loggerFactory;
        this.packManager = packManager;
    }

    @Override
    public Integrator getIntegrator() {
        return new HolographicDisplaysIntegrator(loggerFactory.create(HolographicDisplaysIntegrator.class), packManager);
    }
}
