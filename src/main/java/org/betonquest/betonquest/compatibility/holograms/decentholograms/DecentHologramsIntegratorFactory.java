package org.betonquest.betonquest.compatibility.holograms.decentholograms;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link DecentHologramsIntegrator} instances.
 */
public class DecentHologramsIntegratorFactory implements IntegratorFactory {
    /**
     * The quest package manager to use for the instruction.
     */
    private final QuestPackageManager questPackageManager;

    /**
     * Creates a new instance of the factory.
     *
     * @param questPackageManager the quest package manager to use for the instruction
     */
    public DecentHologramsIntegratorFactory(final QuestPackageManager questPackageManager) {
        this.questPackageManager = questPackageManager;
    }

    @Override
    public Integrator getIntegrator() {
        return new DecentHologramsIntegrator(questPackageManager);
    }
}
