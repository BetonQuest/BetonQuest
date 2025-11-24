package org.betonquest.betonquest.compatibility.holograms.decentholograms;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link DecentHologramsIntegrator} instances.
 */
public class DecentHologramsIntegratorFactory implements IntegratorFactory {
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
    public DecentHologramsIntegratorFactory(final BetonQuestLoggerFactory loggerFactory, final QuestPackageManager packManager) {
        this.loggerFactory = loggerFactory;
        this.packManager = packManager;
    }

    @Override
    public Integrator getIntegrator() {
        return new DecentHologramsIntegrator(loggerFactory.create(DecentHologramsIntegrator.class), packManager);
    }
}
