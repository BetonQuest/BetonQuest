package org.betonquest.betonquest.compatibility.holograms.decentholograms;

import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.instruction.InstructionApi;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link DecentHologramsIntegrator} instances.
 */
public class DecentHologramsIntegratorFactory implements IntegratorFactory {

    /**
     * Logger factory to create class-specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Identifier factory to create placeholder identifiers.
     */
    private final IdentifierFactory<PlaceholderIdentifier> identifierFactory;

    /**
     * Instruction api to use.
     */
    private final InstructionApi instructionApi;

    /**
     * Creates a new instance of the factory.
     *
     * @param loggerFactory     the logger factory to create class-specific logger
     * @param instructionApi    the instruction api to use
     * @param identifierFactory the identifier factory to create placeholders
     */
    public DecentHologramsIntegratorFactory(final BetonQuestLoggerFactory loggerFactory, final InstructionApi instructionApi, final IdentifierFactory<PlaceholderIdentifier> identifierFactory) {
        this.loggerFactory = loggerFactory;
        this.instructionApi = instructionApi;
        this.identifierFactory = identifierFactory;
    }

    @Override
    public Integrator getIntegrator() {
        return new DecentHologramsIntegrator(loggerFactory.create(DecentHologramsIntegrator.class), identifierFactory, instructionApi);
    }
}
