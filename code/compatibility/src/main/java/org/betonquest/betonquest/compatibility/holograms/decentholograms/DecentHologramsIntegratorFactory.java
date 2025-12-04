package org.betonquest.betonquest.compatibility.holograms.decentholograms;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.Variables;
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
     * Variable processor to create and resolve variables.
     */
    private final Variables variables;

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * Creates a new instance of the factory.
     *
     * @param variables     the variable processor to create and resolve variables
     * @param loggerFactory the logger factory to create class specific logger
     * @param packManager   the quest package manager to get quest packages from
     */
    public DecentHologramsIntegratorFactory(final BetonQuestLoggerFactory loggerFactory, final Variables variables, final QuestPackageManager packManager) {
        this.loggerFactory = loggerFactory;
        this.variables = variables;
        this.packManager = packManager;
    }

    @Override
    public Integrator getIntegrator() {
        return new DecentHologramsIntegrator(loggerFactory.create(DecentHologramsIntegrator.class), variables, packManager);
    }
}
